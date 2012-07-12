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

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.media.opengl.GL;

/**
 * Framebuffer object. Render to texture. Render to
 * texture is available through instance of this class.
 * @author Alberto Franco
 */
public final class Framebuffer {

    private Texture texture;

    private static int[] frame = null;
    private int width, height;
    
    private static final int SIDE = 1024;

    /**
     * Public constructor. Setup the OpenGL context for frame buffer use.
     * @param gl OpenGL context.
     * @param width Width of the viewport to render.
     * @param height Height of the viewport to render.
     */
    public Framebuffer(GL gl, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        texture = TextureIO.newTexture(image, true);

        this.width = width; this.height = height;
        if (frame == null) {
            frame = new int[1];
            gl.glGenFramebuffersEXT(1, frame, 0);
        }
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, frame[0]);
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
    }

    /**
     * 
     */
    public void prepareRender(GL gl) {
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, frame[0]);
        gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT,
                GL.GL_TEXTURE_2D, texture.getTextureObject(), 0);
    }
    
    /**
     * Return the coordinates of the texture.
     * @return Texture coordinates for frame buffer texture. 
     */
    public TextureCoords getBuffer(GL gl) {
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
        
        texture.enable();
        texture.bind();
        return texture.getImageTexCoords();
    }

    public void release() {
        texture.disable();
    }

    public void serialize(String filename) {
        try {
            TextureIO.write(texture, new File(filename));
        } catch(IOException ex) {
            System.err.println("Could not serialize texture! ");
            System.err.println(ex);
        }
    }
}
