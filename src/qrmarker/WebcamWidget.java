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

import de.humatic.dsj.DSCapture;
import de.humatic.dsj.DSFilterInfo;
import de.humatic.dsj.DSFiltergraph;
import de.humatic.dsj.DSJUtils;
import de.humatic.dsj.SwingMovieController;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.JPanel;

/**
 * Web cam panel. Capture images from the web cam and print it onto a
 * panel.
 * @author Alberto Franco
 */
public class WebcamWidget extends JPanel implements PropertyChangeListener {

    private DSCapture graph;
    private int width, height;

    /**
     * Constructor, initialize the web cam widget. Create a web cam image and
     * a controller to stop the stream.
     */
    public WebcamWidget() {
        Box                 vertLatyout = Box.createVerticalBox();
        DSFilterInfo[][]    infoses     = DSCapture.queryDevices();

        graph = new DSCapture(DSFiltergraph.DD7, infoses[0][0], false,
                DSFilterInfo.doNotRender(), this);

        vertLatyout.add(graph.asComponent());
        vertLatyout.add(new SwingMovieController(graph));
        add(vertLatyout);
        width  = graph.getDisplaySize().width;
        height = graph.getDisplaySize().height;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        switch(DSJUtils.getEventType(evt)) {

        }
    }

    /**
     * Return web cam stream size.
     * @return Width x Height of the web cam stream.
     */
    public Dimension getRenderSize() {
        return graph.getDisplaySize();
    }

    /**
     * Returns web cam image.
     * @return Web cam stream image. As BufferedImage.
     */
    public BufferedImage getBufferedImage() {
        return graph.getImage();
    }
}
