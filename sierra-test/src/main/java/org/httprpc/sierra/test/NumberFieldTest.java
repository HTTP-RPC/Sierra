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
import org.httprpc.sierra.NumberField;
import org.httprpc.sierra.UILoader;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import static org.httprpc.kilo.util.Optionals.*;

public class NumberFieldTest extends JFrame implements Runnable {
    private NumberField defaultNumberField = null;
    private NumberField customNumberField = null;

    private JLabel selectionLabel = null;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(NumberFieldTest.class.getName());

    private NumberFieldTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "number-field-test.xml", resourceBundle));

        defaultNumberField.addActionListener(event -> showSelection());

        customNumberField.setFormat(NumberFormat.getIntegerInstance());

        customNumberField.addActionListener(event -> showSelection());

        setSize(480, 320);
        setVisible(true);
    }

    private void showSelection() {
        var defaultValue = map(defaultNumberField.getValue(), Number::doubleValue);
        var customValue = map(customNumberField.getValue(), Number::doubleValue);

        selectionLabel.setText(String.format(resourceBundle.getString("selectionFormat"), defaultValue, customValue));
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new NumberFieldTest());
    }
}
