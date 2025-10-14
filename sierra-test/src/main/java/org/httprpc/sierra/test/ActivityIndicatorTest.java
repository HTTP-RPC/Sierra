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
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.UILoader;

import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.util.ResourceBundle;

public class ActivityIndicatorTest extends JFrame implements Runnable {
    private @Outlet ActivityIndicator activityIndicator1 = null;
    private @Outlet ActivityIndicator activityIndicator2 = null;
    private @Outlet ActivityIndicator activityIndicator3 = null;

    private @Outlet JToggleButton toggleButton = null;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(ActivityIndicatorTest.class.getName());

    private ActivityIndicatorTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "ActivityIndicatorTest.xml", resourceBundle));

        toggleButton.addActionListener(event -> toggleActivityIndicators(toggleButton.isSelected()));

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
