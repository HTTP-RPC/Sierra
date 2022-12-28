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
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.KeyboardFocusManager;
import java.util.function.Consumer;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.row;

public class FormTest extends JFrame implements Runnable {
    private FormTest() {
        super("Form Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        Consumer<JLabel> labelStyle = label -> label.setAlignmentX(0.5f);
        Consumer<JTextField> textFieldStyle = textField -> textField.setAlignmentX(0.5f);

        var scrollPane = new JScrollPane(column(4, true,
            row(true,
                cell(new JLabel("First Name")).with(labelStyle),
                cell(new JTextField(null, 12)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("Last Name")).with(labelStyle),
                cell(new JTextField(null, 12)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("Street Address")).with(labelStyle),
                cell(new JTextField(null, 24)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("City")).with(labelStyle),
                cell(new JTextField(null, 16)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("State")).with(labelStyle),
                cell(new JTextField(null, 12)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("Postal Code")).with(labelStyle),
                cell(new JTextField(null, 8)).with(textFieldStyle)
            ),

            cell(new JSeparator()),

            row(true,
                cell(new JLabel("Email Address")).with(labelStyle),
                cell(new JTextField(null, 16)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("Home Phone")).with(labelStyle),
                cell(new JTextField(null, 12)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("Mobile Phone")).with(labelStyle),
                cell(new JTextField(null, 12)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("Fax")).with(labelStyle),
                cell(new JTextField(null, 12)).with(textFieldStyle)
            ),

            cell(new JSeparator()),

            row(true,
                cell(new JLabel("Field 1")).with(labelStyle),
                cell(new JTextField(null, 12)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("Field 2")).with(labelStyle),
                cell(new JTextField(null, 12)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("Field 3")).with(labelStyle),
                cell(new JTextField(null, 12)).with(textFieldStyle)
            ),
            row(true,
                cell(new JLabel("Field 4")).with(labelStyle),
                cell(new JTextField(null, 12)).with(textFieldStyle)
            )
        ).with(columnPanel -> columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        scrollPane.setBorder(null);

        setContentPane(scrollPane);

        setSize(480, 360);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        KeyboardFocusManager.setCurrentKeyboardFocusManager(new ScrollingKeyboardFocusManager());

        SwingUtilities.invokeLater(new FormTest());
    }
}
