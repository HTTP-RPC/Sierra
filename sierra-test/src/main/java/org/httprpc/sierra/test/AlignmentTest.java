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
import org.httprpc.sierra.HorizontalAlignment;
import org.httprpc.sierra.TextPane;
import org.httprpc.sierra.VerticalAlignment;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.util.function.Consumer;

import static org.httprpc.sierra.UIBuilder.*;

public class AlignmentTest extends JFrame implements Runnable {
    private static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

    private AlignmentTest() {
        super("Alignment Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        Consumer<TextPane> cellStyle = textPane -> textPane.setBorder(new LineBorder(Color.LIGHT_GRAY));

        setContentPane(column(4, true,
            row(
                cell(new TextPane("abc")).with(cellStyle.andThen(textPane -> {
                    textPane.setAlignmentX(0.25f);
                    textPane.setAlignmentY(0.0f);
                })),
                cell(new TextPane(TEXT)).with(cellStyle.andThen(textPane -> textPane.setWrapText(true))).weightBy(1),
                cell(new TextPane("def")).with(cellStyle.andThen(textPane -> {
                    textPane.setAlignmentX(1.0f);
                    textPane.setAlignmentY(0.25f);
                }))
            ),
            row(16,
                cell(new TextPane("abcdef")).with(cellStyle.andThen(textPane -> textPane.setAlignmentX(1.0f))),
                cell(new TextPane("ABCDEFGHIJKL")).with(cellStyle),
                cell(new TextPane("ghijkl")).with(cellStyle.andThen(textPane -> textPane.setAlignmentX(0.0f)))
            ),
            row(
                cell(new TextPane("ghi")).with(cellStyle.andThen(textPane -> {
                    textPane.setVerticalAlignment(VerticalAlignment.BOTTOM);
                    textPane.setAlignmentX(0.0f);
                    textPane.setAlignmentY(0.75f);
                })),
                cell(new TextPane(TEXT)).with(cellStyle.andThen(textPane -> textPane.setWrapText(true))).weightBy(1),
                cell(new TextPane("jkl")).with(cellStyle.andThen(textPane -> {
                    textPane.setHorizontalAlignment(HorizontalAlignment.TRAILING);
                    textPane.setAlignmentX(0.75f);
                    textPane.setAlignmentY(1.0f);
                }))
            )
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new AlignmentTest());
    }
}