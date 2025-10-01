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
import org.httprpc.sierra.DatePicker;
import org.httprpc.sierra.TimePicker;
import org.httprpc.sierra.UILoader;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.ComponentOrientation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class DateTimePickerTest extends JFrame implements Runnable {
    private DatePicker datePicker = null;

    private TimePicker timePicker = null;
    private JComboBox<Integer> minuteIntervalComboBox = null;
    private JCheckBox strictCheckBox = null;

    private JButton submitButton = null;

    private JLabel selectionLabel = null;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(DateTimePickerTest.class.getName());

    private DateTimePickerTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "date-time-picker-test.xml", resourceBundle));

        var now = LocalDate.now();

        datePicker.setMinimumDate(now.minusMonths(3));
        datePicker.setMaximumDate(now.plusMonths(3));

        var minimumTime = LocalTime.of(6, 0);
        var maximumTime = LocalTime.of(18, 0);

        timePicker.setMinimumTime(minimumTime);
        timePicker.setMaximumTime(maximumTime);

        minuteIntervalComboBox.setModel(new DefaultComboBoxModel<>(new Integer[] {1, 2, 3, 4, 5, 6, 10, 15, 20, 30}));
        minuteIntervalComboBox.addActionListener(event -> timePicker.setMinuteInterval((Integer)minuteIntervalComboBox.getSelectedItem()));

        strictCheckBox.addActionListener(event -> timePicker.setStrict(strictCheckBox.isSelected()));

        submitButton.addActionListener(event -> showSelection());

        rootPane.setDefaultButton(submitButton);

        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        setSize(480, 320);
        setVisible(true);
    }

    private void showSelection() {
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        var message = String.format(resourceBundle.getString("selectionFormat"),
            dateFormatter.format(datePicker.getDate()),
            timeFormatter.format(timePicker.getTime()));

        selectionLabel.setText(message);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new DateTimePickerTest());
    }
}
