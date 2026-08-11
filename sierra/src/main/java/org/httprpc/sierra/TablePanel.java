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

import java.awt.Container;
import java.awt.Dimension;

/**
 * Arranges components in a two-dimensional grid.
 */
public class TablePanel extends LayoutPanel {
    /**
     * Table panel constraints.
     */
    public static class Constraints {
        private int rowIndex = 0;
        private int columnIndex = 0;
        private int rowSpan = 1;
        private int columnSpan = 1;

        /**
         * Returns the row index.
         *
         * @return
         * The row index.
         */
        public int getRowIndex() {
            return rowIndex;
        }

        /**
         * Sets the row index.
         *
         * @param rowIndex
         * The row index.
         */
        public void setRowIndex(int rowIndex) {
            if (rowIndex < 0) {
                throw new IllegalArgumentException();
            }

            this.rowIndex = rowIndex;
        }

        /**
         * Returns the column index.
         *
         * @return
         * The column index.
         */
        public int getColumnIndex() {
            return columnIndex;
        }

        /**
         * Sets the column index.
         *
         * @param columnIndex
         * The column index.
         */
        public void setColumnIndex(int columnIndex) {
            if (columnIndex < 0) {
                throw new IllegalArgumentException();
            }

            this.columnIndex = columnIndex;
        }

        /**
         * Returns the row span.
         *
         * @return
         * The row span.
         */
        public int getRowSpan() {
            return rowSpan;
        }

        /**
         * Sets the row span.
         *
         * @param rowSpan
         * The row span.
         */
        public void setRowSpan(int rowSpan) {
            if (rowSpan < 1) {
                throw new IllegalArgumentException();
            }

            this.rowSpan = rowSpan;
        }

        /**
         * Returns the column span.
         *
         * @return
         * The column span.
         */
        public int getColumnSpan() {
            return columnSpan;
        }

        /**
         * Sets the column span.
         *
         * @param columnSpan
         * The column span.
         */
        public void setColumnSpan(int columnSpan) {
            if (columnSpan < 1) {
                throw new IllegalArgumentException();
            }

            this.columnSpan = columnSpan;
        }
    }

    private class TableLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize(Container container) {
            // TODO
            return new Dimension(0, 0);
        }

        @Override
        public void layoutContainer(Container container) {
            // TODO
        }
    }

    /**
     * Constructs a new table panel.
     */
    public TablePanel() {
        setLayout(new TableLayoutManager());
    }
}
