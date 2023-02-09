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

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.glue;
import static org.httprpc.sierra.UIBuilder.row;

public class CellRendererTest extends JFrame implements Runnable {
    private static class Flag {
        final Image image;
        final String name;
        final String description;

        Flag(String imageName, String name, String description) throws IOException {
            image = ImageIO.read(CellRendererTest.class.getResource(String.format("flags/%s", imageName)));

            this.name = name;
            this.description = description;
        }
    }

    private static class FlagCellRenderer implements ListCellRenderer<Flag> {
        JComponent component;

        ImagePane imagePane;
        JLabel nameLabel;
        JLabel descriptionLabel;

        FlagCellRenderer() {
            var labelFont = javax.swing.UIManager.getDefaults().getFont("Label.font");

            component = row(4,
                cell(new ImagePane()).with(imagePane -> {
                    imagePane.setScaleMode(ImagePane.ScaleMode.FILL_WIDTH);
                    imagePane.setPreferredSize(new Dimension(30, 30));

                    this.imagePane = imagePane;
                }),
                column(
                    cell(new JLabel()).with(label -> {
                        label.setFont(labelFont.deriveFont(Font.BOLD, labelFont.getSize() + 2));

                        nameLabel = label;
                    }),
                    glue(),
                    cell(new JLabel()).with(label -> descriptionLabel = label)
                )
            ).with(component -> {
                component.setOpaque(true);
                component.setBorder(new EmptyBorder(4, 4, 4, 4));
            }).getComponent();
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Flag> list, Flag value, int index, boolean selected, boolean cellHasFocus) {
            imagePane.setImage(value.image);
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

            component.setBackground(background);

            nameLabel.setForeground(foreground);
            descriptionLabel.setForeground(foreground);

            return component;
        }
    }

    private CellRendererTest() {
        super("Cell Renderer Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var list = new JList<Flag>();

        var model = new DefaultListModel<Flag>();

        try {
            model.addElement(new Flag("alpha.png", "Alpha", "I have a diver down; keep well clear at slow speed." ));
            model.addElement(new Flag("bravo.png", "Bravo", "I am taking in or discharging or carrying dangerous goods." ));
            model.addElement(new Flag("charlie.png", "Charlie", "Affirmative." ));
            model.addElement(new Flag("delta.png", "Delta", "Keep clear of me; I am maneuvering with difficulty." ));
            model.addElement(new Flag("echo.png", "Echo", "I am altering my course to starboard." ));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        list.setModel(model);
        list.setCellRenderer(new FlagCellRenderer());

        var scrollPane = new JScrollPane(list);

        scrollPane.setBorder(null);

        setContentPane(scrollPane);

        setSize(420, 640);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new CellRendererTest());
    }
}