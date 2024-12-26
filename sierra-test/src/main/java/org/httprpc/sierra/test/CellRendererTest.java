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
import org.httprpc.sierra.ImagePane;
import org.httprpc.sierra.UILoader;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.IOException;

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
            component = UILoader.load(this, "flag-cell-renderer.xml");
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
            model.addElement(new Flag("alpha.png", "Alpha", "I have a diver down; keep well clear at slow speed."));
            model.addElement(new Flag("bravo.png", "Bravo", "I am taking in or discharging or carrying dangerous goods."));
            model.addElement(new Flag("charlie.png", "Charlie", "Affirmative."));
            model.addElement(new Flag("delta.png", "Delta", "Keep clear of me; I am maneuvering with difficulty."));
            model.addElement(new Flag("echo.png", "Echo", "I am altering my course to starboard."));
            model.addElement(new Flag("foxtrot.png", "Foxtrot", "I am disabled; communicate with me."));
            model.addElement(new Flag("golf.png", "Golf", "I require a pilot."));
            model.addElement(new Flag("hotel.png", "Hotel", "I have a pilot on board."));
            model.addElement(new Flag("india.png", "India", "I am altering my course to port."));
            model.addElement(new Flag("juliet.png", "Juliet", "I am leaking dangerous cargo."));
            model.addElement(new Flag("kilo.png", "Kilo", "I wish to communicate with you."));
            model.addElement(new Flag("lima.png", "Lima", "You should stop your vessel instantly."));
            model.addElement(new Flag("mike.png", "Mike", "My vessel is stopped and making no way through the water."));
            model.addElement(new Flag("november.png", "November", "Negative."));
            model.addElement(new Flag("oscar.png", "Oscar", "Man overboard."));
            model.addElement(new Flag("papa.png", "Papa", "My nets have come fast upon an obstruction."));
            model.addElement(new Flag("quebec.png", "Quebec", "My vessel is \"healthy\" and I request free pratique."));
            model.addElement(new Flag("romeo.png", "Romeo", "No ICS meaning as single flag."));
            model.addElement(new Flag("sierra.png", "Sierra", "I am operating astern propulsion."));
            model.addElement(new Flag("tango.png", "Tango", "Keep clear of me."));
            model.addElement(new Flag("uniform.png", "Uniform", "You are running into danger."));
            model.addElement(new Flag("victor.png", "Victor", "I require assistance."));
            model.addElement(new Flag("whisky.png", "Whiskey", "I require medical assistance."));
            model.addElement(new Flag("xray.png", "Xray", "Stop carrying out your intentions and watch for my signals."));
            model.addElement(new Flag("yankee.png", "Yankee", "I am dragging my anchor."));
            model.addElement(new Flag("zulu.png", "Zulu", "I require a tug."));
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