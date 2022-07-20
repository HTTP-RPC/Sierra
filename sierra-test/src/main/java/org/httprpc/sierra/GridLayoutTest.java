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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.gridPanel;

public class GridLayoutTest extends JFrame implements Runnable {
    GridLayout gridLayout = new GridLayout(0, 2);

    JPanel gridPanel;

    JComboBox<Integer> horizontalGapComboBox;
    JComboBox<Integer> verticalGapComboBox;

    private static final Integer[] gapList = {0, 10, 15, 20};

    private GridLayoutTest() {
        super("Grid Layout Test");

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(borderPanel(new BorderLayout(),
            cell(gridPanel(gridLayout,
                cell(new JButton("Button 1")),
                cell(new JButton("Button 2")),
                cell(new JButton("Button 3")),
                cell(new JButton("Long-Named Button 4")),
                cell(new JButton("5"))
            )).constrainedBy(BorderLayout.PAGE_START).with(gridPanel -> this.gridPanel = gridPanel),

            cell(new JSeparator()).constrainedBy(BorderLayout.CENTER),

            cell(gridPanel(new GridLayout(2, 3),
                cell(new JLabel("Horizontal gap:")),
                cell(new JLabel("Vertical gap:")),
                cell(new JLabel(" ")),
                cell(new JComboBox<>(gapList)).with(comboBox -> horizontalGapComboBox = comboBox),
                cell(new JComboBox<>(gapList)).with(comboBox -> verticalGapComboBox = comboBox),
                cell(new JButton("Apply gaps")).with(button -> button.addActionListener(event -> {
                    gridLayout.setHgap((Integer)horizontalGapComboBox.getSelectedItem());
                    gridLayout.setVgap((Integer)verticalGapComboBox.getSelectedItem());

                    gridLayout.layoutContainer(gridPanel);
                }))
            )).constrainedBy(BorderLayout.PAGE_END)
        ));

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new GridLayoutTest());
    }
}
