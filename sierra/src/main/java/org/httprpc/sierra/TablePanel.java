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

import static org.httprpc.kilo.util.Iterables.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Arranges components in a tabular grid. Row contents are aligned to baseline.
 */
public class TablePanel extends GridPanel {
    private class TableLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize(Container container) {
            var insets = getInsets();

            var horizontalSpacing = getHorizontalSpacing();
            var verticalSpacing = getVerticalSpacing();

            var columnWidths = new ArrayList<Integer>(columnCount);

            var totalRowHeight = 0;

            var rowCount = 0;

            var n = getComponentCount();

            var columnIndex = 0;

            var maximumRowAscent = 0;
            var maximumRowDescent = 0;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var preferredSize = component.getPreferredSize();

                if (columnIndex == columnWidths.size()) {
                    columnWidths.add(preferredSize.width);
                } else {
                    columnWidths.set(columnIndex, Math.max(columnWidths.get(columnIndex), preferredSize.width));
                }

                var baseline = component.getBaseline(preferredSize.width, preferredSize.height);

                if (baseline >= 0) {
                    maximumRowAscent = Math.max(maximumRowAscent, baseline);
                    maximumRowDescent = Math.max(maximumRowDescent, preferredSize.height - baseline);
                }

                columnIndex += coalesce(columnSpans.get(i), () -> 1);

                if (columnIndex >= columnCount) {
                    totalRowHeight += maximumRowAscent + maximumRowDescent;

                    rowCount++;

                    columnIndex = 0;

                    maximumRowAscent = 0;
                    maximumRowDescent = 0;
                }
            }

            if (columnIndex < columnCount) {
                totalRowHeight += maximumRowAscent + maximumRowDescent;

                rowCount++;
            }

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

            var columnWidths = new ArrayList<Integer>(columnCount);

            var rowHeights = new ArrayList<Integer>();
            var rowBaselines = new ArrayList<Integer>();

            var n = getComponentCount();

            var columnIndex = 0;

            var maximumRowAscent = 0;
            var maximumRowDescent = 0;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var preferredSize = component.getPreferredSize();

                if (columnIndex == columnWidths.size()) {
                    columnWidths.add(preferredSize.width);
                } else {
                    columnWidths.set(columnIndex, Math.max(columnWidths.get(columnIndex), preferredSize.width));
                }

                var baseline = component.getBaseline(preferredSize.width, preferredSize.height);

                if (baseline >= 0) {
                    maximumRowAscent = Math.max(maximumRowAscent, baseline);
                    maximumRowDescent = Math.max(maximumRowDescent, preferredSize.height - baseline);
                }

                columnIndex += coalesce(columnSpans.get(i), () -> 1);

                if (columnIndex >= columnCount) {
                    rowHeights.add(maximumRowAscent + maximumRowDescent);
                    rowBaselines.add(maximumRowAscent);

                    columnIndex = 0;

                    maximumRowAscent = 0;
                    maximumRowDescent = 0;
                }
            }

            if (columnIndex < columnCount) {
                rowHeights.add(maximumRowAscent + maximumRowDescent);
                rowBaselines.add(maximumRowAscent);
            }

            columnIndex = 0;

            var x = insets.left;
            var y = insets.top;

            var rowIndex = 0;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var columnSpan = Math.min(coalesce(columnSpans.get(i), () -> 1), columnCount - columnIndex);

                var cellWidth = 0;

                for (var j = 0; j < columnSpan; j++) {
                    cellWidth += columnWidths.get(columnIndex);

                    columnIndex++;
                }

                component.setSize(cellWidth + horizontalSpacing * (columnSpan - 1), component.getPreferredSize().height);

                var baseline = component.getBaseline(component.getWidth(), component.getHeight());

                if (baseline >= 0) {
                    component.setLocation(x, y + (rowBaselines.get(rowIndex) - baseline));
                } else {
                    component.setLocation(x, y + (rowHeights.get(rowIndex) - component.getHeight()) / 2);
                }

                if (columnIndex < columnCount) {
                    x += component.getWidth() + horizontalSpacing;
                } else {
                    columnIndex = 0;

                    x = insets.left;

                    y += rowHeights.get(rowIndex) + verticalSpacing;

                    rowIndex++;
                }
            }
        }
    }

    private List<Integer> columnSpans = new ArrayList<>();

    private int columnCount = 1;

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
}
