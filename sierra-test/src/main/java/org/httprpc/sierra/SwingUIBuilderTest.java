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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.boxPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.flowPanel;

public class SwingUIBuilderTest {
    private static JLabel label;

    public static void main(String[] args) {
        flowPanel(new FlowLayout(FlowLayout.LEADING, 8, 8),
            cell(new JLabel("abc"), label -> {
                label.setText("def");

                SwingUIBuilderTest.label = label;
            }),

            cell(new JButton("xyz"), button -> button.addActionListener(event -> {
                // TODO
            }))
        );

        borderPanel(new BorderLayout(),
            cell(flowPanel(new FlowLayout(),
                cell(new JLabel("123")),
                cell(new JLabel("456")),
                cell(new JLabel("789"))
            ), BorderLayout.NORTH)
        );

        boxPanel(BoxLayout.X_AXIS,
            cell(new JButton(), button -> button.setAlignmentX(1.0f))
        );
    }
}
