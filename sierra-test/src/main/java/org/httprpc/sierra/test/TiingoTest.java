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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class TiingoTest extends JFrame implements Runnable {
    private JTextField tickerTextField;
    private JTextField countTextField;

    private ActivityIndicator activityIndicator;

    private JButton submitButton;

    private JTextField nameTextField;
    private JTextField exchangeCodeTextField;
    private JTextArea descriptionTextArea;
    private JTextField startDateTextField;
    private JTextField endDateTextField;

    private JTable assetPricingTable;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(TiingoTest.class.getName());

    private static final TaskExecutor taskExecutor = new TaskExecutor(Executors.newCachedThreadPool(runnable -> {
        var thread = new Thread(runnable);

        thread.setDaemon(true);

        return thread;
    }));

    private TiingoTest() {
        super(resourceBundle.getString("title"));

        setContentPane(UILoader.load(this, "tiingo-test.xml", resourceBundle));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "tiingo-test.xml", resourceBundle));

        submitButton.addActionListener(event -> submit());

        setSize(720, 360);
        setVisible(true);
    }

    private void submit() {
        submitButton.setEnabled(false);

        activityIndicator.start();

        // TODO Clear fields/table

        taskExecutor.execute(() -> {
            // TODO Load asset details

            return null;
        }, (result, exception) -> {
            if (exception == null) {
                // TODO
            } else {
                // TODO
            }
        });

        taskExecutor.execute(() -> {
            // TODO Load asset pricing history

            return null;
        }, (result, exception) -> {
            if (exception == null) {
                // TODO
            } else {
                // TODO
            }
        });

        taskExecutor.notify(result -> {
            // TODO Stop activity indicator and enable submit button
        });
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new TiingoTest());
    }
}
