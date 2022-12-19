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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * Abstract base class for layout panels.
 */
public abstract class LayoutPanel extends JPanel {
    /**
     * Abstract base class for layout managers.
     */
    protected abstract static class AbstractLayoutManager implements LayoutManager2 {
        /**
         * Does nothing.
         * {@inheritDoc}
         */
        @Override
        public void addLayoutComponent(String name, Component component) {
            // No-op
        }

        /**
         * Does nothing.
         * {@inheritDoc}
         */
        @Override
        public void addLayoutComponent(Component component, Object constraints) {
            // No-op
        }

        /**
         * Does nothing.
         * {@inheritDoc}
         */
        @Override
        public void removeLayoutComponent(Component component) {
            // No-op
        }

        /**
         * Does nothing.
         * {@inheritDoc}
         */
        @Override
        public void invalidateLayout(Container container) {
            // No-op
        }

        /**
         * Returns 0.
         * {@inheritDoc}
         */
        @Override
        public float getLayoutAlignmentX(Container container) {
            return 0;
        }

        /**
         * Returns 0.
         * {@inheritDoc}
         */
        @Override
        public float getLayoutAlignmentY(Container container) {
            return 0;
        }

        /**
         * Returns 0, 0.
         * {@inheritDoc}
         */
        @Override
        public Dimension minimumLayoutSize(Container container) {
            return new Dimension(0, 0);
        }

        /**
         * Returns {@link Integer#MAX_VALUE}, {@link Integer#MAX_VALUE}).
         * {@inheritDoc}
         */
        @Override
        public Dimension maximumLayoutSize(Container container) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    }

    private boolean ignoreInvalidate = false;

    /**
     * Constructs a new layout panel.
     */
    protected LayoutPanel() {
        super(null);

        setOpaque(false);
    }

    /**
     * Return's the panel's preferred size.
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        ignoreInvalidate = true;

        var preferredSize = super.getPreferredSize();

        ignoreInvalidate = false;

        return preferredSize;
    }

    /**
     * Invalidates the panel.
     * {@inheritDoc}
     */
    @Override
    public void invalidate() {
        if (ignoreInvalidate) {
            return;
        }

        super.invalidate();
    }
}
