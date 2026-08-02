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
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays a string of text.
 */
public class TextPane extends JComponent {
    private class TextPaneUI extends ComponentUI {
        @Override
        public Dimension getPreferredSize(JComponent component) {
            doLayout();

            var insets = getInsets();

            var textWidth = 0.0;

            for (var glyphVector : glyphVectors) {
                textWidth = Math.max(textWidth, glyphVector.getLogicalBounds().getWidth());
            }

            var font = getFont();
            var fontRenderContext = getFontMetrics(font).getFontRenderContext();

            var textHeight = glyphVectors.size() * font.getLineMetrics("", fontRenderContext).getHeight();

            var preferredWidth = textWidth + (insets.left + insets.right);
            var preferredHeight = textHeight + (insets.top + insets.bottom);

            return new Dimension((int)Math.ceil(preferredWidth), (int)Math.ceil(preferredHeight));
        }

        @Override
        public void paint(Graphics graphics, JComponent component) {
            paint((Graphics2D)graphics);
        }

        void paint(Graphics2D graphics) {
            if (glyphVectors.isEmpty()) {
                return;
            }

            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);
            var height = Math.max(size.height - (insets.top + insets.bottom), 0);

            var font = getFont();
            var fontRenderContext = getFontMetrics(font).getFontRenderContext();

            var lineMetrics = font.getLineMetrics("", fontRenderContext);

            var lineHeight = lineMetrics.getHeight();

            var textHeight = glyphVectors.size() * lineHeight;

            var ascent = lineMetrics.getAscent();

            var y = switch (verticalAlignment) {
                case TOP -> insets.top;
                case BOTTOM -> size.height - (textHeight + insets.bottom);
                case CENTER -> insets.top + (height - textHeight) / 2;
            };

            graphics = (Graphics2D)graphics.create();

            graphics.setColor(getForeground());
            graphics.setFont(font);

            var n = glyphVectors.size();

            for (var i = 0; i < n; i++) {
                var glyphVector = glyphVectors.get(i);

                var lineWidth = glyphVector.getLogicalBounds().getWidth();

                var x = switch (horizontalAlignment.getLocalizedValue(TextPane.this)) {
                    case LEFT -> insets.left;
                    case RIGHT -> size.width - (lineWidth + insets.right);
                    case CENTER -> insets.left + (width - lineWidth) / 2;
                    default -> throw new UnsupportedOperationException();
                };

                graphics.drawGlyphVector(glyphVector, (float)x, y + ascent);

                y += lineHeight;
            }

            graphics.dispose();
        }
    }

    private String text;

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEADING;
    private VerticalAlignment verticalAlignment = VerticalAlignment.TOP;

    private List<GlyphVector> glyphVectors = new ArrayList<>();

    /**
     * Constructs a new text pane.
     */
    public TextPane() {
        setUI(new TextPaneUI());

        setFont(UIManager.getFont("Label.font"));
    }

    /**
     * Constructs a new text pane.
     *
     * @param text
     * The text to display, or {@code null} for no text.
     */
    public TextPane(String text) {
        this();

        setText(text);
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
        repaint();
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

    @Override
    public void doLayout() {
        glyphVectors.clear();

        if (text == null) {
            return;
        }

        var insets = getInsets();

        var width = Math.max(getWidth() - (insets.left + insets.right), 0);

        var font = getFont();
        var fontRenderContext = getFontMetrics(font).getFontRenderContext();

        if (width == 0) {
            glyphVectors.add(font.createGlyphVector(fontRenderContext, text));
        } else {
            var n = text.length();

            var i = 0;
            var j = 0;

            var k = -1;

            var lineWidth = 0.0;

            while (i < n) {
                var c = text.charAt(i);

                if (Character.isWhitespace(c)) {
                    k = i;
                }

                lineWidth += font.getStringBounds(text, i, i + 1, fontRenderContext).getWidth();

                if (lineWidth > width && k != -1) {
                    glyphVectors.add(font.createGlyphVector(fontRenderContext, new StringCharacterIterator(text, j, k, j)));

                    k++;

                    i = k;
                    j = k;

                    k = -1;

                    lineWidth = 0.0;
                } else {
                    i++;
                }
            }

            glyphVectors.add(font.createGlyphVector(fontRenderContext, new StringCharacterIterator(text, j, i, j)));
        }
    }
}
