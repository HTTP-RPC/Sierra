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

import org.httprpc.sierra.ActivityIndicator;
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.TaskExecutor;
import org.httprpc.sierra.UILoader;

import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.LayoutFocusTraversalPolicy;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;

public class TabPane extends JRootPane {
    private TaskExecutor taskExecutor;

    private @Outlet JButton submitButton = null;
    private @Outlet ActivityIndicator activityIndicator = null;

    public TabPane(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;

        setContentPane(UILoader.load(this, "TabPane.xml"));

        submitButton.addActionListener(event -> submit());

        setDefaultButton(submitButton);

        getGlassPane().addMouseListener(new MouseAdapter() {});

        setFocusTraversalPolicyProvider(true);

        setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            @Override
            public Component getFirstComponent(Container container) {
                return getGlassPane().isVisible() ? null : super.getFirstComponent(container);
            }

            @Override
            public Component getLastComponent(Container container) {
                return getGlassPane().isVisible() ? null : super.getLastComponent(container);
            }
        });
    }

    private void submit() {
        requestFocus();

        var glassPane = getGlassPane();

        glassPane.setVisible(true);

        submitButton.setEnabled(false);
        activityIndicator.start();

        taskExecutor.execute(() -> {
            Thread.sleep(7500);

            return null;
        }, (result, exception) -> {
            glassPane.setVisible(false);

            submitButton.setEnabled(true);
            activityIndicator.stop();
        });
    }
}
