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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Displays an image.
 */
public class ImagePane extends JComponent {
    private Image image;

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

    private boolean scaleToFit = true;

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
        this.image = image;
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

    /**
     * Returns 0, 0.
     * {@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }

    /**
     * Returns {@link Integer#MAX_VALUE}, {@link Integer#MAX_VALUE}.
     * {@inheritDoc}
     */
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the image pane's preferred size.
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        // TODO Return image size
        return new Dimension();
    }

    /**
     * Returns -1.
     * {@inheritDoc}
     */
    @Override
    public int getBaseline(int width, int height) {
        return -1;
    }

    /**
     * Paints the image pane.
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        // TODO Don't make permanent changes to the GC
    }
}
