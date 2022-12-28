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
            columnWidths.clear();

            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);

            var preferredWidth = 0;
            var preferredHeight = 0;

            var maximumRowSpacing = 0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                if (Double.isNaN(getWeight(i))) {
                    component.setSize(width, Integer.MAX_VALUE);

                    var preferredSize = component.getPreferredSize();

                    if (alignToGrid && component instanceof RowPanel) {
                        maximumRowSpacing = Math.max(maximumRowSpacing, ((RowPanel)component).getSpacing());
                    } else {
                        preferredWidth = Math.max(preferredWidth, preferredSize.width);
                        preferredHeight += preferredSize.height;
                    }
                }
            }

            if (alignToGrid) {
                var totalColumnWidth = columnWidths.stream().reduce(0, Integer::sum);

                preferredWidth = totalColumnWidth + (columnWidths.size() - 1) * (getSpacing() + maximumRowSpacing);

                for (var i = 0; i < n; i++) {
                    var component = getComponent(i);

                    if (Double.isNaN(getWeight(i)) && component instanceof RowPanel) {
                        preferredHeight += component.getPreferredSize().height;
                    }
                }
            }

            preferredHeight += getSpacing() * (n - 1);

            return new Dimension(preferredWidth + insets.left + insets.right, preferredHeight + insets.top + insets.bottom);
        }

        @Override
        public void layoutContainer() {
            columnWidths.clear();

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

                    if (alignToGrid && component instanceof RowPanel) {
                        component.doLayout();
                    } else {
                        remainingHeight -= component.getHeight();
                    }
                } else {
                    totalWeight += weight;
                }
            }

            if (alignToGrid) {
                for (var i = 0; i < n; i++) {
                    var component = getComponent(i);

                    if (Double.isNaN(getWeight(i)) && component instanceof RowPanel) {
                        remainingHeight -= component.getPreferredSize().height;
                    }
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

    private boolean alignToGrid = false;

    private List<Integer> columnWidths = new LinkedList<>();

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

    /**
     * Returns the calculated column widths.
     *
     * @return
     * The calculated column widths.
     */
    protected List<Integer> getColumnWidths() {
        return columnWidths;
    }
}
