/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.httprpc.sierra.test;

import org.httprpc.sierra.ColumnPanel;
import org.httprpc.sierra.RowPanel;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

public class FlagCellRenderer extends RowPanel implements ListCellRenderer<Flag> {
    private JLabel iconLabel;
    private JLabel nameLabel;
    private JLabel descriptionLabel;

    public FlagCellRenderer() {
        setOpaque(true);

        setSpacing(4);

        add(new JLabel(), label -> {
            label.setPreferredSize(new Dimension(30, 30));
            label.setVerticalAlignment(SwingConstants.CENTER);

            iconLabel = label;
        });

        add(new ColumnPanel(), columnPanel -> {
            columnPanel.add(new JLabel(), label -> {
                label.putClientProperty("FlatLaf.styleClass", "h4");

                nameLabel = label;
            });

            columnPanel.add(new JLabel(), label -> descriptionLabel = label);
        }, 1.0);

        setBorder(new EmptyBorder(4, 4, 4, 4));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Flag> list,
        Flag value, int index,
        boolean selected, boolean cellHasFocus) {
        iconLabel.setIcon(value.getIcon());
        nameLabel.setText(value.getName());
        descriptionLabel.setText(value.getDescription());

        Color background;
        Color foreground;
        if (selected) {
            background = list.getSelectionBackground();
            foreground = list.getSelectionForeground();
        } else {
            background = list.getBackground();
            foreground = list.getForeground();
        }

        setBackground(background);

        nameLabel.setForeground(foreground);
        descriptionLabel.setForeground(foreground);

        return this;
    }
}
