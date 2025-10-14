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

import org.httprpc.sierra.ImagePane;
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.UILoader;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class FlagCellRenderer implements ListCellRenderer<Flag> {
    private JComponent component;

    private @Outlet ImagePane imagePane = null;
    private @Outlet JLabel nameLabel = null;
    private @Outlet JLabel descriptionLabel = null;

    public FlagCellRenderer() {
        component = UILoader.load(this, "FlagCellRenderer.xml");
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Flag> list, Flag value, int index, boolean selected, boolean cellHasFocus) {
        imagePane.setImage(value.getImage());
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

        component.setBackground(background);

        nameLabel.setForeground(foreground);
        descriptionLabel.setForeground(foreground);

        return component;
    }
}
