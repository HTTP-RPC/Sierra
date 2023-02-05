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

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.glue;
import static org.httprpc.sierra.UIBuilder.row;

public class MenuButtonTest extends JFrame implements Runnable {
    private JTextField textField;

    private MenuButtonTest() {
        super("Menu Button Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(column(
            glue(),
            row(
                glue(),
                row(8,
                    cell(new MenuButton("Show Menu")).with(button -> {
                        var popupMenu = new JPopupMenu();

                        for (var i = 0; i < 3; i++) {
                            var menuItem = new JMenuItem(String.format("Item %d", i + 1));

                            menuItem.addActionListener(event -> textField.setText(menuItem.getText()));

                            popupMenu.add(menuItem);
                        }

                        button.setPopupMenu(popupMenu);
                    }),
                    cell(new JTextField(12)).with(textField -> this.textField = textField)
                ),
                glue()
            ),
            glue()
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        setSize(480, 360);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new MenuButtonTest());
    }
}