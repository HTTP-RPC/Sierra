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
import org.httprpc.sierra.ScrollingKeyboardFocusManager;
import org.httprpc.sierra.UILoader;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.KeyboardFocusManager;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class FormTest extends JFrame implements Runnable {
    private JFormattedTextField formattedTextField1 = null;
    private JFormattedTextField formattedTextField2 = null;
    private JFormattedTextField formattedTextField3 = null;
    private JFormattedTextField formattedTextField4 = null;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(FormTest.class.getName());

    private FormTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var scrollPane = new JScrollPane(UILoader.load(this, "FormTest.xml", resourceBundle));

        scrollPane.setBorder(null);

        setContentPane(scrollPane);

        var numberFormat = NumberFormat.getIntegerInstance();

        formattedTextField1.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numberFormat)));
        formattedTextField2.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numberFormat)));
        formattedTextField3.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numberFormat)));
        formattedTextField4.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numberFormat)));

        setSize(480, 360);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        KeyboardFocusManager.setCurrentKeyboardFocusManager(new ScrollingKeyboardFocusManager());

        SwingUtilities.invokeLater(new FormTest());
    }
}
