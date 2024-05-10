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
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Insets;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import static org.httprpc.sierra.UIBuilder.*;

public class ButtonGroupTest extends JFrame implements Runnable {
    private JToggleButton alignLeftButton;
    private JToggleButton alignCenterButton;
    private JToggleButton alignRightButton;
    private JToggleButton alignJustifyButton;

    private JLabel selectionLabel;

    private ButtonGroupTest() {
        super("Button Group Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        FlatSVGIcon alignLeftIcon;
        FlatSVGIcon alignCenterIcon;
        FlatSVGIcon alignRightIcon;
        FlatSVGIcon alignJustifyIcon;
        try {
            alignLeftIcon = new FlatSVGIcon(ButtonGroupTest.class.getResource("format_align_left_black_18dp.svg").toURI());
            alignCenterIcon = new FlatSVGIcon(ButtonGroupTest.class.getResource("format_align_center_black_18dp.svg").toURI());
            alignRightIcon = new FlatSVGIcon(ButtonGroupTest.class.getResource("format_align_right_black_18dp.svg").toURI());
            alignJustifyIcon = new FlatSVGIcon(ButtonGroupTest.class.getResource("format_align_justify_black_18dp.svg").toURI());
        } catch (URISyntaxException exception) {
            throw new RuntimeException(exception);
        }

        Consumer<JToggleButton> buttonStyle = button -> button.putClientProperty("JButton.buttonType", "toolBarButton");

        var buttonGroup = new ButtonGroup();

        setContentPane(column(8,
            row(8,
                row(
                    cell(new JToggleButton(alignLeftIcon)).with(buttonStyle, buttonGroup::add, button -> {
                        button.addActionListener(event -> updateSelection());

                        alignLeftButton = button;
                    }),

                    cell(new JSeparator(SwingConstants.VERTICAL)),

                    cell(new JToggleButton(alignCenterIcon)).with(buttonStyle, buttonGroup::add, button -> {
                        button.addActionListener(event -> updateSelection());

                        alignCenterButton = button;
                    }),

                    cell(new JSeparator(SwingConstants.VERTICAL)),

                    cell(new JToggleButton(alignRightIcon)).with(buttonStyle, buttonGroup::add, button -> {
                        button.addActionListener(event -> updateSelection());

                        alignRightButton = button;
                    }),

                    cell(new JSeparator(SwingConstants.VERTICAL)),

                    cell(new JToggleButton(alignJustifyIcon)).with(buttonStyle, buttonGroup::add, button -> {
                        button.addActionListener(event -> updateSelection());

                        alignJustifyButton = button;
                    })
                ).with(row -> row.setBorder(new FlatLineBorder(new Insets(2, 2, 2, 2), Color.LIGHT_GRAY, 1, 8))),

                cell(new JLabel()).with(label -> selectionLabel = label)
            )
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        alignLeftButton.setSelected(true);

        updateSelection();

        setSize(320, 120);
        setVisible(true);
    }

    private void updateSelection() {
        String text;
        if (alignLeftButton.isSelected()) {
            text = "Align left";
        } else if (alignCenterButton.isSelected()) {
            text = "Align center";
        } else if (alignRightButton.isSelected()) {
            text = "Align right";
        } else if (alignJustifyButton.isSelected()) {
            text = "Align justify";
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
