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
import org.httprpc.sierra.RowPanel;
import org.httprpc.sierra.UILoader;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import java.awt.ComponentOrientation;
import java.util.ResourceBundle;

public class OrientationTest extends JFrame implements Runnable {
    private RowPanel rowPanel;

    private JRadioButton leftToRightButton;
    private JRadioButton rightToLeftButton;

    private JButton applyOrientationButton;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(OrientationTest.class.getName());

    private OrientationTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "orientation-test.xml", resourceBundle));

        var buttonGroup = new ButtonGroup();

        buttonGroup.add(leftToRightButton);
        buttonGroup.add(rightToLeftButton);

        leftToRightButton.setSelected(true);

        applyOrientationButton.addActionListener(event -> applyOrientation());

        pack();
        setVisible(true);
    }

    private void applyOrientation() {
        if (leftToRightButton.isSelected()) {
            rowPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        } else if (rightToLeftButton.isSelected()) {
            rowPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        } else {
            throw new UnsupportedOperationException();
        }

        rowPanel.revalidate();
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new OrientationTest());
    }
}