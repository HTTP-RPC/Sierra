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
import org.httprpc.sierra.ActivityIndicator;
import org.httprpc.sierra.TaskExecutor;
import org.httprpc.sierra.UILoader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class TaskExecutorTest extends JFrame implements Runnable {
    private JButton button;
    private JLabel label;
    private ActivityIndicator activityIndicator;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(TaskExecutorTest.class.getName());

    private static final TaskExecutor taskExecutor = new TaskExecutor(Executors.newCachedThreadPool(runnable -> {
        var thread = new Thread(runnable);

        thread.setDaemon(true);

        return thread;
    }));

    private TaskExecutorTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "task-executor-test.xml", resourceBundle));

        button.addActionListener(event -> executeTask());

        setSize(320, 180);
        setVisible(true);
    }

    private void executeTask() {
        button.setEnabled(false);

        label.setText(resourceBundle.getString("executingTask"));

        activityIndicator.start();

        taskExecutor.execute(() -> {
            var value = Math.random();

            Thread.sleep((long)(5000 * value));

            if (value < 0.5) {
                throw new Exception();
            }

            return 100.0;
        }, (result, exception) -> {
            button.setEnabled(true);

            if (exception == null) {
                label.setText(String.format(resourceBundle.getString("taskCompleteFormat"), result));
            } else {
                label.setText(resourceBundle.getString("taskFailed"));
            }

            activityIndicator.stop();
        });
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new TaskExecutorTest());
    }
}