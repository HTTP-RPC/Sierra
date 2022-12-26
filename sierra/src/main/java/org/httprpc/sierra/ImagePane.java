/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.httprpc.sierra;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * Displays an image.
 */
public class ImagePane extends JComponent {
    // Image pane UI
    private class ImagePaneUI extends ComponentUI {
        @Override
        public Dimension getMinimumSize(JComponent component) {
            return new Dimension(0, 0);
        }

        @Override
        public Dimension getMaximumSize(JComponent component) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Override
        public Dimension getPreferredSize(JComponent component) {
            var imageWidth = image.getWidth(null);
            var imageHeight = image.getHeight(null);

            if (scaleToFit) {
                // TODO
                return new Dimension();
            } else {
                return new Dimension(imageWidth, imageHeight);
            }
        }

        @Override
        public int getBaseline(JComponent component, int width, int height) {
            return -1;
        }

        @Override
        public void paint(Graphics graphics, JComponent component) {
            paint((Graphics2D)graphics);
        }

        private void paint(Graphics2D graphics) {
            if (image == null) {
                return;
            }

            graphics = (Graphics2D)graphics.create();

            if (scaleToFit) {
                // TODO
            } else {
                // TODO
            }

            // TODO Respect alignment

            graphics.dispose();
        }
    }

    private Image image;

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

    private boolean scaleToFit;

    /**
     * Constructs an image pane.
     */
    public ImagePane() {
        this(null);
    }

    /**
     * Constructs an image pane.
     *
     * @param image
     * The image to display, or {@code null} for no image.
     */
    public ImagePane(Image image) {
        this(image, false);
    }

    /**
     * Constructs an image pane.
     *
     * @param image
     * The image to display, or {@code null} for no image.
     *
     * @param scaleToFit
     * {@code true} to scale the image when needed; {@code false}, otherwise.
     */
    public ImagePane(Image image, boolean scaleToFit) {
        this.image = image;
        this.scaleToFit = scaleToFit;

        setUI(new ImagePaneUI());
    }

    /**
     * Returns the image displayed by the component.
     *
     * @return
     * The image displayed by the component.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the image displayed by the component.
     *
     * @param image
     * The image to display, or {@code null} for no image.
     */
    public void setImage(Image image) {
        this.image = image;

        revalidate();
    }

    /**
     * Returns the horizontal alignment.
     *
     * @return
     * The horizontal alignment.
     */
    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Sets the horizontal alignment.
     *
     * @param horizontalAlignment
     * The horizontal alignment.
     */
    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        if (horizontalAlignment == null) {
            throw new IllegalArgumentException();
        }

        this.horizontalAlignment = horizontalAlignment;

        repaint();
    }

    /**
     * Returns the vertical alignment.
     *
     * @return
     * The vertical alignment.
     */
    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the vertical alignment.
     *
     * @param verticalAlignment
     * The vertical alignment.
     */
    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        if (verticalAlignment == null) {
            throw new IllegalArgumentException();
        }

        this.verticalAlignment = verticalAlignment;

        repaint();
    }

    /**
     * Indicates that image scaling is enabled.
     *
     * @return
     * {@code true} if the image will be scaled when needed; {@code false},
     * otherwise.
     */
    public boolean getScaleToFit() {
        return scaleToFit;
    }

    /**
     * Toggles image scaling.
     *
     * @param scaleToFit
     * {@code true} to scale the image when needed; {@code false}, otherwise.
     */
    public void setScaleToFit(boolean scaleToFit) {
        this.scaleToFit = scaleToFit;

        repaint();
    }
}
