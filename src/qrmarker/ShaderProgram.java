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

import com.sun.opengl.util.BufferUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * Class holding shader programs.
 * @author Alberto Franco
 */
public class ShaderProgram {

    private int program;

    /**
     * Public constructor for ShaderProgram class
     * @param vsFilename Vertex shader source file name
     * @param fsFilename Pixel (fragment) shader source file name
     */
    public ShaderProgram(GL gl, String vsFilename, String fsFilename) {
        try {
            int vs = compileShader(gl, vsFilename, GL.GL_VERTEX_SHADER);
            int fs = compileShader(gl, fsFilename, GL.GL_FRAGMENT_SHADER);
            linkProgram(gl, vs, fs);
        } catch(IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Enable the program onto the current pipeline.
     * @param gl OpenGL context to overwrite.
     */
    public void use(GL gl) {
        gl.glUseProgram(program);
    }

    /**
     * Set the position of the given uniform
     * @param gl OpenGL context
     * @param var The uniform to set
     * @param value The value to set
     */
    public void setUniform(GL gl, String var, float value) {
        GLU glu = new GLU();
        int uniformLocation = gl.glGetUniformLocationARB(program, var);
        if (uniformLocation == -1) { System.err.println("Error in retreving " + var);}
        gl.glUniform1f(uniformLocation, value);

        int err = gl.glGetError();
        if (err != gl.GL_NO_ERROR) {
            System.err.println("Error in setting " + var);
            System.err.println(glu.gluErrorString(err));
        }
    }

    private int compileShader(GL gl, String filename, int type)
            throws IOException {
        int s = gl.glCreateShader(type);
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String[] source = new String[1];
        source[0] = "";
        String line;
        while ((line = br.readLine()) != null) {
            source[0] += line + "\n";
        }
        gl.glShaderSource(s, 1, source, null);
        gl.glCompileShader(s);
        printShaderInfoLog(gl, s, filename);
        return s;
    }

    private void linkProgram(GL gl, int vs, int fs) {
        program = gl.glCreateProgram();
        gl.glAttachShader(program, vs);
        gl.glAttachShader(program, fs);
        gl.glLinkProgram(program);
        gl.glValidateProgram(program);
    }

    private void printShaderInfoLog(GL gl, int shader, String filename) {
        IntBuffer iVal = BufferUtil.newIntBuffer(1);
        gl.glGetShaderiv(shader, GL.GL_INFO_LOG_LENGTH, iVal);
        int length = iVal.get();
        if (length <= 1) {
            return;
        }

        ByteBuffer infoLog = BufferUtil.newByteBuffer(length);
        iVal.flip();
        gl.glGetInfoLogARB(shader, length, iVal, infoLog);
        byte[] infoBytes = new byte[length];
        infoLog.get(infoBytes);
        System.err.println("Shader Error (" + filename + "): " + new String(infoBytes));
    }
}
