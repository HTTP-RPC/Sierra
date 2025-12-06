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
import com.formdev.flatlaf.FlatLightLaf;
import org.httprpc.sierra.HorizontalAlignment;
import org.httprpc.sierra.TextPane;
import org.httprpc.sierra.UILoader;
import org.httprpc.sierra.VerticalAlignment;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

public class PreviewFrame extends JFrame {
    private Path path;

    private static final String REFRESH_ACTION_KEY = "refresh";

    private static final String LIGHT_ACTION_KEY = "light";
    private static final String DARK_ACTION_KEY = "dark";

    public PreviewFrame(Path path) {
        if (path == null) {
            throw new IllegalArgumentException();
        }

        setTitle(path.getFileName().toString());

        this.path = path;

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

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, shortcutModifier, false), LIGHT_ACTION_KEY);
        actionMap.put(LIGHT_ACTION_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                FlatLightLaf.setup();

                refresh();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcutModifier, false), DARK_ACTION_KEY);
        actionMap.put(DARK_ACTION_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                FlatDarkLaf.setup();

                refresh();
            }
        });

        refresh();
    }

    private void refresh() {
        var contentPane = getContentPane();

        contentPane.removeAll();

        JComponent component;
        try {
            component = UILoader.load(path);
        } catch (Exception exception) {
            var textPane = new TextPane(exception.getMessage());

            textPane.setWrapText(true);
            textPane.setHorizontalAlignment(HorizontalAlignment.CENTER);
            textPane.setVerticalAlignment(VerticalAlignment.CENTER);

            textPane.setBorder(new EmptyBorder(8, 8, 8, 8));

            component = textPane;
        }

        contentPane.add(component);

        revalidate();
    }
}
