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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import static org.httprpc.kilo.util.Optionals.*;

/**
 * Displays a small amount of status information.
 */
public class Badge extends JComponent {
    private class BadgeUI extends ComponentUI {
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
            var fontRenderContext = getFontMetrics(font).getFontRenderContext();

            var stringBounds = font.getStringBounds(coalesce(text, () -> ""), fontRenderContext);

            var contentWidth = stringBounds.getWidth();
            var contentHeight = stringBounds.getHeight() * (1.0 + MARGIN * 2);

            var preferredWidth = contentWidth + contentHeight + (insets.left + insets.right);
            var preferredHeight = contentHeight + (insets.top + insets.bottom);

            return new Dimension((int)Math.ceil(preferredWidth), (int)Math.ceil(preferredHeight));
        }

        @Override
        public int getBaseline(JComponent component, int width, int height) {
            var insets = getInsets();

            var font = getFont();
            var fontRenderContext = getFontMetrics(font).getFontRenderContext();

            var lineMetrics = font.getLineMetrics(coalesce(text, () -> ""), fontRenderContext);

            var ascent = lineMetrics.getAscent();

            return (int)Math.ceil(insets.top + lineMetrics.getHeight() * MARGIN + ascent);
        }

        @Override
        public void paint(Graphics graphics, JComponent component) {
            paint((Graphics2D)graphics);
        }

        private void paint(Graphics2D graphics) {
            var width = getWidth();
            var height = getHeight();

            var preferredSize = getPreferredSize(null);

            var insets = getInsets();

            var contentWidth = preferredSize.getWidth() - (insets.left + insets.right);
            var contentHeight = preferredSize.getHeight() - (insets.top + insets.bottom);

            var x = (width - contentWidth) / 2;
            var y = (height - contentHeight) / 2;

            graphics = (Graphics2D)graphics.create();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            graphics.setColor(getBackground());

            graphics.fill(new RoundRectangle2D.Double(x, y, contentWidth, contentHeight, contentHeight, contentHeight));

            if (text != null) {
                var font = getFont();
                var fontRenderContext = getFontMetrics(font).getFontRenderContext();

                var stringBounds = font.getStringBounds(text, fontRenderContext);

                var ascent = font.getLineMetrics(text, fontRenderContext).getAscent();

                var textWidth = stringBounds.getWidth();
                var textHeight = stringBounds.getHeight();

                graphics.setColor(getForeground());
                graphics.setFont(font);

                graphics.drawString(text, (float)(width - textWidth) / 2, (float)(height - textHeight) / 2 + ascent);
            }

            if (outline != null) {
                graphics.setColor(outline);
                graphics.setStroke(new BasicStroke(OUTLINE_THICKNESS));

                graphics.draw(new RoundRectangle2D.Double(x + OUTLINE_THICKNESS / 2, y + OUTLINE_THICKNESS / 2,
                    contentWidth - OUTLINE_THICKNESS, contentHeight - OUTLINE_THICKNESS,
                    contentHeight, contentHeight));
            }

            graphics.dispose();
        }
    }

    private String text;

    private Color outline = null;

    private static final double MARGIN = 0.2;

    private static final float OUTLINE_THICKNESS = 1;

    /**
     * Constructs a new badge.
     */
    public Badge() {
        this(null);
    }

    /**
     * Constructs a new badge.
     *
     * @param text
     * The badge text, or {@code null} for no text.
     */
    public Badge(String text) {
        this.text = text;

        setForeground(UIManager.getColor("Panel.background"));
        setBackground(UIManager.getColor("Label.disabledForeground"));

        setFont(UIManager.getFont("Label.font"));

        setUI(new BadgeUI());
    }

    /**
     * Returns the badge text.
     *
     * @return
     * The badge text.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the badge text.
     *
     * @param text
     * The badge text, or {@code null} for no text.
     */
    public void setText(String text) {
        this.text = text;

        revalidate();
        repaint();
    }

    /**
     * Returns the outline color.
     *
     * @return
     * The outline color, or {@code null} if no outline color has been set.
     */
    public Color getOutline() {
        return outline;
    }

    /**
     * Sets the outline color.
     *
     * @param outline
     * The outline color, or {@code null} for no outline.
     */
    public void setOutline(Color outline) {
        this.outline = outline;
    }
}
