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
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Provides static factory methods for declaratively constructing a component
 * hierarchy.
 */
public class SwingUIBuilder {
    /**
     * Provides a fluent API for configuring a sub-component.
     */
    public static class Cell<C extends Component> {
        private C component;
        private Object constraints;

        private double weightx = 0.0;
        private double weighty = 0.0;

        private int anchor = GridBagConstraints.BASELINE;
        private int fill = GridBagConstraints.HORIZONTAL;

        private Cell(C component) {
            this(component, null);
        }

        private Cell(C component, Object constraints) {
            this.component = component;
            this.constraints = constraints;
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
            this.weightx = weight;

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
            this.weighty = weight;

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
            this.anchor = anchor;

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
            this.fill = fill;

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

    /**
     * A scrollable panel.
     */
    public static class ScrollablePanel extends JPanel implements Scrollable {
        private boolean scrollableTracksViewportWidth = false;
        private boolean scrollableTracksViewportHeight = false;

        private ScrollablePanel(LayoutManager layoutManager) {
            super(layoutManager);
        }

        /**
         * Returns the panel's preferred scrollable viewport size.
         * {@inheritDoc}
         */
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        /**
         * Returns the panel's scrollable unit increment.
         * {@inheritDoc}
         */
        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            switch (orientation) {
                case SwingConstants.VERTICAL: {
                    return visibleRect.height / 10;
                }

                case SwingConstants.HORIZONTAL: {
                    return visibleRect.width / 10;
                }

                default: {
                    throw new UnsupportedOperationException();
                }
            }
        }

        /**
         * Returns the panel's scrollable block increment.
         * {@inheritDoc}
         */
        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            switch (orientation) {
                case SwingConstants.VERTICAL: {
                    return visibleRect.height;
                }

                case SwingConstants.HORIZONTAL: {
                    return visibleRect.width;
                }

                default: {
                    throw new UnsupportedOperationException();
                }
            }
        }

        /**
         * Indicates that the panel tracks viewport width.
         * {@inheritDoc}
         */
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return scrollableTracksViewportWidth;
        }

        /**
         * Toggles viewport width tracking.
         *
         * @param scrollableTracksViewportWidth
         * {@code true} to track viewport width; {@code false}, otherwise.
         */
        public void setScrollableTracksViewportWidth(boolean scrollableTracksViewportWidth) {
            this.scrollableTracksViewportWidth = scrollableTracksViewportWidth;
        }

        /**
         * Indicates that the panel tracks viewport height.
         * {@inheritDoc}
         */
        @Override
        public boolean getScrollableTracksViewportHeight() {
            return scrollableTracksViewportHeight;
        }

