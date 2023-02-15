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

import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.glue;
import static org.httprpc.sierra.UIBuilder.row;

public class ActivityIndicatorTest extends JFrame implements Runnable {
    ActivityIndicator activityIndicator1;
    ActivityIndicator activityIndicator2;
    ActivityIndicator activityIndicator3;

    private ActivityIndicatorTest() {
        super("Activity Indicator Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(column(
            glue(),
            row(8,
                glue(),
                cell(new ActivityIndicator()).with(activityIndicator -> activityIndicator1 = activityIndicator),
                cell(new ActivityIndicator()).with(activityIndicator -> {
                    activityIndicator.setPreferredSize(new Dimension(96, 96));

                    activityIndicator2 = activityIndicator;
                }),
                cell(new ActivityIndicator()).with(activityIndicator -> activityIndicator3 = activityIndicator),
                glue()
            ),
            glue(),
            row(
                glue(),
                cell(new JToggleButton("Active")).with(button -> button.addActionListener(event -> toggleActivityIndicators(button.isSelected()))),
                glue()
            )
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        setSize(360, 240);
        setVisible(true);
    }

    private void toggleActivityIndicators(boolean active) {
        if (active) {
            activityIndicator1.start();
            activityIndicator2.start();
            activityIndicator3.start();
        } else {
            activityIndicator1.stop();
            activityIndicator2.stop();
            activityIndicator3.stop();
        }
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ActivityIndicatorTest());
    }
}