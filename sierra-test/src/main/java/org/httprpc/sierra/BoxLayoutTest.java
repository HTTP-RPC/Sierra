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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.awt.Component;

import static org.httprpc.sierra.SwingUIBuilder.boxPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;

public class BoxLayoutTest extends JFrame implements Runnable {
    private BoxLayoutTest() {
        super("Box Layout Test");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(boxPanel(BoxLayout.Y_AXIS,
            cell(new JButton("Button 1")).with(this::configureButton),
            cell(new JButton("Button 2")).with(this::configureButton),
            cell(new JButton("Button 3")).with(this::configureButton),
            cell(new JButton("Long-Named Button 4")).with(this::configureButton),
            cell(new JButton("5")).with(this::configureButton)
        ));

        pack();
        setVisible(true);
    }

    private void configureButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new BoxLayoutTest());
    }
}
