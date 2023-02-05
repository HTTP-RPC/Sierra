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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.glue;
import static org.httprpc.sierra.UIBuilder.row;

public class DateTimePickerTest extends JFrame implements Runnable {
    private DateTimePickerTest() {
        super("Date Picker Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        setContentPane(column(
            glue(),
            row(4,
                glue(),
                cell(new JTextField(6)).with(textField -> {
                    textField.setText(dateFormatter.format(LocalDate.now()));

                    textField.setInputVerifier(new InputVerifier() {
                        @Override
                        public boolean verify(JComponent input) {
                            try {
                                LocalDate.parse(textField.getText(), dateFormatter);
                                return true;
                            } catch (DateTimeParseException exception) {
                                return false;
                            }
                        }
                    });
                }),
                cell(new JTextField(6)).with(textField -> {
                    textField.setText(timeFormatter.format(LocalTime.now()));

                    textField.setInputVerifier(new InputVerifier() {
                        @Override
                        public boolean verify(JComponent input) {
                            try {
                                LocalTime.parse(textField.getText(), timeFormatter);
                                return true;
                            } catch (DateTimeParseException exception) {
                                return false;
                            }
                        }
                    });
                }),
                glue()
            )
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        setSize(320, 240);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new DateTimePickerTest());
    }
}