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

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.io.IOException;
import java.util.Properties;

public class CustomBindingTest extends JFrame implements Runnable {
    public static class CustomLabel extends JLabel {
        public CustomLabel(String text) {
            this(text, null);
        }

        public CustomLabel(String text, Icon icon) {
            super(text, icon, SwingConstants.CENTER);

            setForeground(Color.RED);
            setBackground(Color.GREEN);

            setOpaque(true);
        }
    }

    private CustomBindingTest() {
        super("Custom Binding Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "CustomBindingTest.xml"));

        setSize(320, 120);
        setVisible(true);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        var bindings = new Properties();

        try (var inputStream = CustomBindingTest.class.getResourceAsStream("/bindings.properties")) {
            bindings.load(inputStream);
        }

        UILoader.bind(bindings);

        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new CustomBindingTest());
    }
}
