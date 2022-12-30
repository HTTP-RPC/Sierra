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
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.util.function.Consumer;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.row;

public class WeightTest extends JFrame implements Runnable {
    private static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

    private WeightTest() {
        super("Weight Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        Consumer<TextPane> textPaneStyle = textPane -> {
            textPane.setWrapText(true);
            textPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        };

        setContentPane(column(4, true,
            row(
                cell(new JLabel("abcdefg")),
                cell(new TextPane(TEXT, true)).weightBy(1).with(textPaneStyle),
                cell(new TextPane(TEXT, true)).weightBy(1).with(textPaneStyle)
            ),
            row(
                cell(new JLabel("hijklmnop")),
                cell(new TextPane(TEXT, true)).weightBy(3).with(textPaneStyle),
                cell(new TextPane(TEXT, true)).weightBy(1).with(textPaneStyle)
            )
        ).with(columnPanel -> columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        setSize(480, 360);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new WeightTest());
    }
}
