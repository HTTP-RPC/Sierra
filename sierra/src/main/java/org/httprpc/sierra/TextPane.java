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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
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
            if (text == null) {
                return new Dimension(0, 0);
            }

            var insets = getInsets();

            var font = getFont();

            double textWidth;
            double textHeight;
            if (wrapText) {
                var lineHeight = font.getLineMetrics("", fontRenderContext).getHeight();

                textWidth = 0.0;
                textHeight = lineHeight;

                var width = Math.max(getWidth() - (insets.left + insets.right), 0);

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

                textWidth = Math.max(lineWidth, textWidth);
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
            if (text == null || verticalAlignment == VerticalAlignment.CENTER) {
                return -1;
            }

            var insets = getInsets();

            var lineMetrics = getFont().getLineMetrics("", fontRenderContext);

            var ascent = lineMetrics.getAscent();

            switch (verticalAlignment) {
                case TOP: {
                    return insets.top + Math.round(ascent);
                }

                case BOTTOM: {
                    var lineHeight = lineMetrics.getHeight();

                    return height - (insets.bottom + Math.round(lineHeight - ascent));
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
            if (glyphVectors.isEmpty()) {
                return;
            }

            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);
            var height = Math.max(size.height - (insets.top + insets.bottom), 0);

            var font = getFont();

            var ascent = font.getLineMetrics("", fontRenderContext).getAscent();

            double y;
            switch (verticalAlignment) {
                case TOP: {
                    y = insets.top;
                    break;
                }

                case BOTTOM: {
                    y = size.height - (textHeight + insets.bottom);
                    break;
                }

                case CENTER: {
                    y = insets.top + (height - textHeight) / 2;
                    break;
                }

                default: {
                    throw new UnsupportedOperationException();
                }
            }

            graphics = (Graphics2D)graphics.create();

            graphics.setClip(insets.left, insets.top, width, height);

            graphics.setColor(getForeground());
            graphics.setFont(font);

            var n = glyphVectors.size();

            for (var i = 0; i < n; i++) {
                var glyphVector = glyphVectors.get(i);

                var textBounds = glyphVector.getLogicalBounds();

                var lineWidth = textBounds.getWidth();

                double x;
                switch (horizontalAlignment) {
                    case LEADING:
                    case TRAILING: {
                        if (getComponentOrientation().isLeftToRight() ^ horizontalAlignment == HorizontalAlignment.TRAILING) {
                            x = insets.left;
                        } else {
                            x = size.width - (lineWidth + insets.right);
                        }

                        break;
                    }

                    case CENTER: {
                        x = insets.left + (width - lineWidth) / 2;
                        break;
                    }

                    default: {
                        throw new UnsupportedOperationException();
                    }
                }

                graphics.drawGlyphVector(glyphVector, (float)x, (float)y + ascent);

                y += textBounds.getHeight();
            }

            graphics.dispose();
        }
    }

    private String text;

    private boolean wrapText = false;

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEADING;
    private VerticalAlignment verticalAlignment = VerticalAlignment.TOP;

    private List<GlyphVector> glyphVectors = new ArrayList<>();
    private double textHeight = 0.0;

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
        this.text = text;

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
     * Indicates that line wrapping is enabled. The default value is
     * {@code false}.
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

    /**
     * Returns the horizontal alignment. The default value is
     * {@link HorizontalAlignment#LEADING}.
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
     * {@link VerticalAlignment#TOP}.
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
     * Lays out the text pane.
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        glyphVectors.clear();

        textHeight = 0.0;

        if (text != null && !text.isEmpty()) {
            var insets = getInsets();

            var width = Math.max(getWidth() - (insets.left + insets.right), 0);

            var font = getFont();

            if (wrapText) {
                var n = text.length();

                var i = 0;
                var start = 0;
                var lineWidth = 0.0;
                var lastWhitespaceIndex = -1;

                while (i < n) {
                    var c = text.charAt(i);

                    if (Character.isWhitespace(c)) {
                        lastWhitespaceIndex = i;
                    }

                    lineWidth += font.getStringBounds(text, i, i + 1, fontRenderContext).getWidth();

                    if (lineWidth > width && lastWhitespaceIndex != -1) {
                        appendLine(font, start, lastWhitespaceIndex);

                        i = lastWhitespaceIndex;
                        start = i + 1;
                        lineWidth = 0.0;
                        lastWhitespaceIndex = -1;
                    }

                    i++;
                }

                appendLine(font, start, i);
            } else {
                appendLine(font, 0, text.length());
            }
        }
    }

    private void appendLine(Font font, int start, int end) {
        var glyphVector = font.createGlyphVector(fontRenderContext, new StringCharacterIterator(text, start, end, start));

        glyphVectors.add(glyphVector);

        textHeight += glyphVector.getLogicalBounds().getHeight();
    }
}
