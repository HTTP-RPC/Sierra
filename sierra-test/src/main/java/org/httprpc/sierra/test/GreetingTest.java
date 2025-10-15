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
import org.httprpc.sierra.ColumnPanel;
import org.httprpc.sierra.HorizontalAlignment;
import org.httprpc.sierra.ImagePane;
import org.httprpc.sierra.TextPane;
import org.httprpc.sierra.UILoader;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.io.IOException;

import static org.httprpc.kilo.util.Optionals.*;

public class GreetingTest extends JFrame implements Runnable {
    private GreetingTest() {
        super("Greeting Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var declarative = coalesce(map(System.getProperty("declarative"), Boolean::valueOf), () -> true);

        if (declarative) {
            setContentPane(UILoader.load(this, "GreetingTest.xml"));
        } else {
            var columnPanel = new ColumnPanel();

            columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

            columnPanel.setOpaque(true);
            columnPanel.setBackground(Color.WHITE);

            var imagePane = new ImagePane();

            try (var inputStream = getClass().getResourceAsStream("world.png")) {
                imagePane.setImage(ImageIO.read(inputStream));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            imagePane.setScaleMode(ImagePane.ScaleMode.FILL_WIDTH);

            columnPanel.add(imagePane);

            var textPane = new TextPane("Hello, World!");

            textPane.setHorizontalAlignment(HorizontalAlignment.CENTER);

            columnPanel.add(textPane);

            setContentPane(columnPanel);
        }

        setSize(320, 480);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new GreetingTest());
    }
}
