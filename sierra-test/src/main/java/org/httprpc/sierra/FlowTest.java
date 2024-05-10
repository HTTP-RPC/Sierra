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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Image;
import java.io.IOException;

import static org.httprpc.sierra.UIBuilder.*;

public class FlowTest extends JFrame implements Runnable {
    private static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

    private FlowTest() {
        super("Flow Test");

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

        setContentPane(row(4,
            cell(new ImagePane(image)).with(imagePane -> imagePane.setScaleMode(ImagePane.ScaleMode.FILL_HEIGHT)),
            cell(new TextPane(TEXT)).with(textPane -> {
                textPane.setWrapText(true);
                textPane.setHorizontalAlignment(HorizontalAlignment.CENTER);
                textPane.setVerticalAlignment(VerticalAlignment.CENTER);
            }).weightBy(1.0),
            cell(new ImagePane(image)).with(imagePane -> imagePane.setScaleMode(ImagePane.ScaleMode.FILL_HEIGHT))
        ).with(contentPane -> {
            contentPane.setBackground(Color.WHITE);
            contentPane.setOpaque(true);
            contentPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        }).getComponent());

        setSize(720, 160);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new FlowTest());
    }
}
