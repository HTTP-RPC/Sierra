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

package org.httprpc.sierra.test;

import com.formdev.flatlaf.FlatLightLaf;
import org.httprpc.sierra.UILoader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.util.ResourceBundle;

public class ActionTest extends JFrame implements Runnable {
    private JButton greetingButton = null;
    private JLabel greetingLabel = null;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(ActionTest.class.getName());

    private ActionTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "ActionTest.xml", resourceBundle));

        greetingButton.addActionListener(event -> sayHello());

        setSize(240, 180);
        setVisible(true);
    }

    private void sayHello() {
        greetingLabel.setText(resourceBundle.getString("greeting"));
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ActionTest());
    }
}
