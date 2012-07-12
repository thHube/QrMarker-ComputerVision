/**
 * Copyright (c) 2011, Alberto Franco
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the name of Alberto Franco nor the
 *      names of its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ALBERTO FRANCO BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package qrmarker;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.texture.TextureCoords;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Class containing the entry point of the program
 * @author Alberto Franco
 */
public class Main extends JFrame implements GLEventListener {

    private WebcamWidget camera = new WebcamWidget();
    private JTabbedPane  tabs = new JTabbedPane();
    private TexturedQuad quad = new TexturedQuad();
    private CameraTexture text = new CameraTexture();
    private QrDetector detect = null;

    private JButton serializeBtt = new JButton("Serialize");

    private Framebuffer gaussPass;
    private Framebuffer gradientPass;
    private Framebuffer cannyPass;
    private Framebuffer finalPass;
    private Framebuffer lastPass;

    private ShaderProgram gauss;
    private ShaderProgram gradient;
    private ShaderProgram canny;
    private ShaderProgram qrResponse;
    private ShaderProgram consense;

    private boolean serialize = false;
    private final IntBuffer lock;

    public static void main(String[] args) {
        Main app = new Main();
        app.setVisible(true);
    }

    public Main() {
        super("QR tracking");
        GLCanvas canvas = new GLCanvas();
        Dimension dim = camera.getRenderSize();

        canvas.addGLEventListener(this);
        canvas.setSize(dim);
       
        serializeBtt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                serialize = true;
            }
        });

        Box vBox = Box.createVerticalBox();
        Box hBox = Box.createHorizontalBox();
        vBox.add(canvas);
        hBox.add(serializeBtt);
        vBox.add(hBox);

        tabs.addTab("OpenGL out", vBox);
        tabs.addTab("Camera out", camera);
        add(tabs);

        lock = IntBuffer.allocate(camera.getRenderSize().height *
                camera.getRenderSize().width);

        final Animator animator = new Animator(canvas);
        // Close handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        animator.stop();
                        detect.sendFinishSignal();
                        System.exit(0);
                    }
                }).start();
            }
        });
        animator.start();
        pack();
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        gl.setSwapInterval(1);

        gl.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
        
        Dimension dim = camera.getRenderSize();

        // Init shaders
        gauss       = new ShaderProgram(gl, "plain.glsl", "gauss.glsl");
        gradient    = new ShaderProgram(gl, "plain.glsl", "gradient.glsl");
        canny       = new ShaderProgram(gl, "plain.glsl", "canny_new.glsl");
        qrResponse  = new ShaderProgram(gl, "plain.glsl", "wideqr.glsl");
        consense    = new ShaderProgram(gl, "plain.glsl", "consense.glsl");

        // Init framebuffers
        gaussPass    = new Framebuffer(gl, dim.width, dim.height);
        gradientPass = new Framebuffer(gl, dim.width, dim.height);
        cannyPass    = new Framebuffer(gl, dim.width, dim.height);
        finalPass    = new Framebuffer(gl, dim.width, dim.height);
        lastPass     = new Framebuffer(gl, dim.width, dim.height);

        // Init Buffer & create widget
        detect = new QrDetector(lock, camera, dim.width, dim.height);
        tabs.addTab("Detection", detect);
        new Thread(detect).start();

        // Set texture params
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
    }

    ByteBuffer buf = ByteBuffer.allocate(640 * 480 * 4);;
    IntBuffer intBuff;

    public void display(GLAutoDrawable drawable) {
        // Clear operations
        GL gl = drawable.getGL();
        gl.glLoadIdentity();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glCullFace(GL.GL_BACK);
        gl.glEnable(GL.GL_CULL_FACE);
        TextureCoords t = null;
        // First blur the image
        gaussPass.prepareRender(gl);
        gauss.use(gl);
        quad.draw(gl, text.bind(camera.getBufferedImage()), true);
        text.unbind();
        
        // Calculate gradient
        t = gaussPass.getBuffer(gl);
        gradientPass.prepareRender(gl);
        gradient.use(gl);
        quad.draw(gl, t, true);
        gaussPass.release();
        
        // Canny edge detection
        t = gradientPass.getBuffer(gl);
        cannyPass.prepareRender(gl);
        canny.use(gl);
        quad.draw(gl, t, true);
        gradientPass.release();
        
        // QR response calculation
        t = cannyPass.getBuffer(gl);
        finalPass.prepareRender(gl);
        qrResponse.use(gl);
        quad.draw(gl, t, true);
        cannyPass.release();

        // Consensus calculation
        t = finalPass.getBuffer(gl);
        lastPass.prepareRender(gl);
        consense.use(gl);
        quad.draw(gl, t, true);
        finalPass.release();

        // Present image
        t = lastPass.getBuffer(gl);
        gl.glUseProgram(0);
        quad.draw(gl, t, true);
        lastPass.release();

        // Copy image onto a buffer
        synchronized(lock) {
            gl.glReadPixels(0, 0, 640, 480, GL.GL_RGBA,
                    GL.GL_UNSIGNED_BYTE, lock);
        }

        // If we have to serialize
        if (serialize) {
            gaussPass.serialize("gauss.png");
            gradientPass.serialize("gradient.png");
            cannyPass.serialize("canny.png");
            finalPass.serialize("result.png");
            lastPass.serialize("consensous.png");
            serialize = false;
        }

        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
}