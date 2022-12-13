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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import java.awt.ComponentOrientation;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.center;
import static org.httprpc.sierra.SwingUIBuilder.flowPanel;
import static org.httprpc.sierra.SwingUIBuilder.pageEnd;

public class FlowLayoutTest extends JFrame implements Runnable {
    private JPanel flowPanel;

    private JRadioButton leftToRightButton;
    private JRadioButton rightToLeftButton;

    private FlowLayoutTest() {
        super("Flow Layout Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        ButtonGroup buttonGroup = new ButtonGroup();

        setContentPane(borderPanel(
            center(flowPanel(
                cell(new JButton("Button 1")),
                cell(new JButton("Button 2")),
                cell(new JButton("Button 3")),
                cell(new JButton("Long-Named Button 4")),
                cell(new JButton("5"))
            )).with(flowPanel -> {
                flowPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                this.flowPanel = flowPanel;
            }),

            pageEnd(flowPanel(
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
                        flowPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                    } else if (rightToLeftButton.isSelected()) {
                        flowPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    flowPanel.validate();
                    flowPanel.repaint();
                }))
            )))
        );

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new FlowLayoutTest());
    }
}