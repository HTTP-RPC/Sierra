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

package org.httprpc.sierra.tools.previewer;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.httprpc.sierra.ColumnPanel;
import org.httprpc.sierra.HorizontalAlignment;
import org.httprpc.sierra.Spacer;
import org.httprpc.sierra.TextPane;
import org.httprpc.sierra.UILoader;
import org.httprpc.sierra.VerticalAlignment;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

public class Previewer extends JFrame implements Runnable {
    private Path path;

    private static final String REFRESH_ACTION_KEY = "refresh";
    private static final String PACK_ACTION_KEY = "pack";
    private static final String TOGGLE_DARK_MODE_ACTION_KEY = "toggle-dark-mode";

    private Previewer(Path path) {
        this.path = path;

        setTitle(path.getFileName().toString());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = rootPane.getActionMap();

        var shortcutModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, shortcutModifier, false), REFRESH_ACTION_KEY);
        actionMap.put(REFRESH_ACTION_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                refresh();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, shortcutModifier, false), PACK_ACTION_KEY);
        actionMap.put(PACK_ACTION_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                pack();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcutModifier, false), TOGGLE_DARK_MODE_ACTION_KEY);
        actionMap.put(TOGGLE_DARK_MODE_ACTION_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (FlatLaf.isLafDark()) {
                    FlatLightLaf.setup();
                } else {
                    FlatDarkLaf.setup();
                }

                refresh();
            }
        });
    }

    @Override
    public void run() {
        var minimumScreenWidth = Integer.MAX_VALUE;
        var minimumScreenHeight = Integer.MAX_VALUE;

        var screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        for (var i = 0; i < screenDevices.length; i++) {
            var bounds = screenDevices[i].getDefaultConfiguration().getBounds();

            minimumScreenWidth = Math.min(minimumScreenWidth, bounds.width);
            minimumScreenHeight = Math.min(minimumScreenHeight, bounds.height);
        }

        setSize((int)Math.ceil(minimumScreenWidth * 0.75), (int)Math.ceil(minimumScreenHeight * 0.75));

        setLocationRelativeTo(null);
        setVisible(true);

        refresh();
    }

    private void refresh() {
        JComponent component;
        try {
            component = UILoader.load(path);
        } catch (Exception exception) {
            var columnPanel = new ColumnPanel();

            columnPanel.setSpacing(8);
            columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

            columnPanel.add(new Spacer(), 1.0);

            columnPanel.add(new JLabel(UIManager.getIcon("OptionPane.errorIcon"), SwingConstants.CENTER));

            var messageTextPane = new TextPane(exception.getMessage());

            messageTextPane.setHorizontalAlignment(HorizontalAlignment.CENTER);
            messageTextPane.setVerticalAlignment(VerticalAlignment.CENTER);

            columnPanel.add(messageTextPane);

            columnPanel.add(new Spacer(), 1.0);

            component = columnPanel;
        }

        setContentPane(component);

        revalidate();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Path is required.");
            return;
        }

        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new Previewer(Path.of(System.getProperty("user.dir")).resolve(args[0])));
    }
}
