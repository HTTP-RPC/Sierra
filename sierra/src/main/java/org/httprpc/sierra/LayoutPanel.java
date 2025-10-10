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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
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

    /**
     * Adds a component to the panel.
     * {@inheritDoc}
     */
    @Override
    protected void addImpl(Component component, Object constraints, int index) {
        super.addImpl(component, constraints, index);

        this.constraints.add((index == -1) ? this.constraints.size() : index, constraints);

        revalidate();
        repaint();
    }

    /**
     * Removes a component from the panel.
     * {@inheritDoc}
     */
    @Override
    public void remove(int index) {
        super.remove(index);

        constraints.remove(index);

        revalidate();
        repaint();
    }

    /**
     * Removes all components from the panel.
     * {@inheritDoc}
     */
    @Override
    public void removeAll() {
        super.removeAll();

        constraints.clear();

        revalidate();
        repaint();
    }

    /**
     * Returns the panel's preferred size.
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * Returns 10% of the visible height for vertical orientations and 10% of
     * the visible width for horizontal orientations.
     * {@inheritDoc}
     */
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (visibleRect == null) {
            throw new IllegalArgumentException();
        }

        return switch (orientation) {
            case SwingConstants.VERTICAL -> visibleRect.height / 10;
            case SwingConstants.HORIZONTAL -> visibleRect.width / 10;
            default -> throw new UnsupportedOperationException();
        };
    }

    /**
     * Returns the visible height for vertical orientations and the visible
     * width for horizontal orientations.
     * {@inheritDoc}
     */
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (visibleRect == null) {
            throw new IllegalArgumentException();
        }

        return switch (orientation) {
            case SwingConstants.VERTICAL -> visibleRect.height;
            case SwingConstants.HORIZONTAL -> visibleRect.width;
            default -> throw new UnsupportedOperationException();
        };
    }

    /**
     * Indicates that the panel tracks viewport width.
     * {@inheritDoc}
     */
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

    /**
     * Indicates that the panel tracks viewport height.
     * {@inheritDoc}
     */
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
