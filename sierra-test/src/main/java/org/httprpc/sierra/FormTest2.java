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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.center;
import static org.httprpc.sierra.SwingUIBuilder.gridBagPanel;

public class FormTest2 extends JFrame implements Runnable {
    private FormTest2() {
        super("Form Test 2");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var contentPane = borderPanel(
            center(
                new JScrollPane(getViewportView())
            )
        );

        contentPane.setBorder(new EmptyBorder(8, 8, 8, 8));

        setContentPane(contentPane);

        setSize(640, 480);
        setVisible(true);
    }

    private JComponent getViewportView() {
        var viewportView = gridBagPanel(4, 4
            // TODO Contact form
        );

        viewportView.setBorder(new EmptyBorder(8, 8, 8, 8));

        return viewportView;
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new FormTest2());
    }
}
