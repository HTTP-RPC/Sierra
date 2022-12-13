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
import javax.swing.SwingUtilities;

import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.flowPanel;

public class FlowPanelTest2 extends JFrame implements Runnable {
    private FlowPanelTest2() {
        super("Flow Panel Test 2");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(flowPanel(
            cell(new JButton("Press Me"))
                .with(button -> button.addActionListener(event -> System.out.println("Button pressed")))
        ));

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new FlowPanelTest2());
    }
}
