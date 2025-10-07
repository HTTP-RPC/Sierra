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
import org.httprpc.sierra.SuggestionPicker;
import org.httprpc.sierra.UILoader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import static org.httprpc.kilo.util.Collections.*;

public class SuggestionPickerTest extends JFrame implements Runnable {
    private JSpinner countSpinner = null;
    private SuggestionPicker sizeSuggestionPicker = null;
    private SuggestionPicker colorSuggestionPicker = null;

    private JButton submitButton = null;

    private JLabel selectionLabel = null;

    private SuggestionPickerTest() {
        super("Suggestion Picker Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "suggestion-picker-test.xml"));

        countSpinner.setModel(new SpinnerNumberModel(0, 0, 10, 1));

        sizeSuggestionPicker.setSuggestions(listOf(
            "small",
            "medium",
            "large"
        ));

        sizeSuggestionPicker.addChangeListener(event -> System.out.println(sizeSuggestionPicker.getText()));

        colorSuggestionPicker.setSuggestions(listOf(
            "red",
            "orange",
            "yellow",
            "green",
            "blue",
            "purple",
            "brown",
            "black"
        ));

        colorSuggestionPicker.addChangeListener(event -> System.out.println(colorSuggestionPicker.getText()));

        submitButton.addActionListener(event -> showSelection());

        rootPane.setDefaultButton(submitButton);

        setSize(360, 240);
        setVisible(true);
    }

    private void showSelection() {
        var count = (int)countSpinner.getValue();

        var size = sizeSuggestionPicker.getText();
        var color = colorSuggestionPicker.getText();

        var message = String.format("You selected %d, \"%s\", and \"%s\".", count, size, color);

        selectionLabel.setText(message);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new SuggestionPickerTest());
    }
}
