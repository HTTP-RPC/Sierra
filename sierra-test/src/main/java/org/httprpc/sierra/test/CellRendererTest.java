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

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.httprpc.sierra.BasicListModel;
import org.httprpc.sierra.ColumnPanel;
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.RowPanel;
import org.httprpc.sierra.Spacer;
import org.httprpc.sierra.UILoader;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;

public class CellRendererTest extends JFrame implements Runnable {
    private static class Flag {
        Icon icon;
        String name;
        String description;

        Flag(String iconName, String name, String description) {
            var icon = new FlatSVGIcon(getClass().getResource(String.format("flags/%s", iconName)));

            this.icon = icon.derive(ICON_SIZE, ICON_SIZE);

            this.name = name;
            this.description = description;
        }
    }

    private static class FlagCellRenderer extends RowPanel implements ListCellRenderer<Flag> {
        JLabel iconLabel;
        JLabel nameLabel;
        JLabel descriptionLabel;

        FlagCellRenderer() {
            setOpaque(true);

            setSpacing(4);

            add(new ColumnPanel(), columnPanel -> {
                columnPanel.add(new JLabel(), label -> {
                    label.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));

                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setVerticalAlignment(SwingConstants.CENTER);

                    label.setBorder(UILoader.createRoundedLineBorder(UIManager.getColor("List.selectionInactiveBackground"),
                        new BasicStroke(1,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND), 4));

                    iconLabel = label;
                });

                columnPanel.add(new Spacer(), 1.0);
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
            iconLabel.setIcon(value.icon);
            nameLabel.setText(value.name);
            descriptionLabel.setText(value.description);

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

    private @Outlet JList<Flag> flagList = null;

    private List<Flag> flags = listOf(
        new Flag("alpha.svg", "Alpha", "I have a diver down; keep well clear at slow speed."),
        new Flag("bravo.svg", "Bravo", "I am taking in or discharging or carrying dangerous goods."),
        new Flag("charlie.svg", "Charlie", "Affirmative."),
        new Flag("delta.svg", "Delta", "Keep clear of me; I am maneuvering with difficulty."),
        new Flag("echo.svg", "Echo", "I am altering my course to starboard."),
        new Flag("foxtrot.svg", "Foxtrot", "I am disabled; communicate with me."),
        new Flag("golf.svg", "Golf", "I require a pilot."),
        new Flag("hotel.svg", "Hotel", "I have a pilot on board."),
        new Flag("india.svg", "India", "I am altering my course to port."),
        new Flag("juliet.svg", "Juliet", "I am leaking dangerous cargo."),
        new Flag("kilo.svg", "Kilo", "I wish to communicate with you."),
        new Flag("lima.svg", "Lima", "You should stop your vessel instantly."),
        new Flag("mike.svg", "Mike", "My vessel is stopped and making no way through the water."),
        new Flag("november.svg", "November", "Negative."),
        new Flag("oscar.svg", "Oscar", "Man overboard."),
        new Flag("papa.svg", "Papa", "My nets have come fast upon an obstruction."),
        new Flag("quebec.svg", "Quebec", "My vessel is \"healthy\" and I request free pratique."),
        new Flag("romeo.svg", "Romeo", "No ICS meaning as single flag."),
        new Flag("sierra.svg", "Sierra", "I am operating astern propulsion."),
        new Flag("tango.svg", "Tango", "Keep clear of me."),
        new Flag("uniform.svg", "Uniform", "You are running into danger."),
        new Flag("victor.svg", "Victor", "I require assistance."),
        new Flag("whisky.svg", "Whiskey", "I require medical assistance."),
        new Flag("xray.svg", "Xray", "Stop carrying out your intentions and watch for my signals."),
        new Flag("yankee.svg", "Yankee", "I am dragging my anchor."),
        new Flag("zulu.svg", "Zulu", "I require a tug.")
    );

    private static final int ICON_SIZE = 48;

    private CellRendererTest() {
        super("Cell Renderer Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "CellRendererTest.xml"));

        flagList.setModel(new BasicListModel<>(flags));

        flagList.setCellRenderer(new FlagCellRenderer());

        setSize(480, 640);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new CellRendererTest());
    }
}
