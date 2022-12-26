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
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Image;
import java.io.IOException;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;

public class GreetingTest extends JFrame implements Runnable {
    private GreetingTest() {
        super("Greeting Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        Image image;
        try {
            image = ImageIO.read(getClass().getResource("world.png"));
        } catch (IOException exception) {
            image = null;
        }

        var scrollPane = new JScrollPane(column(
            cell(new ImagePane(image, true)),
            cell(new TextPane("Hello, World!", false)).with(textPane -> textPane.setHorizontalAlignment(HorizontalAlignment.CENTER))
        ).with(columnPanel -> {
            columnPanel.setSpacing(4);
            columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
            columnPanel.setScrollableTracksViewportWidth(true);
        }).getComponent());

        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(null);

        setContentPane(scrollPane);

        setSize(320, 640);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new GreetingTest());
    }
}
