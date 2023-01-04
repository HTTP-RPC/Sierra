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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.util.concurrent.Executors;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.glue;
import static org.httprpc.sierra.UIBuilder.row;

public class TaskExecutorTest extends JFrame implements Runnable {
    private JButton button;
    private JLabel label;

    private static TaskExecutor taskExecutor = new TaskExecutor(Executors.newCachedThreadPool(runnable -> {
        var thread = new Thread(runnable);

        thread.setDaemon(true);

        return thread;
    }));

    private TaskExecutorTest() {
        super("Task Executor Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(row(
            glue(),
            column(16,
                column(
                    cell(new JCheckBox("Checkbox 1")),
                    cell(new JCheckBox("Checkbox 2"))
                ),
                cell(new JButton("Execute Task")).with(button -> {
                    button.addActionListener(event -> executeTask());

                    this.button = button;
                }),
                cell(new JLabel(null, null, SwingConstants.CENTER)).with(label -> {
                    label.setForeground(Color.GRAY);

                    this.label = label;
                })
            ),
            glue()
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        setSize(240, 180);
        setVisible(true);
    }

    private void executeTask() {
        button.setEnabled(false);

        label.setText("Executing Task");

        taskExecutor.execute(() -> {
            Thread.sleep(5000);

            return 100;
        }, (result, exception) -> {
            button.setEnabled(true);

            if (exception == null) {
                label.setText(String.format("Task Complete (%d)", result));
            } else {
                label.setText("Task Failed");
            }
        });
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new TaskExecutorTest());
    }
}