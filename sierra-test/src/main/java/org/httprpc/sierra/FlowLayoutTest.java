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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.flowPanel;

public class FlowLayoutTest extends JFrame implements Runnable {
    private JPanel experimentFlowPanel;

    private JRadioButton leftToRightButton;
    private JRadioButton rightToLeftButton;

    private FlowLayoutTest() {
        super("Flow Layout Test");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        ButtonGroup buttonGroup = new ButtonGroup();

        setContentPane(borderPanel(new BorderLayout(),
            cell(flowPanel(new FlowLayout(),
                cell(new JButton("Button 1")),
                cell(new JButton("Button 2")),
                cell(new JButton("Button 3")),
                cell(new JButton("Long-Named Button 4")),
                cell(new JButton("5"))
            ), flowPanel -> {
                flowPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                experimentFlowPanel = flowPanel;
            }, BorderLayout.CENTER),

            cell(flowPanel(new FlowLayout(),
                cell(new JRadioButton("Left to right", true), button -> {
                    buttonGroup.add(button);

                    leftToRightButton = button;
                }),

                cell(new JRadioButton("Right to left"), button -> {
                    buttonGroup.add(button);

                    rightToLeftButton = button;
                }),

                cell(new JButton("Apply orientation"), button -> button.addActionListener(event -> {
                    if (leftToRightButton.isSelected()) {
                        experimentFlowPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                    } else if (rightToLeftButton.isSelected()) {
                        experimentFlowPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    experimentFlowPanel.validate();
                    experimentFlowPanel.repaint();
                }))
            ), BorderLayout.SOUTH))
        );

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new FlowLayoutTest());
    }
}