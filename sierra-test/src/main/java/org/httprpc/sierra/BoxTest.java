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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.glue;
import static org.httprpc.sierra.UIBuilder.row;
import static org.httprpc.sierra.UIBuilder.strut;

public class BoxTest extends JFrame implements Runnable {
    private BoxTest() {
        super("Box Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(column(4,
            row(
                cell(new JButton("1a")),
                strut(4),
                cell(new JButton("1b")),
                strut(4),
                cell(new JButton("1c")),
                glue()
            ),
            row(
                glue(),
                cell(new JButton("2a")),
                strut(4),
                cell(new JButton("2b")),
                strut(4),
                cell(new JButton("2c"))
            ),
            row(
                glue(),
                cell(new JButton("3a")),
                strut(4),
                cell(new JButton("3b")),
                strut(4),
                cell(new JButton("3c")),
                glue()
            ),
            cell(new JButton("4")).weightBy(1.0),
            cell(new JButton("5"))
        ).with(columnPanel -> columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new BoxTest());
    }
}
