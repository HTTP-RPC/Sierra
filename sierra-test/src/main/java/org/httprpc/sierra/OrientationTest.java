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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.ComponentOrientation;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
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

        setContentPane(column(
            row(
                cell(new JButton("1")),
                cell(new JButton("2")),
                cell(new JButton("3")),
                cell(new JButton("4")),
                cell(new JButton("5"))
            ).with(rowPanel -> {
                rowPanel.setSpacing(4);

                this.rowPanel = rowPanel;
            }),

            row(
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
                }))
            ).with(rowPanel -> rowPanel.setSpacing(4))
        ).with(columnPanel -> {
            columnPanel.setSpacing(4);
            columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        }).getComponent());

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new OrientationTest());
    }
}