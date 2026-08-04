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

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Displays a small amount of status information.
 */
public class Badge extends JLabel {
    private class BadgeUI extends BasicLabelUI {
        @Override
        public void paint(Graphics graphics, JComponent component) {
            paintBackground((Graphics2D)graphics);

            super.paint(graphics, component);
        }

        void paintBackground(Graphics2D graphics) {
            var insets = getInsets();

            var width = getWidth();

            var height = Math.max(getHeight() - (insets.top + insets.bottom), 0);

            var font = getFont();
            var fontRenderContext = getFontMetrics(font).getFontRenderContext();

            var textHeight = font.getLineMetrics("", fontRenderContext).getHeight();

            var arc = Math.min(textHeight * (1.0 + MARGIN * 2), width);

            var y = (height - arc) / 2 + insets.top;

            graphics = (Graphics2D)graphics.create();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            graphics.setColor(getBackground());

            graphics.fill(new RoundRectangle2D.Double(0, y, width, arc, arc, arc));

            if (outline != null) {
                graphics.setColor(outline);
                graphics.setStroke(new BasicStroke(OUTLINE_THICKNESS));

                graphics.draw(new RoundRectangle2D.Double(OUTLINE_THICKNESS / 2, y + OUTLINE_THICKNESS / 2,
                    width - OUTLINE_THICKNESS, arc - OUTLINE_THICKNESS,
                    arc, arc));
            }

            graphics.dispose();
        }
    }

    private class BadgeBorder implements Border {
        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            // No-op
        }

        @Override
        public Insets getBorderInsets(Component component) {
            var font = getFont();
            var fontRenderContext = getFontMetrics(font).getFontRenderContext();

            var textHeight = font.getLineMetrics("", fontRenderContext).getHeight();

            var vertical = (int)Math.ceil(textHeight * MARGIN);
            var horizontal = vertical * 2;

            return new Insets(vertical, horizontal, vertical, horizontal);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private Color outline = null;

    private static final double MARGIN = 0.2;

    private static final float OUTLINE_THICKNESS = 1;

    /**
     * Constructs a new badge.
     */
    public Badge() {
        setUI(new BadgeUI());
        setBorder(new BadgeBorder());

        setForeground(UIManager.getColor("Panel.background"));
        setBackground(UIManager.getColor("Label.disabledForeground"));

        setHorizontalAlignment(CENTER);
    }

    /**
     * Constructs a new badge.
     *
     * @param text
     * The badge text, or {@code null} for no text.
     */
    public Badge(String text) {
        this(text, null);
    }

    /**
     * Constructs a new badge.
     *
     * @param text
     * The badge text, or {@code null} for no text.
     *
     * @param icon
     * The badge icon, or {@code null} for no icon.
     */
    public Badge(String text, Icon icon) {
        this();

        setText(text);
        setIcon(icon);
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);

        if (icon instanceof FlatSVGIcon flatSVGIcon) {
            flatSVGIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> getForeground()));
        }
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
