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
import org.httprpc.sierra.ScrollingKeyboardFocusManager;
import org.httprpc.sierra.UILoader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.KeyboardFocusManager;

public class BlockLayoutTest extends JFrame implements Runnable {
    private JButton submitButton = null;

    private BlockLayoutTest() {
        super("Block Layout Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var scrollPane = new JScrollPane(UILoader.load(this, "BlockLayoutTest.xml"));

        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(null);

        setContentPane(scrollPane);

        submitButton.addActionListener(event -> submitForm());

        rootPane.setDefaultButton(submitButton);

        setSize(360, 480);
        setVisible(true);
    }

    private void submitForm() {
        JOptionPane.showMessageDialog(this, "Form submitted.");
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        KeyboardFocusManager.setCurrentKeyboardFocusManager(new ScrollingKeyboardFocusManager());

        SwingUtilities.invokeLater(new BlockLayoutTest());
    }
}
