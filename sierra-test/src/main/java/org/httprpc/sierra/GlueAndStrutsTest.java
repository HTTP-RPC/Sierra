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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.Color;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.boxPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.east;
import static org.httprpc.sierra.SwingUIBuilder.horizontalGlue;
import static org.httprpc.sierra.SwingUIBuilder.horizontalStrut;
import static org.httprpc.sierra.SwingUIBuilder.north;
import static org.httprpc.sierra.SwingUIBuilder.verticalGlue;
import static org.httprpc.sierra.SwingUIBuilder.verticalStrut;

public class GlueAndStrutsTest extends JFrame implements Runnable {
    private GlueAndStrutsTest() {
        super("Glue and Struts Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(borderPanel(new BorderLayout(),
            north(boxPanel(BoxLayout.X_AXIS,
                horizontalGlue(),
                cell(new JLabel("A")).with(label -> label.setBorder(new LineBorder(Color.GRAY))),
                horizontalStrut(8),
                cell(new JLabel("B")).with(label -> label.setBorder(new LineBorder(Color.GRAY))),
                horizontalStrut(32),
                cell(new JLabel("C")).with(label -> label.setBorder(new LineBorder(Color.GRAY))),
                horizontalGlue()
            )),
            east(boxPanel(BoxLayout.Y_AXIS,
                verticalGlue(),
                cell(new JLabel("A")).with(label -> label.setBorder(new LineBorder(Color.GRAY))),
                verticalStrut(8),
                cell(new JLabel("B")).with(label -> label.setBorder(new LineBorder(Color.GRAY))),
                verticalStrut(32),
                cell(new JLabel("C")).with(label -> label.setBorder(new LineBorder(Color.GRAY))),
                verticalGlue()
            ))
        ));

        setSize(320, 240);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new GlueAndStrutsTest());
    }
}
