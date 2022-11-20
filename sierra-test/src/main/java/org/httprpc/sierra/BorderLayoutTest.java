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
import java.awt.BorderLayout;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;

public class BorderLayoutTest extends JFrame implements Runnable {
    private BorderLayoutTest() {
        super("Border Layout Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(borderPanel(new BorderLayout(),
            cell(new JButton("Button 1 (PAGE_START)")).constrainedBy(BorderLayout.PAGE_START),
            cell(new JButton("Button 3 (LINE_START)")).constrainedBy(BorderLayout.LINE_START),
            cell(new JButton("Button 2 (CENTER)")).constrainedBy(BorderLayout.CENTER),
            cell(new JButton("5 (LINE_END)")).constrainedBy(BorderLayout.LINE_END),
            cell(new JButton("Long-Named Button 4 (PAGE_END)")).constrainedBy(BorderLayout.PAGE_END)
        ));

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new BorderLayoutTest());
    }
}