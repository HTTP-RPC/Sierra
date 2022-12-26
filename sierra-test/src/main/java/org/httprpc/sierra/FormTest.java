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
        var scrollPane = new JScrollPane(column(4, true,
            row(4, true,
                cell(new JLabel("First Name")),
                cell(new JTextField(null, 12)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("Last Name")),
                cell(new JTextField(null, 12)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("Street Address")),
                cell(new JTextField(null, 24)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("City")),
                cell(new JTextField(null, 16)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("State")),
                cell(new JTextField(null, 12)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("Postal Code")),
                cell(new JTextField(null, 8)).alignXTo(0)
            ),
            row(
                cell(new JSeparator())
            ),
            row(4, true,
                cell(new JLabel("Email Address")),
                cell(new JTextField(null, 16)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("Home Phone")),
                cell(new JTextField(null, 12)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("Mobile Phone")),
                cell(new JTextField(null, 12)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("Fax")),
                cell(new JTextField(null, 12)).alignXTo(0)
            ),
            row(
                cell(new JSeparator())
            ),
            row(4, true,
                cell(new JLabel("Field 1")),
                cell(new JTextField(null, 12)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("Field 2")),
                cell(new JTextField(null, 12)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("Field 3")),
                cell(new JTextField(null, 12)).alignXTo(0)
            ),
            row(4, true,
                cell(new JLabel("Field 4")),
                cell(new JTextField(null, 12)).alignXTo(0)
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
