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
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Displays an image.
 */
public class ImagePane extends JComponent {
    /**
     * Image scaling options.
     */
    public enum ScaleMode {
        /**
         * No scaling.
         */
        NONE,

        /**
         * Image will be scaled to match the width of the image pane.
         */
        FILL_WIDTH,

        /**
         * Image will be scaled to match the height of the image pane.
         */
        FILL_HEIGHT
    }

    /**
     * Interpolation options.
     */
    public enum InterpolationMode {
        /**
         * Nearest neighbor.
         */
        NEAREST_NEIGHBOR,

        /**
         * Bilinear.
         */
        BILINEAR,

        /**
         * Bicubic.
         */
        BICUBIC
    }

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
            if (image == null) {
                return new Dimension(0, 0);
            }

            var insets = getInsets();

            var imageWidth = image.getWidth(null);
            var imageHeight = image.getHeight(null);

            if (scaleMode == ScaleMode.NONE) {
                var preferredWidth = imageWidth + (insets.left + insets.right);
                var preferredHeight = imageHeight + (insets.top + insets.bottom);

                return new Dimension(preferredWidth, preferredHeight);
            } else {
                var width = Math.max(getWidth() - (insets.left + insets.right), 0);
                var height = Math.max(getHeight() - (insets.top + insets.bottom), 0);

                var scale = getScale(width, height, imageWidth, imageHeight);

                var preferredWidth = scale * imageWidth + (insets.left + insets.right);
                var preferredHeight = scale * imageHeight + (insets.top + insets.bottom);

                return new Dimension((int)Math.floor(preferredWidth), (int)Math.floor(preferredHeight));
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

            drawImage(graphics);

            if (cornerRadius > 0) {
                drawMask(graphics);
            }
        }

        private void drawImage(Graphics2D graphics) {
            var insets = getInsets();

            var width = Math.max(getWidth() - (insets.left + insets.right), 0);
            var height = Math.max(getHeight() - (insets.top + insets.bottom), 0);

            var imageWidth = image.getWidth(null);
            var imageHeight = image.getHeight(null);

            var scale = getScale(width, height, imageWidth, imageHeight);

            var scaledImageWidth = scale * imageWidth;
            var scaledImageHeight = scale * imageHeight;

            var x = switch (horizontalAlignment.getLocalizedValue(ImagePane.this)) {
                case LEFT -> 0;
                case RIGHT -> width - scaledImageWidth;
                case CENTER -> (width - scaledImageWidth) / 2;
                default -> throw new UnsupportedOperationException();
            };

            var y = switch (verticalAlignment) {
                case TOP -> 0;
                case BOTTOM -> height - scaledImageHeight;
                case CENTER -> (height - scaledImageHeight) / 2;
            };

            graphics = (Graphics2D)graphics.create();

            graphics.translate(x + insets.left, y + insets.top);
            graphics.scale(scale, scale);

            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, switch (interpolationMode) {
                case NEAREST_NEIGHBOR -> RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                case BILINEAR -> RenderingHints.VALUE_INTERPOLATION_BILINEAR;
                case BICUBIC -> RenderingHints.VALUE_INTERPOLATION_BICUBIC;
            });

            graphics.drawImage(image, 0, 0, null);

