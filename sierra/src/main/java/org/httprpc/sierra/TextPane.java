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
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.util.Map;

/**
 * Displays a string of text.
 */
public class TextPane extends JComponent {
    // Text pane UI
    private class TextPaneUI extends ComponentUI {
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
            var insets = getInsets();

            var font = getFont();

            var text = (TextPane.this.text == null) ? "" : TextPane.this.text;

            double textWidth;
            double textHeight;
            if (wrapText) {
                textWidth = 0.0;
                textHeight = 0.0;

                var width = Math.max(getWidth() - (insets.left + insets.right), 0);

                if (width > 0) {
                    var lineHeight = font.getLineMetrics("", fontRenderContext).getHeight();

                    var lineWidth = 0.0;
                    var lastWhitespaceIndex = -1;

                    var n = text.length();
                    var i = 0;

                    while (i < n) {
                        var c = text.charAt(i);

                        if (Character.isWhitespace(c)) {
                            lastWhitespaceIndex = i;
                        }

                        var characterBounds = font.getStringBounds(text, i, i + 1, fontRenderContext);

                        lineWidth += characterBounds.getWidth();

                        if (lineWidth > width && lastWhitespaceIndex != -1) {
                            textWidth = Math.max(lineWidth, textWidth);
                            textHeight += lineHeight;

                            i = lastWhitespaceIndex;

                            lineWidth = 0.0;
                            lastWhitespaceIndex = -1;
                        }

                        i++;
                    }
                }
            } else {
                var stringBounds = font.getStringBounds(text, 0, text.length(), fontRenderContext);

                textWidth = stringBounds.getWidth();
                textHeight = stringBounds.getHeight();
            }

            var preferredWidth = textWidth + (insets.left + insets.right);
            var preferredHeight = textHeight + (insets.top + insets.bottom);

            return new Dimension((int)Math.ceil(preferredWidth), (int)Math.ceil(preferredHeight));
        }

        @Override
        public int getBaseline(JComponent component, int width, int height) {
            if (verticalAlignment == VerticalAlignment.CENTER) {
                return -1;
            }

            var insets = getInsets();

            var lineMetrics = getFont().getLineMetrics("", fontRenderContext);

            var ascent = lineMetrics.getAscent();
            var textHeight = lineMetrics.getHeight();

            switch (verticalAlignment) {
                case TOP: {
                    return insets.top + Math.round(ascent);
                }

                case BOTTOM: {
                    return height - (insets.bottom + Math.round(textHeight - ascent));
                }

                default: {
                    throw new UnsupportedOperationException();
                }
            }
        }

        @Override
        public void paint(Graphics graphics, JComponent component) {
            paint((Graphics2D)graphics);
        }

        private void paint(Graphics2D graphics) {
            if (text == null) {
                return;
            }

            graphics = (Graphics2D)graphics.create();

            // TODO Respect alignment

            graphics.dispose();
        }
    }

    private String text;

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEADING;
    private VerticalAlignment verticalAlignment = VerticalAlignment.TOP;

    private boolean wrapText;

    private static final FontRenderContext fontRenderContext;
    static {
        var fontDesktopHints = (Map<?, ?>)Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");

        Object aaHint = null;
        Object fmHint = null;
        if (fontDesktopHints != null) {
            aaHint = fontDesktopHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
            fmHint = fontDesktopHints.get(RenderingHints.KEY_FRACTIONALMETRICS);
        }

        if (aaHint == null) {
            aaHint = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
        }

        if (fmHint == null) {
            fmHint = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
        }

        fontRenderContext = new FontRenderContext(null, aaHint, fmHint);
    }

    /**
     * Constructs a text pane.
     */
    public TextPane() {
        this(null);
    }

    /**
     * Constructs a text pane.
     *
     * @param text
     * The text to display, or {@code null} for no text.
     */
    public TextPane(String text) {
        this(text, false);
    }

    /**
     * Constructs a text pane.
     *
     * @param text
     * The text to display, or {@code null} for no text.
     *
     * @param wrapText
     * {@code true} to wrap text when needed; {@code false}, otherwise.
     */
    public TextPane(String text, boolean wrapText) {
        this.text = text;
        this.wrapText = wrapText;

        setUI(new TextPaneUI());
    }

    /**
     * Returns the text displayed by the component.
     *
     * @return
     * The text displayed by the component.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text displayed by the component.
     *
     * @param text
     * The text to display, or {@code null} for no text.
     */
    public void setText(String text) {
        this.text = text;

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
     * Indicates that line wrapping is enabled.
     *
     * @return
     * {@code true} if the text will wrap when needed; {@code false},
     * otherwise.
     */
    public boolean getWrapText() {
        return wrapText;
    }

    /**
     * Toggles line wrapping.
     *
     * @param wrapText
     * {@code true} to wrap text when needed; {@code false}, otherwise.
     */
    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;

        revalidate();
    }
}
