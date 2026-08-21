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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Iterables.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Arranges components in a tabular grid.
 */
public class TablePanel extends GridPanel {
    private class TableLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize(Container container) {
            var insets = getInsets();

            var horizontalSpacing = getHorizontalSpacing();
            var verticalSpacing = getVerticalSpacing();

            var columnWidths = listOf(iterableOf(0, columnCount));

            var totalRowHeight = 0;
            var rowCount = 0;

            var columnIndex = 0;

            var rowHeight = 0;

            var maximumRowAscent = 0;
            var maximumRowDescent = 0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var preferredSize = component.getPreferredSize();

                var columnSpan = getColumnSpan(i, columnIndex);

                if (columnSpan == 1) {
                    columnWidths.set(columnIndex, Math.max(columnWidths.get(columnIndex), preferredSize.width));
                }

                if (alignToBaseline) {
                    var baseline = component.getBaseline(preferredSize.width, preferredSize.height);

                    if (baseline >= 0) {
                        maximumRowAscent = Math.max(maximumRowAscent, baseline);
                        maximumRowDescent = Math.max(maximumRowDescent, preferredSize.height - baseline);
                    }
                } else {
                    rowHeight = Math.max(rowHeight, preferredSize.height);
                }

                columnIndex += columnSpan;

                if (columnIndex == columnCount) {
                    if (alignToBaseline) {
                        totalRowHeight += maximumRowAscent + maximumRowDescent;
                    } else {
                        totalRowHeight += rowHeight;
                    }

                    rowCount++;

                    columnIndex = 0;

                    rowHeight = 0;

                    maximumRowAscent = 0;
                    maximumRowDescent = 0;
                }
            }

            if (columnIndex < columnCount) {
                if (alignToBaseline) {
                    totalRowHeight += maximumRowAscent + maximumRowDescent;
                } else {
                    totalRowHeight += rowHeight;
                }

                rowCount++;
            }

            adjustColumnWidths(columnWidths);

            var totalColumnWidth = sumOf(columnWidths, Integer::intValue);

            var preferredWidth = totalColumnWidth + horizontalSpacing * (columnCount - 1) + insets.left + insets.right;
            var preferredHeight = totalRowHeight + verticalSpacing * (rowCount - 1) + insets.top + insets.bottom;