            graphics.dispose();
        }

        private void drawMask(Graphics2D graphics) {
            var insets = getInsets();

            var maskEdge = (float)Math.ceil(cornerRadius * (Math.sqrt(2) - 1));

            if (insets.top < maskEdge
                || insets.left < maskEdge
                || insets.bottom < maskEdge
                || insets.right < maskEdge) {
                var clipBounds = graphics.getClipBounds();

                var width = getWidth();
                var height = getHeight();

                if (clipBounds.x < maskEdge
                    || clipBounds.y < maskEdge
                    || clipBounds.x + clipBounds.width > width - maskEdge
                    || clipBounds.y + clipBounds.height > height - maskEdge) {
                    var transform = graphics.getTransform();

                    var scaleX = transform.getScaleX();
                    var scaleY = transform.getScaleY();

                    var maskWidth = (int)Math.round(clipBounds.width * scaleX);
                    var maskHeight = (int)Math.round(clipBounds.height * scaleY);

                    var maskImage = new BufferedImage(maskWidth, maskHeight, BufferedImage.TYPE_INT_ARGB);

                    var maskGraphics = maskImage.createGraphics();

                    maskGraphics.scale(scaleX, scaleY);
                    maskGraphics.translate(-clipBounds.x, -clipBounds.y);

                    maskGraphics.setColor(getOpaqueBackground(getParent()));
                    maskGraphics.setStroke(new BasicStroke(maskEdge));

                    maskGraphics.draw(new Rectangle2D.Double(maskEdge / 2, maskEdge / 2, width - maskEdge, height - maskEdge));

                    maskGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    maskGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

                    maskGraphics.setComposite(AlphaComposite.Clear);

                    var arc = cornerRadius * 2;

                    maskGraphics.fill(new RoundRectangle2D.Double(0, 0, width, height, arc, arc));

                    maskGraphics.dispose();

                    graphics = (Graphics2D)graphics.create();

                    graphics.translate(clipBounds.x, clipBounds.y);
                    graphics.scale(1 / scaleX, 1 / scaleY);

                    graphics.drawImage(maskImage, 0, 0, null);

                    graphics.dispose();
                }
            }
        }

        private double getScale(int width, int height, int imageWidth, int imageHeight) {
            return switch (scaleMode) {
                case NONE -> 1.0;
                case FILL_WIDTH -> imageWidth > 0 ? (double)width / imageWidth : 1.0;
                case FILL_HEIGHT -> imageHeight > 0 ? (double)height / imageHeight : 1.0;
            };
        }

        private static Color getOpaqueBackground(Component component) {
            if (component == null) {
                return null;
            } else if (component.isOpaque()) {
                return component.getBackground();
            } else {
                return getOpaqueBackground(component.getParent());
            }
        }
    }

    private Image image;

    private ScaleMode scaleMode = ScaleMode.NONE;
    private InterpolationMode interpolationMode = InterpolationMode.BILINEAR;

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

    private int cornerRadius = 0;

    /**
     * Constructs a new image pane.
     */
    public ImagePane() {
        this(null);
    }

    /**
     * Constructs a new image pane.
     *
     * @param image
     * The image to display, or {@code null} for no image.
     */
    public ImagePane(Image image) {
        this.image = image;

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
        repaint();
    }

    /**
     * Returns the scale mode. The default value is {@link ScaleMode#NONE}.
     *
     * @return
     * The scale mode.
     */
    public ScaleMode getScaleMode() {
        return scaleMode;
    }

    /**
     * Sets the scale mode.
     *
     * @param scaleMode
     * The scale mode.
     */
    public void setScaleMode(ScaleMode scaleMode) {
        if (scaleMode == null) {
            throw new IllegalArgumentException();
        }

        this.scaleMode = scaleMode;

        revalidate();
        repaint();
    }

    /**
     * Returns the interpolation mode. The default value is
     * {@link InterpolationMode#BILINEAR}.
     *
     * @return
     * The interpolation mode.
     */
    public InterpolationMode getInterpolationMode() {
        return interpolationMode;
    }

    /**
     * Sets the interpolation mode.
     *
     * @param interpolationMode
     * The interpolation mode.
     */
    public void setInterpolationMode(InterpolationMode interpolationMode) {
        if (interpolationMode == null) {
            throw new IllegalArgumentException();
        }

        this.interpolationMode = interpolationMode;
    }

    /**
     * Returns the horizontal alignment. The default value is
     * {@link HorizontalAlignment#CENTER}.
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
     * Returns the vertical alignment. The default value is
     * {@link VerticalAlignment#CENTER}.
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
     * Returns the corner radius.
     *
     * @return
     * The corner radius.
     */
    public int getCornerRadius() {
        return cornerRadius;
    }

    /**
     * Sets the corner radius.
     *
     * @param cornerRadius
     * The corner radius.
     */
    public void setCornerRadius(int cornerRadius) {
        if (cornerRadius < 0) {
            throw new IllegalArgumentException();
        }

        this.cornerRadius = cornerRadius;
    }
}
