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
 * Arranges sub-components in a vertical line. The panel's preferred width is
 * the maximum preferred width of its sub-components plus horizontal insets.
 * Preferred height is the total preferred height of all unweighted
 * sub-components plus vertical insets.
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

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                component.setSize(width, Integer.MAX_VALUE);

                if (alignToGrid && component instanceof RowPanel) {
                    component.doLayout();
                } else {
                    var preferredSize = component.getPreferredSize();

                    preferredWidth = Math.max(preferredWidth, preferredSize.width);

                    if (Double.isNaN(getWeight(i))) {
                        preferredHeight += preferredSize.height;
                    }
                }
            }

            var spacing = getSpacing();

            if (alignToGrid) {
                preferredWidth = columnWidths.stream().reduce(0, Integer::sum) + (columnWidths.size() - 1) * spacing;

                for (var i = 0; i < n; i++) {
                    var component = getComponent(i);

                    if (Double.isNaN(getWeight(i)) && component instanceof RowPanel) {
                        preferredHeight += component.getPreferredSize().height;
                    }
                }
            }

            preferredHeight += spacing * (n - 1);

            return new Dimension(preferredWidth + insets.left + insets.right, preferredHeight + insets.top + insets.bottom);
        }

        @Override
        public void layoutContainer() {
            columnWidths.clear();

            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);

            var totalWeight = 0.0;
            var excessHeight = Math.max(size.height - (insets.top + insets.bottom), 0);

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (Double.isNaN(weight)) {
                    component.setSize(width, Integer.MAX_VALUE);

                    if (alignToGrid && component instanceof RowPanel) {
                        component.doLayout();
                    } else {
                        component.setSize(width, component.getPreferredSize().height);

                        excessHeight -= component.getHeight();
                    }
                } else {
                    totalWeight += weight;
                }
            }

            if (alignToGrid) {
                for (var i = 0; i < n; i++) {
                    var component = getComponent(i);

                    if (Double.isNaN(getWeight(i)) && component instanceof RowPanel) {
                        component.setSize(component.getWidth(), component.getPreferredSize().height);

                        excessHeight -= component.getHeight();
                    }
                }
            }

            var spacing = getSpacing();

            excessHeight = Math.max(0, excessHeight - spacing * (n - 1));

            var remainingHeight = excessHeight;

            var y = insets.top;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                component.setLocation(insets.left, y);

                var weight = getWeight(i);

                if (!Double.isNaN(weight)) {
                    if (i < n - 1) {
                        var height = (int)Math.round(excessHeight * (weight / totalWeight));

                        component.setSize(width, height);

                        remainingHeight -= height;
                    } else {
                        component.setSize(width, remainingHeight);
                    }
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
     * Indicates that nested elements will be vertically aligned in a grid. The
     * default value is {@code false}.
     *
     * @return
     * {@code true} if nested elements will be aligned to grid; {@code false},
     * otherwise.
     */
    public boolean getAlignToGrid() {
        return alignToGrid;
    }

    /**
     * Toggles grid alignment.
     *
     * @param alignToGrid
     * {@code true} to enable grid alignment; {@code false} to disable it.
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
