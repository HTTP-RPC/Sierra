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
import java.util.List;

/**
 * Arranges sub-components horizontally in a row, optionally pinning component
 * edges to the container's top and bottom insets. The panel's preferred width
 * is determined as the total preferred width of its unweighted sub-components
 * plus horizontal insets. By default, preferred height is the maximum
 * preferred height of all sub-components plus vertical insets, and
 * sub-components are pinned to top and bottom. When aligning to baseline,
 * preferred height is the maximum ascent/descent of all sub-components plus
 * vertical insets, and sub-components are not pinned to top and bottom.
 */
public class RowPanel extends BoxPanel {
    // Row layout manager
    private class RowLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize() {
            var parent = getParent();

            List<Integer> columnWidths = null;
            List<Double> columnWeights = null;

            var spacing = getSpacing();

            if (parent instanceof ColumnPanel) {
                var columnPanel = (ColumnPanel)parent;

                if (columnPanel.getAlignToGrid()) {
                    columnWidths = columnPanel.getColumnWidths();
                    columnWeights = columnPanel.getColumnWeights();

                    spacing = columnPanel.getRowSpacing();
                }
            }

            var size = getSize();
            var insets = getInsets();

            var preferredWidth = 0;
            var totalWeight = 0.0;

            var height = Math.max(size.height - (insets.top + insets.bottom), 0);

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (Double.isNaN(weight)) {
                    component.setSize(Integer.MAX_VALUE, height);
                    component.setSize(component.getPreferredSize());

                    var width = component.getWidth();

                    if (columnWidths != null) {
                        width = Math.max(columnWidths.get(i), width);

                        columnWidths.set(i, width);

                        component.setSize(width, component.getHeight());
                    }

                    preferredWidth += width;
                } else {
                    if (columnWeights != null) {
                        weight = Math.max(columnWeights.get(i), weight);

                        columnWeights.set(i, weight);
                    }

                    totalWeight += weight;
                }
            }

            preferredWidth += spacing * (n - 1);

            var excessWidth = Math.max(size.width - (insets.left + insets.right) - preferredWidth, 0);
            var remainingWidth = excessWidth;

            var preferredHeight = 0;

            var maximumAscent = 0;
            var maximumDescent = 0;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (!Double.isNaN(weight)) {
                    var columnWidth = (columnWidths == null) ? 0 : columnWidths.get(i);

                    int width;
                    if (i < n - 1) {
                        width = (columnWidth > 0) ? columnWidth : (int)Math.round(excessWidth * (weight / totalWeight));

                        remainingWidth -= width;
                    } else {
                        width = remainingWidth;
                    }

                    component.setSize(width, height);
                    component.setSize(width, component.getPreferredSize().height);
                }

                preferredHeight = Math.max(preferredHeight, component.getHeight());

