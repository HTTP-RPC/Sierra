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
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.glue;
import static org.httprpc.sierra.UIBuilder.row;

public class BaselineTest extends JFrame implements Runnable {
    private BaselineTest() {
        super("Baseline Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var labelFont = javax.swing.UIManager.getDefaults().getFont("Label.font");

        Image checkImage;
        try {
            checkImage = ImageIO.read(getClass().getResource("add.png"));
        } catch (IOException exception) {
            checkImage = null;
        }

        setContentPane(row(4, true,
            glue(),
            cell(new JLabel("abcdefg")).with(label -> {
                label.setFont(labelFont.deriveFont(Font.PLAIN, 16));
                label.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
            }),
            cell(new JLabel("hijk")).with(label -> {
                label.setFont(labelFont.deriveFont(Font.PLAIN, 32));
                label.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
            }),
            cell(new ImagePane(checkImage)).with(imagePane -> {
                imagePane.setPreferredSize(new Dimension(20, 20));
                imagePane.setScaleMode(ImagePane.ScaleMode.FILL_WIDTH);

                imagePane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
            }),
            cell(new JLabel("lmnop")).with(label -> {
                label.setFont(labelFont.deriveFont(Font.PLAIN, 24));
                label.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
            }),
            glue()
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new BaselineTest());
    }
}