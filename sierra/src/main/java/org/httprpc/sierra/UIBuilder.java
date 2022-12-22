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
            constraints = weight;

            return this;
        }

        /**
         * Applies a consumer to a cell's component.
         *
         * @param consumer
         * The consumer to apply.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> with(Consumer<C> consumer) {
            if (consumer == null) {
                throw new IllegalArgumentException();
            }

            consumer.accept(component);

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
     * Declares a flexible spacer cell.
     *
     * @return
     * The cell instance.
     */
    public static Cell<Spacer> glue() {
        return cell(new Spacer(0)).weightBy(1.0);
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