                if (alignToBaseline) {
                    var baseline = component.getBaseline(component.getWidth(), component.getHeight());

                    if (baseline >= 0) {
                        maximumAscent = Math.max(maximumAscent, baseline);
                        maximumDescent = Math.max(maximumDescent, component.getHeight() - baseline);
                    }
                }
            }

            if (alignToBaseline) {
                preferredHeight = Math.max(maximumAscent + maximumDescent, preferredHeight);
            }

            return new Dimension(preferredWidth + insets.left + insets.right, preferredHeight + insets.top + insets.bottom);
        }

        @Override
        public void layoutContainer() {
            var parent = getParent();

            List<Integer> columnWidths = null;
            List<Double> columnWeights = null;

            var spacing = getSpacing();

            if (parent instanceof ColumnPanel) {
                var columnPanel = (ColumnPanel)parent;

                if (columnPanel.getAlignToGrid()) {
                    columnWidths = columnPanel.getColumnWidths();
                    columnWeights = columnPanel.getColumnWeights();

                    spacing = columnPanel.getRowSpacing();
                }
            }

            var size = getSize();
            var insets = getInsets();

            var excessWidth = Math.max(size.width - (insets.left + insets.right), 0);
            var totalWeight = 0.0;

            var height = Math.max(size.height - (insets.top + insets.bottom), 0);

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (Double.isNaN(weight)) {
                    component.setSize(Integer.MAX_VALUE, height);
                    component.setSize(component.getPreferredSize());

                    if (!alignToBaseline) {
                        if (columnWidths != null) {
                            component.setSize(component.getWidth(), adjustSize(component.getHeight(), height, component.getAlignmentY()));
                        } else {
                            component.setSize(component.getWidth(), height);
                        }
                    }

                    var preferredWidth = component.getWidth();

                    var columnWidth = preferredWidth;

                    if (columnWidths != null) {
                        if (i == columnWidths.size()) {
                            columnWidths.add(columnWidth);
                        } else {
                            columnWidth = Math.max(columnWidths.get(i), columnWidth);

                            columnWidths.set(i, columnWidth);

                            component.setSize(adjustSize(preferredWidth, columnWidth, component.getAlignmentX()), component.getHeight());
                        }
                    }

                    if (columnWeights != null && i == columnWeights.size()) {
                        columnWeights.add(Double.NaN);
                    }

                    excessWidth -= columnWidth;
                } else {
                    if (columnWeights != null) {
                        if (i == columnWidths.size()) {
                            columnWeights.add(weight);
                        } else {
                            weight = Math.max(columnWeights.get(i), weight);

                            columnWeights.set(i, weight);
                        }
                    }

                    if (columnWidths != null && i == columnWidths.size()) {
                        columnWidths.add(0);
                    }

                    totalWeight += weight;
                }
            }

            excessWidth = Math.max(0, excessWidth - spacing * (n - 1));

            var remainingWidth = excessWidth;

            var leftToRight = getComponentOrientation().isLeftToRight();

            int x;
            if (leftToRight) {
                x = insets.left;
            } else {
                x = size.width - insets.right;

                spacing *= -1;
            }

            var baselines = new int[n];

            var maximumBaseline = 0;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (!Double.isNaN(weight)) {
                    var width = (columnWidths == null) ? 0 : columnWidths.get(i);

                    if (width == 0) {
                        if (columnWeights != null) {
                            weight = columnWeights.get(i);
                        }

                        if (i < n - 1) {
                            width = (int)Math.round(excessWidth * (weight / totalWeight));

                            remainingWidth -= width;
                        } else {
                            width = remainingWidth;
                        }
                    }

                    if (alignToBaseline) {
                        component.setSize(width, height);
                        component.setSize(width, component.getPreferredSize().height);
                    } else {
                        component.setSize(width, height);
                    }
                }

                var width = component.getWidth();

                var columnWidth = (columnWidths == null) ? width : columnWidths.get(i);

                var gap = columnWidth - width;

                if (!leftToRight) {
                    x -= width;

                    gap *= -1;
                }

                var alignmentX = component.getAlignmentX();

                if (alignmentX > 0.5) {
                    x += gap;
                }

                component.setLocation(x, insets.top);

                var alignmentY = component.getAlignmentY();

                if (!alignToBaseline && alignmentY > 0.5) {
                    component.setLocation(component.getX(), component.getY() + (height - component.getHeight()));
                }

                if (leftToRight) {
                    x += width;
                }

                if (alignmentX < 0.5) {
                    x += gap;
                }

                x += spacing;

                if (alignToBaseline) {
                    var baseline = component.getBaseline(component.getWidth(), component.getHeight());

                    baselines[i] = baseline;

                    if (baseline >= 0) {
                        maximumBaseline = Math.max(baseline, maximumBaseline);
                    }
                }
            }

            if (alignToBaseline) {
                for (var i = 0; i < n; i++) {
                    var component = getComponent(i);

                    var baseline = baselines[i];

                    int offset;
                    if (baseline >= 0) {
                        offset = maximumBaseline - baseline;
                    } else {
                        offset = (height - component.getHeight()) / 2;
                    }

                    component.setLocation(component.getX(), component.getY() + offset);
                }
            }
        }
    }

    private boolean alignToBaseline = false;

    private static int adjustSize(int preferredSize, int size, float alignment) {
        return Math.round(preferredSize + Math.max(size - preferredSize, 0) * (1.0f - Math.abs((0.5f - alignment) / 0.5f)));
    }

    /**
     * Constructs a new row panel.
     */
    public RowPanel() {
        setLayout(new RowLayoutManager());
    }

    /**
     * Sets the layout manager.
     * {@inheritDoc}
     */
    @Override
    public void setLayout(LayoutManager layoutManager) {
        if (layoutManager != null && !(layoutManager instanceof RowLayoutManager)) {
            throw new IllegalArgumentException();
        }

        super.setLayout(layoutManager);
    }

    /**
     * Indicates that sub-components will be aligned to baseline. The default
     * value is {@code false}.
     *
     * @return
     * {@code true} to align to baseline; {@code false}, otherwise.
     */
    public boolean getAlignToBaseline() {
        return alignToBaseline;
    }

    /**
     * Toggles baseline alignment.
     *
     * @param alignToBaseline
     * {@code true} to align to baseline; {@code false}, otherwise.
     */
    public void setAlignToBaseline(boolean alignToBaseline) {
        this.alignToBaseline = alignToBaseline;

        revalidate();
    }

    /**
     * Calculates the panel's baseline.
     * {@inheritDoc}
     */
    @Override
    public int getBaseline(int width, int height) {
        return alignToBaseline ? super.getBaseline(width, height) : -1;
    }
}
