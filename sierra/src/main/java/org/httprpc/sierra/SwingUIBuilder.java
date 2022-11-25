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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Class for declaratively constructing a Swing component hierarchy.
 */
public class SwingUIBuilder {
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
         * Applies a fill to a grid bag panel cell.
         *
         * @param fill
         * The fill value.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> fill(int fill) {
            getGridBagConstraints().fill = fill;

            return this;
        }

        /**
         * Applies an anchor to a grid bag panel cell.
         *
         * @param anchor
         * The anchor value.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> anchorTo(int anchor) {
            getGridBagConstraints().anchor = anchor;

            return this;
        }

        /**
         * Applies a horizontal weight to a grid bag panel cell.
         *
         * @param weight
         * The weight value.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> weightXBy(double weight) {
            getGridBagConstraints().weightx = weight;

            return this;
        }

        /**
         * Applies a vertical weight to a grid bag panel cell.
         *
         * @param weight
         * The weight value.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> weightYBy(double weight) {
            getGridBagConstraints().weighty = weight;

            return this;
        }

        /**
         * Applies horizontal padding to a grid bag panel cell.
         *
         * @param width
         * The padding width.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> padXBy(int width) {
            getGridBagConstraints().ipadx = width;

            return this;
        }

        /**
         * Applies vertical padding to a grid bag panel cell.
         *
         * @param height
         * The padding height.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> padYBy(int height) {
            getGridBagConstraints().ipady = height;

            return this;
        }

        /**
         * Applies insets to a grid bag panel cell.
         *
         * @param top
         * The top inset.
         *
         * @param left
         * The left inset.
         *
         * @param bottom
         * The bottom inset.
         *
         * @param right
         * The right inset.
         *
         * @return
         * The cell instance.
         */
        public Cell<C> insetBy(int top, int left, int bottom, int right) {
            getGridBagConstraints().insets = new Insets(top, left, bottom, right);

            return this;
        }

        /**
         * Applies a column span to a grid bag panel cell.
         *
         * @param count
         * The number of columns to span (see {@link GridBagConstraints#gridwidth}).
         *
         * @return
         * The cell instance.
         */
        public Cell<C> spanColumns(int count) {
            getGridBagConstraints().gridwidth = count;

            return this;
        }

        private GridBagConstraints getGridBagConstraints() {
            if (constraints == null) {
                constraints = new GridBagConstraints();
            }

            return (GridBagConstraints)constraints;
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
    public static <C extends Component> Cell<C> cell(C component) {
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
     * Declares a "center" cell for a border panel.
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
    public static <C extends Component> Cell<C> center(C component) {
        return cell(component).constrainedBy(BorderLayout.CENTER);
    }

    /**
     * Declares a "north" cell for a border panel.
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
    public static <C extends Component> Cell<C> north(C component) {
        return cell(component).constrainedBy(BorderLayout.NORTH);
    }

    /**
     * Declares a "south" cell for a border panel.
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
    public static <C extends Component> Cell<C> south(C component) {
        return cell(component).constrainedBy(BorderLayout.SOUTH);
    }

    /**
     * Declares an "east" cell for a border panel.
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
    public static <C extends Component> Cell<C> east(C component) {
        return cell(component).constrainedBy(BorderLayout.EAST);
    }

    /**
     * Declares a "west" cell for a border panel.
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
    public static <C extends Component> Cell<C> west(C component) {
        return cell(component).constrainedBy(BorderLayout.WEST);
    }

    /**
     * Declares a "page start" cell for a border panel.
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
    public static <C extends Component> Cell<C> pageStart(C component) {
        return cell(component).constrainedBy(BorderLayout.PAGE_START);
    }

    /**
     * Declares a "page end" cell for a border panel.
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
    public static <C extends Component> Cell<C> pageEnd(C component) {
        return cell(component).constrainedBy(BorderLayout.PAGE_END);
    }

    /**
     * Declares a "line start" cell for a border panel.
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
    public static <C extends Component> Cell<C> lineStart(C component) {
        return cell(component).constrainedBy(BorderLayout.LINE_START);
    }

    /**
     * Declares a "line end" cell for a border panel.
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
    public static <C extends Component> Cell<C> lineEnd(C component) {
        return cell(component).constrainedBy(BorderLayout.LINE_END);
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

    /**
     * Declares a horizontal strut cell for a box panel.
     *
     * @param width
     * The strut width.
     *
     * @return
     * The cell instance.
     */
    public static Cell<Component> horizontalStrut(int width) {
        return cell(Box.createHorizontalStrut(width));
    }

    /**
     * Declares a vertical strut cell for a box panel.
     *
     * @param height
     * The strut height.
     *
     * @return
     * The cell instance.
     */
    public static Cell<Component> verticalStrut(int height) {
        return cell(Box.createVerticalStrut(height));
    }

    /**
     * Declares a horizontal glue cell for a box panel.
     *
     * @return
     * The cell instance.
     */
    public static Cell<Component> horizontalGlue() {
        return cell(Box.createHorizontalGlue());
    }

    /**
     * Declares a vertical glue cell for a box panel.
     *
     * @return
     * The cell instance.
     */
    public static Cell<Component> verticalGlue() {
        return cell(Box.createVerticalGlue());
    }

    /**
     * Declares a grid bag panel.
     *
     * @param rows
     * The panel's rows.
     *
     * @return
     * The panel instance.
     */
    @SafeVarargs
    public static JPanel gridBagPanel(Cell<? extends Component>[]... rows) {
        return gridBagPanel(0, 0, rows);
    }

    /**
     * Declares a grid bag panel.
     *
     * @param hgap
     * The horizontal spacing between cells.
     *
     * @param vgap
     * The vertical spacing between cells.
     *
     * @param rows
     * The panel's rows.
     *
     * @return
     * The panel instance.
     */
    @SafeVarargs
    public static JPanel gridBagPanel(int hgap, int vgap, Cell<? extends Component>[]... rows) {
        if (rows == null) {
            throw new IllegalArgumentException();
        }

        JPanel panel = new JPanel(new GridBagLayout());

        var cells = new LinkedList<Cell<? extends Component>>();

        for (int y = 0; y < rows.length; y++)  {
            Cell<? extends Component>[] row = rows[y];

            for (int x = 0; x < row.length; x++) {
                Cell<? extends Component> cell = row[x];

                var gridBagConstraints = cell.getGridBagConstraints();

                gridBagConstraints.gridx = x;
                gridBagConstraints.gridy = y;

                if (x > 0) {
                    gridBagConstraints.insets.left += hgap;
                }

                if (y > 0) {
                    gridBagConstraints.insets.top += vgap;
                }

                cells.add(cell);
            }
        }

        return populate(panel, cells.toArray(new Cell<?>[0]));
    }

    /**
     * Declares a row of cells for a grid bag panel.
     *
     * @param cells
     * The row's cells.
     *
     * @return
     * The row of cells.
     */
    @SafeVarargs
    public static Cell<? extends Component>[] row(Cell<? extends Component>... cells) {
        if (cells == null) {
            throw new IllegalArgumentException();
        }

        return cells;
    }

    private static JPanel populate(JPanel panel, Cell<?>... cells) {
        if (cells == null) {
            throw new IllegalArgumentException();
        }

        for (var i = 0; i < cells.length; i++) {
            Cell<?> cell = cells[i];

            panel.add(cell.component, cell.constraints);
        }

        panel.setOpaque(false);

        return panel;
    }
}
