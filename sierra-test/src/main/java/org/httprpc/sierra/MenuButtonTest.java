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

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.glue;
import static org.httprpc.sierra.UIBuilder.row;

public class MenuButtonTest extends JFrame implements Runnable {
    private MenuButton menuButton;

    private JCheckBox focusableCheckBox;

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
                    cell(new MenuButton("Show Menu")).with(menuButton -> {
                        var popupMenu = new JPopupMenu();

                        popupMenu.add(new JMenuItem("Item 1"));
                        popupMenu.add(new JMenuItem("Item 2"));
                        popupMenu.add(new JMenuItem("Item 3"));

                        menuButton.setPopupMenu(popupMenu);

                        this.menuButton = menuButton;
                    }),
                    cell(new JCheckBox("Focusable")).with(checkBox -> {
                        checkBox.addActionListener(event -> toggleFocusable());
                        checkBox.setSelected(true);

                        focusableCheckBox = checkBox;
                    })
                ),
                glue()
            ),
            glue()
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        setSize(480, 360);
        setVisible(true);
    }

    private void toggleFocusable() {
        menuButton.setFocusable(focusableCheckBox.isSelected());
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new MenuButtonTest());
    }
}