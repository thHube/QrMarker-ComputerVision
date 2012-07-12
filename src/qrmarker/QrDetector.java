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

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Final stage of the detection algorithm. Draw a rectangle around the QR code
 * detected. It runs on a thread different from the one that manage OpenGL.
 *
 * @author Alberto Franco
 */
public class QrDetector extends JPanel implements Runnable {

    private final IntBuffer image;
    private BufferedImage lblImage;
    private WebcamWidget camera;

    private boolean finished = false;
    private int width, height;

    private final int X_OFFSET = 28;
    private final int Y_OFFSET = 48;

    private final int THRESHOLD = 170;

    /**
     * Public constructor of the widget. Initialize the detection subsystem.
     * @param buff Buffer of integer that comes from OpenGL.
     * @param cam Camera widget, get the camera image stream.
     * @param width Width of the camera.
     * @param height Height of the camera.
     */
    public QrDetector(IntBuffer buff, WebcamWidget cam, int width, int height) {
        image = buff;
        lblImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        camera = cam;

        this.width = width;
        this.height = height;

        JLabel label = new JLabel(new ImageIcon(lblImage));
        add(label);
    }

    void sendFinishSignal() {
        finished = true;
    }

    @Override
    public void run() {

        int[] imageData;
        int currentPixel, startX = -1, startY = -1, endX = -1, endY = -1;
        BufferedImage img;

        while(!finished) {
            img = camera.getBufferedImage();
            
            synchronized(image) { // -------------------------------------------
                imageData = image.array();

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        lblImage.setRGB(x, y, img.getRGB(x, y));
                        currentPixel = imageData[x + y * width];
                        // If pixel is greater than our threshold.
                        if (((currentPixel & 0xFF00) >> 8) > THRESHOLD) {
                            // Init start & end
                            if (startX == -1) {
                                endX = startX = x;
                                endY = startY = y;
                            }

                            // Update start & end
                            if (startX > x) startX = x;
                            if (startY > y) startY = y;
                            if (endX < x) endX = x;
                            if (endY < y) endY = y;
                        } 
                    }
                }
            } // end synchronized ----------------------------------------------
            
            if (startX > X_OFFSET) startX = startX - X_OFFSET;
            if (endX > X_OFFSET) endX = endX - X_OFFSET;
            
            if (startY > Y_OFFSET)startY = startY - Y_OFFSET;
            if (endY > Y_OFFSET) endY = endY - Y_OFFSET;

            drawRect(startX, startY, endX, endY, 0xFFFF00FF);
            startX = startY = endX = endY = -1;
            repaint();
        }
    }

    private boolean checkMaximal(int[] image, int xPos, int yPos, int windowSize) {
        int maxPixel = (image[xPos + yPos * width] & 0xFF00) >> 8;
        int currPixel;
        for (int y = yPos - windowSize; y < yPos + windowSize; y++) {
            for (int x = xPos - windowSize; x < xPos + windowSize; x++) {
                currPixel = (image[x + y * width] & 0xFF00) >> 8;
                if (maxPixel < currPixel) {
                    return false;
                }
            }
        }
        return true;
    }

    private void drawRect(int sx, int sy, int ex, int ey, int color) {

        for(int x = sx; x < ex; x++) { lblImage.setRGB(x, sy, color); }
        for(int x = sx; x < ex; x++) { lblImage.setRGB(x, ey, color); }
        
        for(int y = sy; y < ey; y++) { lblImage.setRGB(sx, y, color); }
        for(int y = sy; y < ey; y++) { lblImage.setRGB(ex, y, color); }
    }

}
