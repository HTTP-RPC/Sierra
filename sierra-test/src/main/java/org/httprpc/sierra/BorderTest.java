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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.util.function.Consumer;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.row;

public class BorderTest extends JFrame implements Runnable {
    private BorderTest() {
        super("Border Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        Consumer<JLabel> cellStyle = label -> {
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY),
                new EmptyBorder(4, 4, 4, 4)
            ));
        };

        setContentPane(column(
            cell(new JLabel("Page Start")).with(cellStyle),
            row(
                cell(new JLabel("Line Start")).with(cellStyle),
                cell(new JLabel("Center")).weightBy(1.0).with(cellStyle),
                cell(new JLabel("Line End")).with(cellStyle)
            ).weightBy(1.0).with(rowPanel -> rowPanel.setSpacing(4)),
            cell(new JLabel("Page End")).with(cellStyle)
        ).with(columnPanel -> {
            columnPanel.setSpacing(4);
            columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        }).getComponent());

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new BorderTest());
    }
}