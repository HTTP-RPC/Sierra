package org.httprpc.sierra;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Insets;
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

    // TODO

    public static Cell<RowPanel> row(Insets insets,
        HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment,
        int spacing, boolean alignToBaseline, Cell<?>... cells) {
        // TODO
        return null;
    }

    public static Cell<RowPanel> column(Insets insets,
        HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment,
        int spacing, boolean alignToGrid, Cell<?>... cells) {
        // TODO
        return null;
    }

    public static Cell<Spacer> spacer() {
        // TODO
        return null;
    }

    public static Cell<Spacer> spacer(int size) {
        // TODO
        return null;
    }

    public static Cell<StackPanel> stack(Insets insets, Cell<?> cells) {
        // TODO
        return null;
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
