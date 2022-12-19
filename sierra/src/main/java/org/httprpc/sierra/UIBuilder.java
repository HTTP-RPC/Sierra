package org.httprpc.sierra;

import javax.swing.JPanel;
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
     * The component type.
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
     * Declares a strut cell.
     *
     * @param size
     * The spacer size.
     *
     * @return
     * The strut cell.
     */
    public static Cell<Spacer> strut(int size) {
        return cell(new Spacer(size));
    }

    /**
     * Declares a glue cell.
     *
     * @return
     * The glue cell.
     */
    public static Cell<Spacer> glue() {
        return cell(new Spacer(0)).weightBy(1.0);
    }

    public static RowPanel row(HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment,
        int spacing, boolean alignToBaseline, Cell<?>... cells) {
        // TODO
        return null;
    }

    public static ColumnPanel column(HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment,
        int spacing, boolean alignToGrid, Cell<?>... cells) {
        // TODO
        return null;
    }

    /**
     * Declares a stack panel.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The stack panel.
     */
    public static StackPanel stack(Cell<?>... cells) {
        return populate(new StackPanel(), cells);
    }

    private static <T extends JPanel> T populate(T panel, Cell<?>... cells) {
        if (cells == null) {
            throw new IllegalArgumentException();
        }

        for (var i = 0; i < cells.length; i++) {
            var cell = cells[i];

            panel.add(cell.component, cell.constraints);
        }

        return panel;
    }
}
