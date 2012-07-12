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

import com.sun.opengl.util.texture.TextureCoords;
import javax.media.opengl.GL;

/**
 * A texture quad is a square with a texture on it.
 * @author Alberto Franco
 */
public class TexturedQuad {

    /**
     * Draw the quad into the curret openGL context
     * @param gl OpenGL context 
     * @param coords Coordinates 
     * @param flip If has to flip the image.
     */
    public void draw(GL gl, TextureCoords coords, boolean flip) {
        gl.glBegin(GL.GL_QUADS);
            if (flip) {
                gl.glTexCoord2f(coords.left(), 1.0f);
                gl.glVertex3f(-1.0f, 1.0f, 0.0f);  // Top Left
                gl.glTexCoord2f(coords.left(), 0.0f);
                gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom Left
                gl.glTexCoord2f(coords.right(), 0.0f);
                gl.glVertex3f(1.0f, -1.0f, 0.0f);  // Bottom Right
                gl.glTexCoord2f(coords.right(), 1.0f);
                gl.glVertex3f(1.0f, 1.0f, 0.0f);   // Top Right
            }
        // Done Drawing The Quad
        gl.glEnd();
    }

}
