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
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.UILoader;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;

public class MenuTest extends JFrame implements Runnable {
    private @Outlet JMenuItem oneMenuItem = null;
    private @Outlet JMenuItem twoMenuItem = null;
    private @Outlet JMenuItem threeMenuItem = null;

    private MenuTest() {
        super("Menu Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "MenuTest.xml"));

        ActionListener actionListener = event -> System.out.println(((JMenuItem)event.getSource()).getName());

        oneMenuItem.addActionListener(actionListener);
        twoMenuItem.addActionListener(actionListener);
        threeMenuItem.addActionListener(actionListener);

        setSize(640, 480);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new MenuTest());
    }
}