            return new Dimension(preferredWidth, preferredHeight);
        }

        @Override
        public void layoutContainer(Container parent) {
            var insets = getInsets();

            var horizontalSpacing = getHorizontalSpacing();
            var verticalSpacing = getVerticalSpacing();

            var columnWidths = listOf(iterableOf(0, columnCount));

            var rowHeights = new ArrayList<Integer>();
            var rowBaselines = new ArrayList<Integer>();

            var columnIndex = 0;

            var rowHeight = 0;

            var maximumRowAscent = 0;
            var maximumRowDescent = 0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var preferredSize = component.getPreferredSize();

                var columnSpan = getColumnSpan(i, columnIndex);

                if (columnSpan == 1) {
                    columnWidths.set(columnIndex, Math.max(columnWidths.get(columnIndex), preferredSize.width));
                }

                if (alignToBaseline) {
                    var baseline = component.getBaseline(preferredSize.width, preferredSize.height);

                    if (baseline >= 0) {
                        maximumRowAscent = Math.max(maximumRowAscent, baseline);
                        maximumRowDescent = Math.max(maximumRowDescent, preferredSize.height - baseline);
                    }
                } else {
                    rowHeight = Math.max(rowHeight, preferredSize.height);
                }

                columnIndex += columnSpan;

                if (columnIndex == columnCount) {
                    if (alignToBaseline) {
                        rowHeights.add(maximumRowAscent + maximumRowDescent);
                        rowBaselines.add(maximumRowAscent);
                    } else {
                        rowHeights.add(rowHeight);
                    }

                    columnIndex = 0;

                    rowHeight = 0;

                    maximumRowAscent = 0;
                    maximumRowDescent = 0;
                }
            }

            if (columnIndex < columnCount) {
                if (alignToBaseline) {
                    rowHeights.add(maximumRowAscent + maximumRowDescent);
                    rowBaselines.add(maximumRowAscent);
                } else {
                    rowHeights.add(rowHeight);
                }
            }

            adjustColumnWidths(columnWidths);

            columnIndex = 0;

            var x = insets.left;
            var y = insets.top;

            var rowIndex = 0;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var columnSpan = getColumnSpan(i, columnIndex);

                var cellWidth = 0;

                for (var j = 0; j < columnSpan; j++) {
                    cellWidth += columnWidths.get(columnIndex);

                    columnIndex++;
                }

                var cellHeight = alignToBaseline ? component.getPreferredSize().height : rowHeights.get(rowIndex);

                component.setSize(cellWidth + horizontalSpacing * (columnSpan - 1), cellHeight);

                if (alignToBaseline) {
                    var baseline = component.getBaseline(component.getWidth(), component.getHeight());

                    if (baseline >= 0) {
                        component.setLocation(x, y + (rowBaselines.get(rowIndex) - baseline));
                    } else {
                        component.setLocation(x, y + (rowHeights.get(rowIndex) - component.getHeight()) / 2);
                    }
                } else {
                    component.setLocation(x, y);
                }

                if (columnIndex == columnCount) {
                    columnIndex = 0;

                    x = insets.left;

                    y += rowHeights.get(rowIndex) + verticalSpacing;

                    rowIndex++;
                } else {
                    x += component.getWidth() + horizontalSpacing;
                }
            }
        }

        private int getColumnSpan(int i, int columnIndex) {
            return Math.min(coalesce(columnSpans.get(i), () -> 1), columnCount - columnIndex);
        }

        private void adjustColumnWidths(List<Integer> columnWidths) {
            var columnIndex = 0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var preferredSize = component.getPreferredSize();

                var columnSpan = getColumnSpan(i, columnIndex);

                var cellWidth = 0;

                for (var j = 0; j < columnSpan; j++) {
                    cellWidth += columnWidths.get(columnIndex);

                    columnIndex++;
                }

                cellWidth += getHorizontalSpacing() * (columnSpan - 1);

                var delta = preferredSize.width - cellWidth;

                if (delta > 0) {
                    columnWidths.set(columnIndex - 1, columnWidths.get(columnIndex - 1) + delta);
                }

                if (columnIndex == columnCount) {
                    columnIndex = 0;
                }
            }
        }
    }

    private List<Integer> columnSpans = new ArrayList<>();

    private int columnCount = 1;

    private boolean alignToBaseline = false;

    /**
     * Constructs a new table panel.
     */
    public TablePanel() {
        setLayout(new TableLayoutManager());
    }

    @Override
    protected void addImpl(Component component, Object constraints, int index) {
        super.addImpl(component, constraints, index);

        var columnSpan = (Integer)constraints;

        if (columnSpan != null && columnSpan < 1) {
            throw new IllegalArgumentException();
        }

        columnSpans.add(index == -1 ? columnSpans.size() : index, columnSpan);
    }

    /**
     * Returns the column count. The default value is 1.
     *
     * @return
     * The column count.
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Sets the column count.
     *
     * @param columnCount
     * The column count.
     */
    public void setColumnCount(int columnCount) {
        if (columnCount < 1) {
            throw new IllegalArgumentException();
        }

        this.columnCount = columnCount;

        revalidate();
        repaint();
    }

    /**
     * Indicates that components will be aligned to baseline. The default value
     * is {@code false}.
     *
     * @return
     * {@code true} if baseline alignment is enabled; {@code false}, otherwise.
     */
    public boolean getAlignToBaseline() {
        return alignToBaseline;
    }

    /**
     * Toggles baseline alignment.
     *
     * @param alignToBaseline
     * {@code true} to enable baseline alignment; {@code false} to disable it.
     */
    public void setAlignToBaseline(boolean alignToBaseline) {
        this.alignToBaseline = alignToBaseline;

        revalidate();
        repaint();
    }
}
