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
}
