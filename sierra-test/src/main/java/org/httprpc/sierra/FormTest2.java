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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagConstraints;
import java.awt.KeyboardFocusManager;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.center;
import static org.httprpc.sierra.SwingUIBuilder.gridBagPanel;
import static org.httprpc.sierra.SwingUIBuilder.row;

public class FormTest2 extends JFrame implements Runnable {
    private FormTest2() {
        super("Form Test 2");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var contentPane = borderPanel(center(new JScrollPane(getViewportView())));

        contentPane.setBorder(new EmptyBorder(8, 8, 8, 8));

        setContentPane(contentPane);

        setSize(480, 360);
        setVisible(true);
    }

    private JComponent getViewportView() {
        var viewportView = gridBagPanel(4, 4,
            row(
                cell(new JLabel("First Name")),
                cell(new JTextField(null, 12))
            ),
            row(
                cell(new JLabel("Last Name")),
                cell(new JTextField(null, 12))
            ),
            row(
                cell(new JLabel("Street Address")),
                cell(new JTextField(null, 24))
            ),
            row(
                cell(new JLabel("City")),
                cell(new JTextField(null, 16))
            ),
            row(
                cell(new JLabel("State")),
                cell(new JTextField(null, 12))
            ),
            row(
                cell(new JLabel("Postal Code")),
                cell(new JTextField(null, 8))
            ),
            row(
                cell(new JSeparator())
                    .fill(GridBagConstraints.HORIZONTAL)
            ),
            row(
                cell(new JLabel("Email Address")),
                cell(new JTextField(null, 16))
            ),
            row(
                cell(new JLabel("Home Phone")),
                cell(new JTextField(null, 12))
            ),
            row(
                cell(new JLabel("Mobile Phone")),
                cell(new JTextField(null, 12))
            ),
            row(
                cell(new JLabel("Fax")),
                cell(new JTextField(null, 12))
            ),
            row(
                cell(new JSeparator())
                    .fill(GridBagConstraints.HORIZONTAL)
            ),
            row(
                cell(new JLabel("Field 1")),
                cell(new JTextField(null, 12))
            ),
            row(
                cell(new JLabel("Field 2")),
                cell(new JTextField(null, 12))
            ),
            row(
                cell(new JLabel("Field 3")),
                cell(new JTextField(null, 12))
            ),
            row(
                cell(new JLabel("Field 4")),
                cell(new JTextField(null, 12))
            )
        );

        viewportView.setBorder(new EmptyBorder(8, 8, 8, 8));

        return viewportView;
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        KeyboardFocusManager.setCurrentKeyboardFocusManager(new ScrollingKeyboardFocusManager());

        SwingUtilities.invokeLater(new FormTest2());
    }
}
