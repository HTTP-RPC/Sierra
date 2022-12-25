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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.util.function.Consumer;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.glue;
import static org.httprpc.sierra.UIBuilder.row;

public class GridTest extends JFrame implements Runnable {
    private static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

    private GridTest() {
        super("Grid Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        Consumer<JTextArea> textAreaStyle = textArea -> {
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        };

        var contentPane = new JScrollPane(column(4,
            cell(new JTextArea(TEXT)).with(textAreaStyle),
            column(4, true,
                row(4, true,
                    cell(new JLabel("abcdefg")),
                    column(4,
                        cell(new JTextArea(TEXT)).with(textAreaStyle),
                        cell(new JTextArea(TEXT)).with(textAreaStyle)
                    ).weightBy(1.0)
                ),
                row(4, true,
                    cell(new JLabel("xyz")),
                    column(4,
                        cell(new JTextArea(TEXT)).with(textAreaStyle),
                        cell(new JTextArea(TEXT)).with(textAreaStyle)
                    ).weightBy(1.0)
                )
            ),
            row(4, true,
                column(4,
                    cell(new JTextArea(TEXT)).with(textAreaStyle),
                    glue()
                ).weightBy(2.0),
                cell(new JTextArea(TEXT)).weightBy(1.0).with(textAreaStyle)
            )
        ).with(columnPanel -> {
            columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
            columnPanel.setScrollableTracksViewportWidth(true);
        }).getComponent());

        contentPane.setBorder(null);

        setContentPane(contentPane);

        setSize(320, 640);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new GridTest());
    }
}
