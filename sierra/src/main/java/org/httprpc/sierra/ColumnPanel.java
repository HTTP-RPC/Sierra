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
import java.util.LinkedList;
import java.util.List;

/**
 * Arranges sub-components vertically in a column, optionally pinning component
 * edges to the container's leading and trailing insets.
 */
public class ColumnPanel extends BoxPanel {
    // Column layout manager
    private class ColumnLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize() {
            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);

            var preferredWidth = 0;
            var preferredHeight = 0;

            var columnWidths = new LinkedList<Integer>();
            var maximumRowSpacing = 0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                component.setSize(width, Integer.MAX_VALUE);

                var preferredSize = component.getPreferredSize();

                if (alignToGrid && component instanceof RowPanel) {
                    var rowPanel = (RowPanel)component;

                    updateColumnWidths(rowPanel, columnWidths);

                    maximumRowSpacing = Math.max(maximumRowSpacing, rowPanel.getSpacing());
                } else {
                    preferredWidth = Math.max(preferredWidth, preferredSize.width);
                    preferredHeight += preferredSize.height;
                }
            }

            if (alignToGrid) {
                var totalColumnWidth = columnWidths.stream().reduce(0, Integer::sum);

                preferredWidth = Math.max(totalColumnWidth + getSpacing() * (columnWidths.size() - 1) + maximumRowSpacing, preferredWidth);

                for (var i = 0; i < n; i++) {
                    var component = getComponent(i);

                    if (component instanceof RowPanel) {
                        component.setSize(preferredWidth, Integer.MAX_VALUE);

                        preferredHeight += component.getPreferredSize().height;
                    }
                }
            }

            preferredHeight += getSpacing() * (n - 1);

            return new Dimension(preferredWidth + insets.left + insets.right, preferredHeight + insets.top + insets.bottom);
        }

        private void updateColumnWidths(RowPanel rowPanel, List<Integer> columnWidths) {
            var n = rowPanel.getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = rowPanel.getComponent(i);

                var width = component.getWidth();

                if (i == columnWidths.size()) {
                    columnWidths.add(width);
                } else {
                    columnWidths.set(i, Math.max(columnWidths.get(i), width));
                }
            }
        }

        @Override
        public void layoutContainer() {
            // TODO Add support for grid alignment

            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);

            var spacing = getSpacing();

            var totalWeight = 0.0;
            var remainingHeight = Math.max(size.height - (insets.top + insets.bottom), 0);

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (Double.isNaN(weight)) {
                    component.setSize(width, Integer.MAX_VALUE);
                    component.setSize(width, component.getPreferredSize().height);

                    remainingHeight -= component.getHeight();
                } else {
                    totalWeight += weight;
                }
            }

            remainingHeight = Math.max(0, remainingHeight - spacing * (n - 1));

            var y = insets.top;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                component.setLocation(insets.left, y);

                var weight = getWeight(i);

                if (!Double.isNaN(weight)) {
                    component.setSize(width, (int)Math.round(remainingHeight * (weight / totalWeight)));
                }

                y += component.getHeight() + spacing;
            }
        }
    }

    private boolean alignToGrid = true;

    /**
     * Constructs a new column panel.
     */
    public ColumnPanel() {
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
     * Indicates that row descendants will be vertically aligned in a grid.
     *
     * @return
     * {@code true} if row descendants will be aligned to grid; {@code false},
     * otherwise.
     */
    public boolean getAlignToGrid() {
        return alignToGrid;
    }

    /**
     * Toggles grid alignment.
     *
     * @param alignToGrid
     * {@code true} to align row descendants to grid; {@code false}, otherwise.
     */
    public void setAlignToGrid(boolean alignToGrid) {
        this.alignToGrid = alignToGrid;

        revalidate();
    }
}
