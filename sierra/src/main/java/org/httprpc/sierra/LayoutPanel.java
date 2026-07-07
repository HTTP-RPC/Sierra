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

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for layout panels.
 */
public abstract class LayoutPanel extends JPanel implements Scrollable {
    abstract static class AbstractLayoutManager implements LayoutManager2 {
        @Override
        public void addLayoutComponent(String name, Component component) {
            // No-op
        }

        @Override
        public void addLayoutComponent(Component component, Object constraints) {
            // No-op
        }

        @Override
        public void removeLayoutComponent(Component component) {
            // No-op
        }

        @Override
        public void invalidateLayout(Container container) {
            // No-op
        }

        @Override
        public float getLayoutAlignmentX(Container container) {
            return 0;
        }

        @Override
        public float getLayoutAlignmentY(Container container) {
            return 0;
        }

        @Override
        public Dimension minimumLayoutSize(Container container) {
            return new Dimension(0, 0);
        }

        @Override
        public Dimension maximumLayoutSize(Container container) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    }

    private List<Object> constraints = new ArrayList<>();

    private boolean scrollableTracksViewportWidth = false;
    private boolean scrollableTracksViewportHeight = false;

    LayoutPanel() {
        super(null, false);

        setOpaque(false);

        setAlignmentX(0.5f);
        setAlignmentY(0.5f);
    }

    Object getConstraints(int index) {
        return constraints.get(index);
    }

    @Override
    protected void addImpl(Component component, Object constraints, int index) {
        super.addImpl(component, constraints, index);

        this.constraints.add((index == -1) ? this.constraints.size() : index, constraints);

        revalidate();
        repaint();
    }

    @Override
    public void remove(int index) {
        super.remove(index);

        constraints.remove(index);

        revalidate();
        repaint();
    }

    @Override
    public void removeAll() {
        super.removeAll();

        constraints.clear();

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        paintComponent((Graphics2D)graphics);
    }

    private void paintComponent(Graphics2D graphics) {
        if (isOpaque()
            && getBorder() instanceof CompoundBorder compoundBorder
            && compoundBorder.getOutsideBorder() instanceof UILoader.RoundedLineBorder roundedLineBorder) {
            var width = getWidth();
            var height = getHeight();

            var thickness = roundedLineBorder.getStroke().getLineWidth();
            var cornerRadius = roundedLineBorder.getCornerRadius();

            graphics = (Graphics2D)graphics.create();

            graphics.setPaint(getParent().getBackground());
            graphics.setStroke(new BasicStroke((float)Math.ceil(cornerRadius * (Math.sqrt(2) - 1))));

            graphics.drawRect(0, 0, width, height);

            graphics.setPaint(getBackground());

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.fill(new RoundRectangle2D.Double(thickness / 2.0, thickness / 2.0,
                width - thickness, height - thickness,
                cornerRadius, cornerRadius));

            graphics.dispose();
        } else {
            super.paintComponent(graphics);
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return getScrollableBlockIncrement(visibleRect, orientation, direction) / 4;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        var size = getSize();

        return switch (orientation) {
            case SwingConstants.VERTICAL -> size.height / 10;
            case SwingConstants.HORIZONTAL -> size.width / 10;
            default -> throw new UnsupportedOperationException();
        };
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return scrollableTracksViewportWidth;
    }

    /**
     * Toggles viewport width tracking.
     *
     * @param scrollableTracksViewportWidth
     * {@code true} to enable viewport width tracking; {@code false} to disable
     * it.
     */
    public void setScrollableTracksViewportWidth(boolean scrollableTracksViewportWidth) {
        this.scrollableTracksViewportWidth = scrollableTracksViewportWidth;

        revalidate();
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return scrollableTracksViewportHeight;
    }

    /**
     * Toggles viewport height tracking.
     *
     * @param scrollableTracksViewportHeight
     * {@code true} to enable viewport height tracking; {@code false} to
     * disable it.
     */
    public void setScrollableTracksViewportHeight(boolean scrollableTracksViewportHeight) {
        this.scrollableTracksViewportHeight = scrollableTracksViewportHeight;

        revalidate();
    }
}
