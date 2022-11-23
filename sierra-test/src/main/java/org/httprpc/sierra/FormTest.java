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

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.gridBagPanel;
import static org.httprpc.sierra.SwingUIBuilder.row;

public class FormTest extends JFrame implements Runnable {
    private FormTest() {
        super("Form Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(gridBagPanel(
            row(
                cell(new JLabel("Username")),
                cell(new JTextField())
            ),
            row(
                cell(new JLabel("Password")),
                cell(new JTextField())
            ),
            row(
                cell(new JCheckBox("Remember Me"))
                    .anchorTo(GridBagConstraints.LINE_START)
                    .spanColumns(2)
            )
        ));

        setSize(320, 240);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new FormTest());
    }
}
