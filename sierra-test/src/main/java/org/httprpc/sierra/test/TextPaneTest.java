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
import org.httprpc.sierra.TextPane;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class TextPaneTest extends JFrame implements Runnable {
    private TextPaneTest() {
        super("Text Pane Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var textPane = new TextPane("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");

        textPane.setBackground(Color.WHITE);
        textPane.setOpaque(true);
        textPane.setWrapText(true);
        textPane.setBorder(new LineBorder(Color.LIGHT_GRAY, 16));

        setContentPane(textPane);

        setSize(320, 240);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new TextPaneTest());
    }
}
