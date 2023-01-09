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
import java.util.function.Consumer;

/**
 * Provides static factory methods for declaratively constructing a component
 * hierarchy.
 */
public class UIBuilder {
    /**
     * Provides a fluent API for configuring a sub-component.
     */
    public static class Cell<C extends Component> {
        private C component;

        private Object constraints = null;

        private Cell(C component) {
            this.component = component;
        }

        /**
         * Returns the cell's component.
         *
         * @return
         * The cell's component.
         */
        public C getComponent() {
            return component;
        }

        /**
         * Applies a weight value to a cell.
         *
         * @param weight
         * The weight value.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> weightBy(double weight) {
            if (weight < 0.0) {
                throw new IllegalArgumentException();
            }

            constraints = weight;

            return this;
        }

        /**
         * Applies consumers to a cell's component.
         *
         * @param consumers
         * The consumers to apply.
         *
         * @return
         * The cell instance.
         */
        @SafeVarargs
        public final Cell<C> with(Consumer<? super C>... consumers) {
            if (consumers == null) {
                throw new IllegalArgumentException();
            }

            for (var i = 0; i < consumers.length; i++) {
                consumers[i].accept(component);
            }

            return this;
        }
    }

    private UIBuilder() {
    }

    /**
     * Declares a cell.
     *
     * @param <C>
     * The cell's component type.
     *
     * @param component
     * The cell's component.
     *
     * @return
     * The cell instance.
     */
    public static <C extends Component> Cell<C> cell(C component) {
        return new Cell<>(component);
    }

    /**
     * Declares a row cell.
     *
     * @param cells
     * The row's contents.
     *
     * @return
     * The cell instance.
     */
    public static Cell<RowPanel> row(Cell<?>... cells) {
        if (cells == null) {
            throw new IllegalArgumentException();
        }

        return cell(populate(new RowPanel(), cells));
    }

    /**
     * Declares a row cell.
     *
     * @param spacing
     * The cell spacing.
     *
     * @param cells
     * The row's contents.
     *
     * @return
     * The cell instance.
     */
    public static Cell<RowPanel> row(int spacing, Cell<?>... cells) {
        return row(spacing, false, cells);
    }

    /**
     * Declares a row cell.
     *
     * @param alignToBaseline
     * {@code true} to align to baseline; {@code false}, otherwise.
     *
     * @param cells
     * The row's contents.
     *
     * @return
     * The cell instance.
     */
    public static Cell<RowPanel> row(boolean alignToBaseline, Cell<?>... cells) {
        return row(0, alignToBaseline, cells);
    }

    /**
     * Declares a row cell.
     *
     * @param spacing
     * The cell spacing.
     *
     * @param alignToBaseline
     * {@code true} to align to baseline; {@code false}, otherwise.
     *
     * @param cells
     * The row's contents.
     *
     * @return
     * The cell instance.
     */
    public static Cell<RowPanel> row(int spacing, boolean alignToBaseline, Cell<?>... cells) {
        return row(cells).with(rowPanel -> {
            rowPanel.setSpacing(spacing);
            rowPanel.setAlignToBaseline(alignToBaseline);
        });
    }

    /**
     * Declares a column cell.
     *
     * @param cells
     * The column's contents.
     *
     * @return
     * The cell instance.
     */
    public static Cell<ColumnPanel> column(Cell<?>... cells) {
        if (cells == null) {
            throw new IllegalArgumentException();
        }

        return cell(populate(new ColumnPanel(), cells));
    }

    /**
     * Declares a column cell.
     *
     * @param spacing
     * The cell spacing.
     *
     * @param cells
     * The column's contents.
     *
     * @return
     * The cell instance.
     */
    public static Cell<ColumnPanel> column(int spacing, Cell<?>... cells) {
        return column(spacing, false, cells);
    }

    /**
     * Declares a column cell.
     *
     * @param alignToGrid
     * {@code true} to align row descendants to grid; {@code false}, otherwise.
     *
     * @param cells
     * The column's contents.
     *
     * @return
     * The cell instance.
     */
    public static Cell<ColumnPanel> column(boolean alignToGrid, Cell<?>... cells) {
        return column(0, alignToGrid, cells);
    }

    /**
     * Declares a column cell.
     *
     * @param spacing
     * The cell spacing.
     *
     * @param alignToGrid
     * {@code true} to align row descendants to grid; {@code false}, otherwise.
     *
     * @param cells
     * The column's contents.
     *
     * @return
     * The cell instance.
     */
    public static Cell<ColumnPanel> column(int spacing, boolean alignToGrid, Cell<?>... cells) {
        return column(cells).with(columnPanel -> {
            columnPanel.setSpacing(spacing);
            columnPanel.setAlignToGrid(alignToGrid);
        });
    }

    private static <P extends BoxPanel> P populate(P panel, Cell<?>... cells) {
        for (var i = 0; i < cells.length; i++) {
            var cell = cells[i];

            panel.add(cell.component, cell.constraints);
        }

        return panel;
    }

    /**
     * Declares a fixed-size spacer cell.
     *
     * @param size
     * The spacer size.
     *
     * @return
     * The cell instance.
     */
    public static Cell<Spacer> strut(int size) {
        return cell(new Spacer(size));
    }

    /**
     * Declares a flexible spacer cell with a weight of 1.0.
     *
     * @return
     * The cell instance.
     */
    public static Cell<Spacer> glue() {
        return glue(1.0);
    }

    /**
     * Declares a flexible spacer cell with a given weight.
     *
     * @param weight
     * The cell weight.
     *
     * @return
     * The cell instance.
     */
    public static Cell<Spacer> glue(double weight) {
        return cell(new Spacer(0)).weightBy(weight);
    }

    /**
     * Declares a stack cell.
     *
     * @param cells
     * The stack's contents.
     *
     * @return
     * The cell instance.
     */
    public static Cell<StackPanel> stack(Cell<?>... cells) {
        if (cells == null) {
            throw new IllegalArgumentException();
        }

        var panel = new StackPanel();

        for (var i = cells.length - 1; i >= 0; i--) {
            var cell = cells[i];

            panel.add(cell.component, cell.constraints);
        }

        return cell(panel);
    }
}
