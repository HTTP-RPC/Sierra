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
import com.formdev.flatlaf.ui.FlatLineBorder;
import org.httprpc.sierra.RowPanel;
import org.httprpc.sierra.UILoader;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Insets;
import java.io.IOException;
import java.util.ResourceBundle;

public class ButtonGroupTest extends JFrame implements Runnable {
    private RowPanel buttonRow;

    private JToggleButton alignLeftButton;
    private JToggleButton alignCenterButton;
    private JToggleButton alignRightButton;
    private JToggleButton alignJustifyButton;

    private JLabel selectionLabel;

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(ButtonGroupTest.class.getName());

    private ButtonGroupTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        try {
            setContentPane(UILoader.load(this, "button-group-test.xml"));
        } catch (IOException exception) {
            exception.printStackTrace(System.out);
            return;
        }

        buttonRow.setBorder(new FlatLineBorder(new Insets(2, 2, 2, 2), Color.LIGHT_GRAY, 1, 8));

        var buttonGroup = new ButtonGroup();

        buttonGroup.add(alignLeftButton);
        buttonGroup.add(alignCenterButton);
        buttonGroup.add(alignRightButton);
        buttonGroup.add(alignJustifyButton);

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
