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
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.UILoader;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.util.ResourceBundle;

public class ButtonGroupTest extends JFrame implements Runnable {
    private @Outlet JToggleButton alignLeftButton = null;
    private @Outlet JToggleButton alignCenterButton = null;
    private @Outlet JToggleButton alignRightButton = null;
    private @Outlet JToggleButton alignJustifyButton = null;

    private @Outlet JLabel selectionLabel = null;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(ButtonGroupTest.class.getName());

    private ButtonGroupTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "ButtonGroupTest.xml"));

        alignLeftButton.addActionListener(event -> updateSelection());
        alignCenterButton.addActionListener(event -> updateSelection());
        alignRightButton.addActionListener(event -> updateSelection());
        alignJustifyButton.addActionListener(event -> updateSelection());

        alignLeftButton.setSelected(true);

        updateSelection();

        setSize(320, 120);
        setVisible(true);
    }

    private void updateSelection() {
        String text;
        if (alignLeftButton.isSelected()) {
            text = resourceBundle.getString("alignLeft");
        } else if (alignCenterButton.isSelected()) {
            text = resourceBundle.getString("alignCenter");
        } else if (alignRightButton.isSelected()) {
            text = resourceBundle.getString("alignRight");
        } else if (alignJustifyButton.isSelected()) {
            text = resourceBundle.getString("alignJustify");
        } else {
            throw new UnsupportedOperationException();
        }

        selectionLabel.setText(text);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ButtonGroupTest());
    }
}
