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
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.column;
import static org.httprpc.sierra.UIBuilder.row;

public class PeriodicTableTest extends JFrame implements Runnable {
    private enum Group {
        ALKALI_METAL(0xff6268),
        ALKALINE_EARTH_METAL(0xffddb2),
        LANTHANIDE(0xffbffb),
        ACTINIDE(0xff98c9),
        TRANSITION_METAL(0xffbfc1),
        POST_TRANSITION_METAL(0xcccccc),
        METALLOID(0xcbcc9e),
        REACTIVE_NONMETAL(0xe5ff9c),
        NOBLE_GAS(0xbeffff),
        UNKNOWN(0xe8e8e8);

        final Color color;

        Group(int rgb) {
            color = new Color(rgb);
        }
    }

    private static class ElementPanel extends ColumnPanel {
        ElementPanel() {
            setPreferredSize(new Dimension(48, 48));
        }

        ElementPanel(int number, String symbol, Group group) {
            this();

            var labelFont = javax.swing.UIManager.getDefaults().getFont("Label.font");

            var numberTextPane = new TextPane(String.valueOf(number));

            numberTextPane.setForeground(new Color(0x0c47a7));
            numberTextPane.setHorizontalAlignment(HorizontalAlignment.CENTER);
            numberTextPane.setFont(labelFont.deriveFont(Font.PLAIN, 12));

            add(numberTextPane);

            var symbolTextPane = new TextPane(symbol);

            symbolTextPane.setForeground(new Color(0x0c47a7));
            symbolTextPane.setHorizontalAlignment(HorizontalAlignment.CENTER);
            symbolTextPane.setFont(labelFont.deriveFont(Font.PLAIN, 16));

            add(symbolTextPane, 1.0);

            setOpaque(true);
            setBackground(group.color);

            setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(0x8d1af6)),
                new EmptyBorder(4, 4, 4, 4)
            ));
        }
    }

    private static class KeyPanel extends ColumnPanel {
        KeyPanel(String text, Group group) {
            var labelFont = javax.swing.UIManager.getDefaults().getFont("Label.font");

            var textPane = new TextPane(text, true);

            textPane.setForeground(new Color(0x0c47a7));
            textPane.setHorizontalAlignment(HorizontalAlignment.CENTER);
            textPane.setVerticalAlignment(VerticalAlignment.CENTER);
            textPane.setFont(labelFont.deriveFont(Font.PLAIN, 12));

            add(textPane, 1.0);

            setOpaque(true);
            setBackground(group.color);

            setBorder(new EmptyBorder(2, 2, 2, 2));
        }
    }

    private PeriodicTableTest() {
        super("Periodic Table Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(column(4,
            row(4,
                cell(new ElementPanel(1, "H", Group.REACTIVE_NONMETAL)),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel(1, "He", Group.NOBLE_GAS))
            ),
            row(4,
                cell(new ElementPanel(3, "Li", Group.ALKALI_METAL)),
                cell(new ElementPanel(4, "Be", Group.ALKALINE_EARTH_METAL)),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel()),
                cell(new ElementPanel(5, "B", Group.METALLOID)),
                cell(new ElementPanel(6, "C", Group.REACTIVE_NONMETAL)),
                cell(new ElementPanel(7, "N", Group.REACTIVE_NONMETAL)),
                cell(new ElementPanel(8, "O", Group.REACTIVE_NONMETAL)),
                cell(new ElementPanel(9, "F", Group.REACTIVE_NONMETAL)),
                cell(new ElementPanel(10, "Ne", Group.NOBLE_GAS))
            ),
            cell(new JSeparator()),
            row(8,
                cell(new KeyPanel("Alkali metal", Group.ALKALI_METAL)).weightBy(1),
                cell(new KeyPanel("Alkaline earth metal", Group.ALKALINE_EARTH_METAL)).weightBy(1),
                cell(new KeyPanel("Lanthanide", Group.LANTHANIDE)).weightBy(1),
                cell(new KeyPanel("Actinide", Group.ACTINIDE)).weightBy(1),
                cell(new KeyPanel("Transition metal", Group.TRANSITION_METAL)).weightBy(1),
                cell(new KeyPanel("Post-transition metal", Group.POST_TRANSITION_METAL)).weightBy(1),
                cell(new KeyPanel("Metalloid", Group.METALLOID)).weightBy(1),
                cell(new KeyPanel("Reactive nonmetal", Group.REACTIVE_NONMETAL)).weightBy(1),
                cell(new KeyPanel("Noble gas", Group.NOBLE_GAS)).weightBy(1),
                cell(new KeyPanel("Unknown", Group.UNKNOWN)).weightBy(1)
            )
        ).with(columnPanel -> columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        KeyboardFocusManager.setCurrentKeyboardFocusManager(new ScrollingKeyboardFocusManager());

        SwingUtilities.invokeLater(new PeriodicTableTest());
    }
}
