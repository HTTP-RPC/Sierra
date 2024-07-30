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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;
import java.util.function.Consumer;

import static org.httprpc.sierra.UIBuilder.*;

public class BorderTest extends JFrame implements Runnable {
    private BorderTest() {
        super("Border Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var labelFont = javax.swing.UIManager.getDefaults().getFont("Label.font");

        Consumer<JLabel> cellStyle = label -> {
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(4, 4, 4, 4)
            ));
        };

        setContentPane(column(4,
            cell(new JLabel("Page Start")).with(cellStyle),
            row(4,
                cell(new JLabel("Line Start")).with(cellStyle.andThen(label -> label.setFont(labelFont.deriveFont(Font.PLAIN, 24)))),
                cell(new JLabel("Center")).weightBy(1.0).with(cellStyle.andThen(label -> label.setFont(labelFont.deriveFont(Font.BOLD, 48)))),
                cell(new JLabel("Line End")).with(cellStyle.andThen(label -> label.setFont(labelFont.deriveFont(Font.PLAIN, 24))))
            ).weightBy(1.0),
            cell(new JLabel("Page End")).with(cellStyle)
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        setSize(480, 320);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new BorderTest());
    }
}