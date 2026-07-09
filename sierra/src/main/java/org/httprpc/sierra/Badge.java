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
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

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
            if (text == null) {
                return new Dimension(0, 0);
            }

            var insets = getInsets();

            var font = getFont();
            var fontRenderContext = getFontMetrics(font).getFontRenderContext();

            var stringBounds = font.getStringBounds(text, fontRenderContext);

            var textWidth = stringBounds.getWidth();
            var textHeight = stringBounds.getHeight() * (1.0 + MARGIN * 2);

            var preferredWidth = textWidth + textHeight + (insets.left + insets.right);
            var preferredHeight = textHeight + (insets.top + insets.bottom);

            return new Dimension((int)Math.ceil(preferredWidth), (int)Math.ceil(preferredHeight));
        }

        @Override
        public int getBaseline(JComponent component, int width, int height) {
            var insets = getInsets();

            var font = getFont();
            var fontRenderContext = getFontMetrics(font).getFontRenderContext();

            var lineMetrics = font.getLineMetrics(text, fontRenderContext);

            var ascent = lineMetrics.getAscent();

            return (int)Math.ceil(insets.top + lineMetrics.getHeight() * MARGIN + ascent);
        }

        @Override
        public void paint(Graphics graphics, JComponent component) {
            paint((Graphics2D)graphics);
        }

        private void paint(Graphics2D graphics) {
            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);
            var height = Math.max(size.height - (insets.top + insets.bottom), 0);

            graphics = (Graphics2D)graphics.create();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setColor(getBackground());

            graphics.fill(new RoundRectangle2D.Double(0, 0, width, height, height, height));

            var font = getFont();
            var fontRenderContext = getFontMetrics(font).getFontRenderContext();

            var stringBounds = font.getStringBounds(text, fontRenderContext);

            var ascent = font.getLineMetrics(text, fontRenderContext).getAscent();

            var textWidth = stringBounds.getWidth();
            var textHeight = stringBounds.getHeight();

            var x = insets.left + (width - textWidth) / 2;
            var y = insets.top + (height - textHeight) / 2 + ascent;

            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);

            graphics.setColor(getForeground());
            graphics.setFont(font);

            graphics.drawString(text, (float)x, (float)y);

            graphics.dispose();
        }
    }

    private String text;

    private static final double MARGIN = 0.1;

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
}