        /**
         * Toggles viewport height tracking.
         *
         * @param scrollableTracksViewportHeight
         * {@code true} to track viewport height; {@code false}, otherwise.
         */
        public void setScrollableTracksViewportHeight(boolean scrollableTracksViewportHeight) {
            this.scrollableTracksViewportHeight = scrollableTracksViewportHeight;
        }
    }

    private SwingUIBuilder() {
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
        if (component == null) {
            throw new IllegalArgumentException();
        }

        return new Cell<>(component);
    }

    /**
     * Declares a border panel.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static ScrollablePanel borderPanel(Cell<?>... cells) {
        return borderPanel(0, 0, cells);
    }

    /**
     * Declares a border panel.
     *
     * @param hgap
     * The horizontal gap between components.
     *
     * @param vgap
     * The vertical gap between components.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static ScrollablePanel borderPanel(int hgap, int vgap, Cell<?>... cells) {
        return populate(new ScrollablePanel(new BorderLayout(hgap, vgap)), cells);
    }

    /**
     * Declares a "center" cell for a border panel.
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
    public static <C extends Component> Cell<C> center(C component) {
        return new Cell<>(component, BorderLayout.CENTER);
    }

    /**
     * Declares a "page start" cell for a border panel.
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
    public static <C extends Component> Cell<C> pageStart(C component) {
        return new Cell<>(component, BorderLayout.PAGE_START);
    }

    /**
     * Declares a "page end" cell for a border panel.
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
    public static <C extends Component> Cell<C> pageEnd(C component) {
        return new Cell<>(component, BorderLayout.PAGE_END);
    }

    /**
     * Declares a "line start" cell for a border panel.
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
    public static <C extends Component> Cell<C> lineStart(C component) {
        return new Cell<>(component, BorderLayout.LINE_START);
    }

    /**
     * Declares a "line end" cell for a border panel.
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
    public static <C extends Component> Cell<C> lineEnd(C component) {
        return new Cell<>(component, BorderLayout.LINE_END);
    }

    /**
     * Declares a horizontal box panel.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static ScrollablePanel horizontalBoxPanel(Cell<?>... cells) {
        return boxPanel(BoxLayout.X_AXIS, cells);
    }

    /**
     * Declares a vertical box panel.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static ScrollablePanel verticalBoxPanel(Cell<?>... cells) {
        return boxPanel(BoxLayout.Y_AXIS, cells);
    }

    private static ScrollablePanel boxPanel(int axis, Cell<?>... cells) {
        var panel = new ScrollablePanel(null);

        var boxLayout = new BoxLayout(panel, axis);

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
        return new Cell<>(Box.createHorizontalStrut(width));
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
        return new Cell<>(Box.createVerticalStrut(height));
    }

    /**
     * Declares a horizontal glue cell for a box panel.
     *
     * @return
     * The cell instance.
     */
    public static Cell<Component> horizontalGlue() {
        return new Cell<>(Box.createHorizontalGlue());
    }

    /**
     * Declares a vertical glue cell for a box panel.
     *
     * @return
     * The cell instance.
     */
    public static Cell<Component> verticalGlue() {
        return new Cell<>(Box.createVerticalGlue());
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
    public static ScrollablePanel gridBagPanel(Cell<? extends Component>[]... rows) {
        return gridBagPanel(4, 4, rows);
    }

    /**
     * Declares a grid bag panel.
     *
     * @param hgap
     * The horizontal gap between components.
     *
     * @param vgap
     * The vertical gap between components.
     *
     * @param rows
     * The panel's rows.
     *
     * @return
     * The panel instance.
     */
    @SafeVarargs
    public static ScrollablePanel gridBagPanel(int hgap, int vgap, Cell<? extends Component>[]... rows) {
        if (rows == null) {
            throw new IllegalArgumentException();
        }

        var panel = new ScrollablePanel(new GridBagLayout());

        var cells = new LinkedList<Cell<? extends Component>>();

        for (var y = 0; y < rows.length; y++) {
            var row = rows[y];

            for (var x = 0; x < row.length; x++) {
                var cell = row[x];

                if (cell.constraints != null) {
                    throw new IllegalStateException();
                }

                var constraints = new GridBagConstraints();

                constraints.gridx = x;
                constraints.gridy = y;

                if (x == row.length - 1) {
                    constraints.gridwidth = GridBagConstraints.REMAINDER;
                }

                constraints.weightx = cell.weightx;
                constraints.weighty = cell.weighty;

                constraints.anchor = cell.anchor;
                constraints.fill = cell.fill;

                if (x > 0) {
                    constraints.insets.left += hgap;
                }

                if (y > 0) {
                    constraints.insets.top += vgap;
                }

                cell.constraints = constraints;

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

    /**
     * Declares a flow panel.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static ScrollablePanel flowPanel(Cell<?>... cells) {
        return flowPanel(FlowLayout.LEADING, 4, 4, true, cells);
    }

    /**
     * Declares a flow panel.
     *
     * @param align
     * The alignment value.
     *
     * @param hgap
     * The horizontal gap between components and the borders of the container.
     *
     * @param vgap
     * The vertical gap between components and the borders of the container.
     *
     * @param alignOnBaseline
     * Indicates that the panel's content should be aligned to its baseline.
     *
     * @param cells
     * The panel's cells.
     *
     * @return
     * The panel instance.
     */
    public static ScrollablePanel flowPanel(int align, int hgap, int vgap, boolean alignOnBaseline, Cell<?>... cells) {
        var flowLayout = new FlowLayout(align, hgap, vgap);

        flowLayout.setAlignOnBaseline(alignOnBaseline);

        return populate(new ScrollablePanel(flowLayout), cells);
    }

    private static ScrollablePanel populate(ScrollablePanel panel, Cell<?>... cells) {
        if (cells == null) {
            throw new IllegalArgumentException();
        }

        for (var i = 0; i < cells.length; i++) {
            var cell = cells[i];

            panel.add(cell.component, cell.constraints);
        }

        panel.setOpaque(false);

        return panel;
    }
}
