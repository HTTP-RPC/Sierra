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
import org.httprpc.sierra.MenuButton;
import org.httprpc.sierra.UILoader;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.util.ResourceBundle;

public class MenuButtonTest extends JFrame implements Runnable {
    private MenuButton menuButton = null;

    private JCheckBox focusableCheckBox = null;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(MenuButtonTest.class.getName());

    private MenuButtonTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "menu-button-test.xml", resourceBundle));

        var popupMenu = new JPopupMenu();

        popupMenu.add(new JMenuItem(resourceBundle.getString("item1")));
        popupMenu.add(new JMenuItem(resourceBundle.getString("item2")));
        popupMenu.add(new JMenuItem(resourceBundle.getString("item3")));

        menuButton.setPopupMenu(popupMenu);

        focusableCheckBox.addActionListener(event -> toggleFocusable());
        focusableCheckBox.setSelected(true);

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
