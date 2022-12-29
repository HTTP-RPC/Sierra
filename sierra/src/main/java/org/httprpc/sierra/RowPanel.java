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
 * edges to the container's top and bottom insets.
 */
public class RowPanel extends BoxPanel {
    // Row layout manager
    private class RowLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize() {
            var parent = getParent();

            List<Integer> columnWidths = null;
            var columnSpacing = 0;

            if (parent instanceof ColumnPanel) {
                var columnPanel = (ColumnPanel)parent;

                if (columnPanel.getAlignToGrid()) {
                    columnWidths = columnPanel.getColumnWidths();
                    columnSpacing = columnPanel.getSpacing();
                }
            }

            var preferredWidth = 0;
            var totalWeight = 0.0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (Double.isNaN(weight)) {
                    component.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
                    component.setSize(component.getPreferredSize());

                    var width = component.getWidth();

                    if (columnWidths != null) {
                        if (i == columnWidths.size()) {
                            columnWidths.add(width);
                        } else {
                            width = Math.max(columnWidths.get(i), width);

                            columnWidths.set(i, width);

                            component.setSize(width, component.getHeight());
                        }
                    }

                    preferredWidth += width;
                } else {
                    if (columnWidths != null && i == columnWidths.size()) {
                        columnWidths.add(0);
                    }

                    totalWeight += weight;
                }
            }

            preferredWidth += (getSpacing() + columnSpacing) * (n - 1);

            var size = getSize();
            var insets = getInsets();

            var remainingWidth = Math.max(size.width - (insets.left + insets.right) - preferredWidth, 0);

            var preferredHeight = 0;

            var maximumAscent = 0;
            var maximumDescent = 0;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (!Double.isNaN(weight)) {
                    var columnWidth = (columnWidths == null) ? 0 : columnWidths.get(i);

                    var width = columnWidth > 0 ? columnWidth : (int)Math.round(remainingWidth * (weight / totalWeight));

                    component.setSize(width, Integer.MAX_VALUE);
                    component.setSize(width, component.getPreferredSize().height);
                }

                preferredHeight = Math.max(preferredHeight, component.getHeight());

                if (alignToBaseline) {
                    var height = component.getHeight();

                    var baseline = component.getBaseline(component.getWidth(), height);

                    if (baseline >= 0) {
                        maximumAscent = Math.max(maximumAscent, baseline);
                        maximumDescent = Math.max(maximumDescent, height - baseline);
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
            var columnSpacing = 0;

            if (parent instanceof ColumnPanel) {
                var columnPanel = (ColumnPanel)parent;

                if (columnPanel.getAlignToGrid()) {
                    columnWidths = columnPanel.getColumnWidths();
                    columnSpacing = columnPanel.getSpacing();
                }
            }

            var size = getSize();
            var insets = getInsets();

            var remainingWidth = Math.max(size.width - (insets.left + insets.right), 0);
            var totalWeight = 0.0;

            var height = Math.max(size.height - (insets.top + insets.bottom), 0);

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (Double.isNaN(weight)) {
                    component.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
                    component.setSize(component.getPreferredSize());

                    if (!alignToBaseline) {
                        var alignmentY = component.getAlignmentY();

                        var preferredHeight = component.getHeight();

                        int adjustedHeight;
                        if (alignmentY == 0.5f) {
                            adjustedHeight = height;
                        } else {
                            adjustedHeight = preferredHeight + Math.round((height - preferredHeight) * getScale(alignmentY));
                        }

                        component.setSize(component.getPreferredSize().width, adjustedHeight);
                    }

                    var preferredWidth = component.getWidth();

                    var columnWidth = preferredWidth;

                    if (columnWidths != null) {
                        if (i == columnWidths.size()) {
                            columnWidths.add(columnWidth);
                        } else {
                            columnWidth = Math.max(columnWidths.get(i), columnWidth);

                            columnWidths.set(i, columnWidth);

                            var alignmentX = component.getAlignmentX();

                            int adjustedWidth;
                            if (alignmentX == 0.5f) {
                                adjustedWidth = columnWidth;
                            } else {
                                adjustedWidth = preferredWidth + Math.round((columnWidth - preferredWidth) * getScale(alignmentX));
                            }

                            component.setSize(adjustedWidth, component.getHeight());
                        }
                    }

                    remainingWidth -= columnWidth;
                } else {
                    totalWeight += weight;
                }
            }

            var spacing = getSpacing() + columnSpacing;

            remainingWidth = Math.max(0, remainingWidth - spacing * (n - 1));

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
                    var columnWidth = (columnWidths == null) ? 0 : columnWidths.get(i);

                    var width = columnWidth > 0 ? columnWidth : (int)Math.round(remainingWidth * (weight / totalWeight));

                    if (alignToBaseline) {
                        component.setSize(width, Integer.MAX_VALUE);
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

                    if (baseline >= 0) {
                        component.setLocation(component.getX(), component.getY() + (maximumBaseline - baseline));
                    }
                }
            }
        }

        private float getScale(float alignment) {
            return (alignment < 0.5f) ? alignment / 0.5f : (1.0f - alignment) / 0.5f;
        }
    }

    private boolean alignToBaseline = false;

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
     * Indicates that sub-components will be aligned to baseline.
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
