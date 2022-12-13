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
import java.awt.Component;
import java.util.function.Consumer;

import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.verticalBoxPanel;

public class BoxLayoutTest extends JFrame implements Runnable {
    private BoxLayoutTest() {
        super("Box Layout Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        Consumer<JButton> buttonConsumer = button -> button.setAlignmentX(Component.CENTER_ALIGNMENT);

        setContentPane(verticalBoxPanel(
            cell(new JButton("Button 1")).with(buttonConsumer),
            cell(new JButton("Button 2")).with(buttonConsumer),
            cell(new JButton("Button 3")).with(buttonConsumer),
            cell(new JButton("Long-Named Button 4")).with(buttonConsumer),
            cell(new JButton("5")).with(buttonConsumer)
        ));

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new BoxLayoutTest());
    }
}
