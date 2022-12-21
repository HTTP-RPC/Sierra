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

import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Arranges components vertically in a column, optionally pinning component
 * edges to the container's insets.
 */
public class ColumnPanel extends BoxPanel {
    private class ColumnLayoutManager extends BoxLayoutManager {
        @Override
        public Dimension preferredLayoutSize() {
            var size = getSize();
            var insets = getInsets();

            int width;
            if (getHorizontalAlignment() == HorizontalAlignment.FILL) {
                width = Math.max(size.width - (insets.left + insets.right), 0);
            } else {
                width = Integer.MAX_VALUE;
            }

            var preferredWidth = 0;
            var preferredHeight = 0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++){
                var component = getComponent(i);

                component.setSize(width, Integer.MAX_VALUE);

                var preferredSize = component.getPreferredSize();

                preferredWidth = Math.max(preferredWidth, (int)preferredSize.getWidth());
                preferredHeight += (int)preferredSize.getHeight();
            }

            return new Dimension(preferredWidth + insets.left + insets.right, preferredHeight + insets.top + insets.bottom);
        }


        @Override
        public void layoutContainer() {
            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);

            var horizontalAlignment = getHorizontalAlignment();
            var verticalAlignment = getVerticalAlignment();

            var totalWeight = 0.0;
            var remainingHeight = Math.max(size.height - (insets.top + insets.bottom), 0);

            var n = getComponentCount();

            for (var i = 0; i < n; i++){
                var component = getComponent(i);

                if (horizontalAlignment == HorizontalAlignment.FILL) {
                    component.setSize(width, Integer.MAX_VALUE);
                    component.setSize(width, component.getPreferredSize().height);
                } else {
                    component.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
                    component.setSize(component.getPreferredSize());
                }

                var weight = getWeight(component);

                if (!Double.isNaN(weight) && verticalAlignment == VerticalAlignment.FILL) {
                    totalWeight += weight;
                } else {
                    remainingHeight -= component.getHeight();
                }
            }

            remainingHeight = Math.max(0, remainingHeight);

            var y = insets.top;

            switch (verticalAlignment) {
                case BOTTOM: {
                    y += remainingHeight;
                    break;
                }

                case CENTER: {
                    y += remainingHeight / 2;
                    break;
                }
            }

            for (var i = 0; i < n; i++){
                var component = getComponent(i);

                var x = insets.left;

                switch (horizontalAlignment) {
                    case LEADING:
                    case TRAILING: {
                        if (getComponentOrientation().isLeftToRight() ^ horizontalAlignment == HorizontalAlignment.LEADING) {
                            x += width - component.getWidth();
                        }

                        break;
                    }

                    case CENTER: {
                        x += (width - component.getWidth()) / 2;
                        break;
                    }
                }

                component.setLocation(x, y);

                var weight = getWeight(component);

                if (!Double.isNaN(weight) && verticalAlignment == VerticalAlignment.FILL) {
                    component.setSize(component.getWidth(), (int)Math.round(remainingHeight * (weight / totalWeight)));
                }

                y += component.getHeight();
            }
        }
    }

    private boolean alignToGrid;

    /**
     * Constructs a new column panel.
     */
    public ColumnPanel() {
        this(HorizontalAlignment.FILL, VerticalAlignment.FILL, 4, true);
    }

    /**
     * Constructs a new row panel.
     *
     * @param horizontalAlignment
     * The horizontal alignment.
     *
     * @param verticalAlignment
     * The vertical alignment.
     *
     * @param spacing
     * The spacing value.
     *
     * @param alignToGrid
     * {@code true} to align components to grid; {@code false}, otherwise.
     */
    public ColumnPanel(HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int spacing, boolean alignToGrid) {
        super(horizontalAlignment, verticalAlignment, spacing);

        this.alignToGrid = alignToGrid;

        setLayout(new ColumnLayoutManager());
    }

    /**
     * Sets the layout manager.
     * {@inheritDoc}
     */
    @Override
    public void setLayout(LayoutManager layoutManager) {
        if (layoutManager != null && !(layoutManager instanceof ColumnLayoutManager)) {
            throw new IllegalArgumentException();
        }

        super.setLayout(layoutManager);
    }

    /**
     * Indicates that components will be aligned to grid.
     *
     * @return
     * {@code true} if components will be grid-aligned; {@code false},
     * otherwise.
     */
    public boolean getAlignToGrid() {
        return alignToGrid;
    }

    /**
     * Toggles grid alignment.
     *
     * @param alignToGrid
     * {@code true} to align components to grid; {@code false}, otherwise.
     */
    public void setAlignToGrid(boolean alignToGrid) {
        this.alignToGrid = alignToGrid;

        revalidate();
    }
}
