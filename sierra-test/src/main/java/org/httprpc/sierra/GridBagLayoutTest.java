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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.GridBagConstraints;

import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.gridBagPanel;
import static org.httprpc.sierra.SwingUIBuilder.row;

public class GridBagLayoutTest extends JFrame implements Runnable {
    private GridBagLayoutTest() {
        super("Grid Bag Layout Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(gridBagPanel(
            row(
                cell(new JButton("Button 1"))
                    .fill(GridBagConstraints.HORIZONTAL)
                    .weightXBy(0.5),
                cell(new JButton("Button 2"))
                    .fill(GridBagConstraints.HORIZONTAL)
                    .weightXBy(0.5),
                cell(new JButton("Button 3"))
                    .fill(GridBagConstraints.HORIZONTAL)
                    .weightXBy(0.5)
            ),
            row(
                cell(new JButton("Long-Named Button 4"))
                    .fill(GridBagConstraints.HORIZONTAL)
                    .padYBy(40)
                    .spanColumns(3)
            ),
            row(
                cell(new JPanel()),
                cell(new JButton("5"))
                    .fill(GridBagConstraints.HORIZONTAL)
                    .anchorTo(GridBagConstraints.PAGE_END)
                    .weightYBy(1.0)
                    .insetBy(10, 0, 0, 0)
                    .spanColumns(2)
            )
        ));

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new GridBagLayoutTest());
    }
}
