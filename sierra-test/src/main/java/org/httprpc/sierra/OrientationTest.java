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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.ComponentOrientation;
import java.util.Locale;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.glue;
import static org.httprpc.sierra.UIBuilder.row;

public class OrientationTest extends JFrame implements Runnable {
    private RowPanel rowPanel;

    private JRadioButton leftToRightButton;
    private JRadioButton rightToLeftButton;

    private OrientationTest() {
        super("Orientation Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var buttonGroup = new ButtonGroup();

        setContentPane(column(4,
            row(4,
                cell(new JButton("1")),
                cell(new JButton("2")),
                cell(new JButton("3")),
                cell(new JButton("4")),
                cell(new JButton("5"))
            ).with(rowPanel -> this.rowPanel = rowPanel),

            cell(new JSeparator()),

            row(4,
                glue(),
                cell(new JRadioButton("Left to right", true)).with(button -> {
                    buttonGroup.add(button);

                    leftToRightButton = button;
                }),

                cell(new JRadioButton("Right to left")).with(button -> {
                    buttonGroup.add(button);

                    rightToLeftButton = button;
                }),

                cell(new JButton("Apply orientation")).with(button -> button.addActionListener(event -> {
                    if (leftToRightButton.isSelected()) {
                        rowPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                    } else if (rightToLeftButton.isSelected()) {
                        rowPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    rowPanel.revalidate();
                })),
                glue()
            )
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        // Deliberately set locale to one with RTL orientation.
        Locale.setDefault(new Locale("ar", "SA"));

        SwingUtilities.invokeLater(new OrientationTest());
    }
}