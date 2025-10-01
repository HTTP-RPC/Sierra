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
import org.httprpc.sierra.ValidatedTextField;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import static org.httprpc.kilo.util.Optionals.*;

public class ValidatedInputTest extends JFrame implements Runnable {
    private NumberField numberField1 = null;
    private NumberField numberField2 = null;

    private ValidatedTextField validatedTextField = null;

    private JButton submitButton = null;

    private JLabel messageLabel = null;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(ValidatedInputTest.class.getName());

    private ValidatedInputTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "validated-input-test.xml", resourceBundle));

        numberField2.setFormat(NumberFormat.getIntegerInstance());

        submitButton.addActionListener(event -> showMessage());

        rootPane.setDefaultButton(submitButton);

        setSize(480, 320);
        setVisible(true);
    }

    private void showMessage() {
        var number1 = map(numberField1.getValue(), Number::doubleValue);
        var number2 = map(numberField2.getValue(), Number::doubleValue);

        var validatedText = validatedTextField.getValue();

        messageLabel.setText(String.format(resourceBundle.getString("messageFormat"), number1, number2, validatedText));
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ValidatedInputTest());
    }
}
