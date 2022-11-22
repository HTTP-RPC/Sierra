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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.cardPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.center;
import static org.httprpc.sierra.SwingUIBuilder.flowPanel;
import static org.httprpc.sierra.SwingUIBuilder.pageStart;

public class CardLayoutTest extends JFrame implements Runnable {
    private CardLayout cardLayout = new CardLayout();

    private JPanel cardPanel;

    private static final String BUTTON_PANEL = "Card with JButtons";
    private static final String TEXT_PANEL = "Card with JTextField";

    private CardLayoutTest() {
        super("Card Layout Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(borderPanel(new BorderLayout(),
            pageStart(flowPanel(new FlowLayout(),
                cell(new JComboBox<String>()).with(comboBox -> {
                    var model = new DefaultComboBoxModel<String>();

                    model.addElement(BUTTON_PANEL);
                    model.addElement(TEXT_PANEL);

                    comboBox.setModel(model);
                    comboBox.setEditable(false);
                    comboBox.addItemListener(event -> cardLayout.show(cardPanel, (String)event.getItem()));
                })
            )),

            center(cardPanel(cardLayout,
                cell(flowPanel(new FlowLayout(),
                    cell(new JButton("Button 1")),
                    cell(new JButton("Button 2")),
                    cell(new JButton("Button 3"))
                )).constrainedBy(BUTTON_PANEL),

                cell(flowPanel(new FlowLayout(),
                    cell(new JTextField("TextField", 20))
                )).constrainedBy(TEXT_PANEL)
            )).with(cardPanel -> this.cardPanel = cardPanel)
        ));

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new CardLayoutTest());
    }
}