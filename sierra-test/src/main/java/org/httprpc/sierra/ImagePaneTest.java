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
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.Image;
import java.io.IOException;

public class ImagePaneTest extends JFrame implements Runnable {
    private ImagePaneTest() {
        super("Image Pane Test");

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

        var imagePane = new ImagePane(image);

        imagePane.setBackground(Color.WHITE);
        imagePane.setOpaque(true);
        imagePane.setScaleMode(ImagePane.ScaleMode.FILL_HEIGHT);
        imagePane.setBorder(new MatteBorder(16, 16, 16, 16, Color.LIGHT_GRAY));

        setContentPane(imagePane);

        setSize(320, 240);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ImagePaneTest());
    }
}
