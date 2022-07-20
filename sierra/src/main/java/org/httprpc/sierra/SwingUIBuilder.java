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

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;

/**
 * Class for declaratively constructing a Swing component hierarchy.
 */
public class SwingUIBuilder {
    /**
     * Provides a fluent API for configuring a sub-component.
     */
    public static class Cell<C extends JComponent> {
        private C component;

        private Object constraints = null;

        private Cell(C component) {
            this.component = component;
        }

        /**
         * Applies constraints to a cell.
         *
         * @param constraints
         * The constraints to apply.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> constrainedBy(Object constraints) {
            this.constraints = constraints;

            return this;
        }

        /**
         * Applies a handler to a cell's component.
         *
         * @param handler
         * The handler to apply.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> with(Consumer<C> handler) {
            if (handler == null) {
                throw new IllegalArgumentException();
            }

            handler.accept(component);

            return this;
        }
    }

    private SwingUIBuilder() {
    }

    /**
     * Declares a cell.
     *
     * @param component
     * The cell's component.
     *
     * @return
     * The cell instance.
     *
     * @param <C>
     * The component type.
     */
    public static <C extends JComponent> Cell<C> cell(C component) {
        if (component == null) {
            throw new IllegalArgumentException();
        }

        return new Cell<>(component);
    }

    /**
     * Declares a flow panel.
     *
     * @param flowLayout
     * The panel's flow layout.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static JPanel flowPanel(FlowLayout flowLayout, Cell<?>... cells) {
        return flowPanel(flowLayout, false, cells);
    }

    /**
     * Declares a flow panel.
     *
     * @param flowLayout
     * The panel's flow layout.
     *
     * @param alignOnBaseline
     * Indicates that the flow panel's content should be aligned to its
     * baseline.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static JPanel flowPanel(FlowLayout flowLayout, boolean alignOnBaseline, Cell<?>... cells) {
        if (flowLayout == null) {
            throw new IllegalArgumentException();
        }

        flowLayout.setAlignOnBaseline(alignOnBaseline);

        return populate(new JPanel(flowLayout), cells);
    }

    /**
     * Declares a border panel.
     *
     * @param borderLayout
     * The panel's border layout.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static JPanel borderPanel(BorderLayout borderLayout, Cell<?>... cells) {
        if (borderLayout == null) {
            throw new IllegalArgumentException();
        }

        return populate(new JPanel(borderLayout), cells);
    }

    /**
     * Declares a grid panel.
     *
     * @param gridLayout
     * The panel's grid layout.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static JPanel gridPanel(GridLayout gridLayout, Cell<?>... cells) {
        if (gridLayout == null) {
            throw new IllegalArgumentException();
        }

        return populate(new JPanel(gridLayout), cells);
    }

    /**
     * Declares a card panel.
     *
     * @param cardLayout
     * The panel's card layout.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static JPanel cardPanel(CardLayout cardLayout, Cell<?>... cells) {
        if (cardLayout == null) {
            throw new IllegalArgumentException();
        }

        return populate(new JPanel(cardLayout), cells);
    }

    /**
     * Declares a box panel.
     *
     * @param axis
     * The panel's axis.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static JPanel boxPanel(int axis, Cell<?>... cells) {
        JPanel panel = new JPanel();

        BoxLayout boxLayout = new BoxLayout(panel, axis);

        panel.setLayout(boxLayout);

        return populate(panel, cells);
    }

    private static JPanel populate(JPanel panel, Cell<?>... cells) {
        if (cells == null) {
            throw new IllegalArgumentException();
        }

        for (var i = 0; i < cells.length; i++) {
            Cell<?> cell = cells[i];

            panel.add(cell.component, cell.constraints);
        }

        return panel;
    }
}
