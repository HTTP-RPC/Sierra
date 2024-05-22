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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.util.Arrays;

import static org.httprpc.sierra.UIBuilder.*;

public class SuggestionPickerTest extends JFrame implements Runnable {
    private SuggestionPickerTest() {
        super("Suggestion Picker Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(column(4, true,
            row(4,
                cell(new JLabel("Quantity")),
                cell(new JTextField(18))
            ),
            row(4,
                cell(new JLabel("Size")),
                cell(new SuggestionPicker(18)).with(suggestionPicker -> {
                    suggestionPicker.setSuggestions(Arrays.asList(
                        "small",
                        "medium",
                        "large"
                    ));
                })
            ),
            row(4,
                cell(new JLabel("Color")),
                cell(new SuggestionPicker(18)).with(suggestionPicker -> {
                    suggestionPicker.setSuggestions(Arrays.asList(
                        "red",
                        "orange",
                        "yellow",
                        "green",
                        "blue",
                        "purple",
                        "brown",
                        "black"
                    ));

                    suggestionPicker.setMaximumRowCount(4);
                })
            ),
            glue()
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        setSize(320, 240);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new SuggestionPickerTest());
    }
}
