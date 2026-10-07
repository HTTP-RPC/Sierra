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

package org.httprpc.sierra.test;

import com.formdev.flatlaf.FlatLightLaf;
import org.httprpc.sierra.BasicComboBoxModel;
import org.httprpc.sierra.BasicListCellRenderer;
import org.httprpc.sierra.BasicListModel;
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.UILoader;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import static org.httprpc.kilo.util.Collections.*;

public class ListTest extends JFrame implements Runnable {
    public static class ListItem {
        private int value;
        private String label;

        public ListItem(int value, String label) {
            this.value = value;
            this.label = label;
        }

        public int getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }

    private @Outlet UILoader.JxList<ListItem> list = null;
    private @Outlet UILoader.JxComboBox<ListItem> comboBox = null;

    private ListTest() {
        super("List Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "ListTest.xml"));

        var listItems = listOf(
            new ListItem(1, "One"),
            new ListItem(2, "Two"),
            new ListItem(3, "Three")
        );

        list.setModel(new BasicListModel<>(listItems));

        list.setCellRenderer(new BasicListCellRenderer<>(ListItem::getLabel));
        list.setLabelMapper(ListItem::getLabel);

        list.addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }

            System.out.println(list.getSelectedValue().getValue());
        });

        comboBox.setModel(new BasicComboBoxModel<>(listItems));

        comboBox.setRenderer(new BasicListCellRenderer<>(ListItem::getLabel));
        comboBox.setLabelMapper(ListItem::getLabel);

        comboBox.addActionListener(event -> System.out.println(((ListItem)comboBox.getSelectedItem()).getValue()));

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ListTest());
    }
}
