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

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.TaskExecutor;
import org.httprpc.sierra.UILoader;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.util.concurrent.Executors;

import static org.httprpc.kilo.util.Optionals.*;

public class RootPaneTest extends JFrame implements Runnable {
    private static final TaskExecutor taskExecutor = new TaskExecutor(Executors.newCachedThreadPool(runnable -> {
        var thread = new Thread(runnable);

        thread.setDaemon(true);

        return thread;
    }));

    private @Outlet JList<String> list = null;

    private @Outlet JTabbedPane tabbedPane = null;

    private RootPaneTest() {
        super("Root Pane Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "RootPaneTest.xml"));

        list.setModel(new DefaultListModel<>() {{
            addElement("One");
            addElement("Two");
            addElement("Three");
        }});

        tabbedPane.add("Tab 2", new TabPane(taskExecutor));
        tabbedPane.add("Tab 3", new TabPane(taskExecutor));
        tabbedPane.add("Tab 4", new TabPane(taskExecutor));

        setSize(480, 320);
        setVisible(true);
    }

    public static void main(String[] args) {
        var dark = coalesce(map(System.getProperty("dark"), Boolean::valueOf), () -> false);

        if (dark) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }

        SwingUtilities.invokeLater(new RootPaneTest());
    }
}
