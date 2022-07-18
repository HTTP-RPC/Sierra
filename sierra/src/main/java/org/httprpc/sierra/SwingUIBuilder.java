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
import java.awt.FlowLayout;
import java.util.function.Consumer;

public class SwingUIBuilder {
    public static class Cell {
        private JComponent component;
        private Object constraints;
        private Consumer<JComponent> handler;

        private Cell(JComponent component, Object constraints, Consumer<JComponent> handler) {
            this.component = component;
            this.constraints = constraints;
            this.handler = handler;
        }

        public JComponent getComponent() {
            return component;
        }

        public Object getConstraints() {
            return constraints;
        }

        public Consumer<JComponent> getHandler() {
            return handler;
        }
    }

    public static <C extends JComponent> Cell cell(C component) {
        return cell(component, null);
    }

    public static <C extends JComponent> Cell cell(C component, Object constraints) {
        return cell(component, null, null);
    }

    @SuppressWarnings("unchecked")
    public static <C extends JComponent> Cell cell(C component, Object constraints, Consumer<C> handler) {
        if (component == null) {
            throw new IllegalArgumentException();
        }

        return new Cell(component, constraints, (Consumer<JComponent>)handler);
    }

    public static JPanel flowPanel(Cell... cells) {
        JPanel panel = new JPanel();

        // TODO align, hgap, vgap, alignOnBaseline
        FlowLayout flowLayout = new FlowLayout();

        panel.setLayout(flowLayout);

        return addComponents(panel, cells);
    }

    public static JPanel horizontalBoxPanel(Cell... cells) {
        return boxPanel(BoxLayout.X_AXIS, cells);
    }

    public static JPanel verticalBoxPanel(Cell... cells) {
        return boxPanel(BoxLayout.Y_AXIS, cells);
    }

    private static JPanel boxPanel(int axis, Cell... cells) {
        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, axis));

        return addComponents(panel, cells);
    }

    public static JPanel borderPanel(Cell... cells) {
        JPanel panel = new JPanel();

        // TODO hgap, vgap
        BorderLayout borderLayout = new BorderLayout();

        panel.setLayout(borderLayout);

        return addComponents(panel, cells);
    }

    private static <C extends JComponent> JPanel addComponents(JPanel panel, Cell... cells) {
        if (cells == null) {
            throw new IllegalArgumentException();
        }

        for (var i = 0; i < cells.length; i++) {
            Cell cell = cells[i];

            panel.add(cell.component, cell.constraints);

            cell.handler.accept(cell.component);
        }

        return panel;
    }
}
