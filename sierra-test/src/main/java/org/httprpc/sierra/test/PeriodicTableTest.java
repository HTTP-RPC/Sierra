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
import org.httprpc.sierra.TextPane;
import org.httprpc.sierra.VerticalAlignment;

import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import static org.httprpc.sierra.UIBuilder.*;

public class PeriodicTableTest extends JFrame implements Runnable {
    private enum Group {
        ALKALI_METAL(0xff6268),
        ALKALINE_EARTH_METAL(0xffddb2),
        LANTHANIDE(0xffbffb),
        ACTINIDE(0xff98c9),
        TRANSITION_METAL(0xffbfc1),
        OTHER_METAL(0xcccccc),
        METALLOID(0xcbcc9e),
        OTHER_NONMETAL(0xb6fda9),
        HALOGEN(0xffffa6),
        NOBLE_GAS(0xbeffff);

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
                new LineBorder(new Color(0x8d1af6)),
                new EmptyBorder(4, 4, 4, 4)
            ));
        }
    }

    private static class KeyPanel extends ColumnPanel {
        KeyPanel(String text, Group group) {
            setPreferredSize(new Dimension(84, 36));

            var labelFont = javax.swing.UIManager.getDefaults().getFont("Label.font");

            var textPane = new TextPane(text);

            textPane.setForeground(new Color(0x0c47a7));
            textPane.setFont(labelFont.deriveFont(Font.PLAIN, 10));
            textPane.setWrapText(true);
            textPane.setHorizontalAlignment(HorizontalAlignment.CENTER);
            textPane.setVerticalAlignment(VerticalAlignment.CENTER);

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
        setContentPane(row(
            glue(),
            column(4,
                row(4,
                    cell(new ElementPanel(1, "H", Group.OTHER_NONMETAL)),
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
                    cell(new ElementPanel(2, "He", Group.NOBLE_GAS))
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
                    cell(new ElementPanel(6, "C", Group.OTHER_NONMETAL)),
                    cell(new ElementPanel(7, "N", Group.OTHER_NONMETAL)),
                    cell(new ElementPanel(8, "O", Group.OTHER_NONMETAL)),
                    cell(new ElementPanel(9, "F", Group.HALOGEN)),
                    cell(new ElementPanel(10, "Ne", Group.NOBLE_GAS))
                ),
                row(4,
                    cell(new ElementPanel(11, "Na", Group.ALKALI_METAL)),
                    cell(new ElementPanel(12, "Mg", Group.ALKALINE_EARTH_METAL)),
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
                    cell(new ElementPanel(13, "Al", Group.OTHER_METAL)),
                    cell(new ElementPanel(14, "Si", Group.METALLOID)),
                    cell(new ElementPanel(15, "P", Group.OTHER_NONMETAL)),
                    cell(new ElementPanel(16, "S", Group.OTHER_NONMETAL)),
                    cell(new ElementPanel(17, "Cl", Group.HALOGEN)),
                    cell(new ElementPanel(18, "Ar", Group.NOBLE_GAS))
                ),
                row(4,
                    cell(new ElementPanel(19, "K", Group.ALKALI_METAL)),
                    cell(new ElementPanel(20, "Ca", Group.ALKALINE_EARTH_METAL)),
                    cell(new ElementPanel(21, "Sc", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(22, "Ti", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(23, "V", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(24, "Cr", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(25, "Mn", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(26, "Fe", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(27, "Co", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(28, "Ni", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(29, "Cu", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(30, "Zn", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(31, "Ga", Group.OTHER_METAL)),
                    cell(new ElementPanel(32, "Ge", Group.METALLOID)),
                    cell(new ElementPanel(33, "As", Group.METALLOID)),
                    cell(new ElementPanel(34, "Se", Group.OTHER_NONMETAL)),
                    cell(new ElementPanel(35, "Br", Group.HALOGEN)),
                    cell(new ElementPanel(36, "Kr", Group.NOBLE_GAS))
                ),
                row(4,
                    cell(new ElementPanel(37, "Rb", Group.ALKALI_METAL)),
                    cell(new ElementPanel(38, "Sr", Group.ALKALINE_EARTH_METAL)),
                    cell(new ElementPanel(39, "Y", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(40, "Zr", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(41, "Nb", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(42, "Mo", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(43, "Tc", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(44, "Ru", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(45, "Rh", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(46, "Pd", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(47, "Ag", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(48, "Cd", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(49, "In", Group.OTHER_METAL)),
                    cell(new ElementPanel(50, "Sn", Group.OTHER_METAL)),
                    cell(new ElementPanel(51, "Sb", Group.METALLOID)),
                    cell(new ElementPanel(52, "Te", Group.METALLOID)),
                    cell(new ElementPanel(53, "I", Group.HALOGEN)),
                    cell(new ElementPanel(54, "Xe", Group.NOBLE_GAS))
                ),
                row(4,
                    cell(new ElementPanel(55, "Cs", Group.ALKALI_METAL)),
                    cell(new ElementPanel(56, "Ba", Group.ALKALINE_EARTH_METAL)),
                    cell(new ElementPanel(57, "La", Group.LANTHANIDE)),
                    cell(new ElementPanel(72, "Hf", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(73, "Ta", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(74, "W", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(75, "Re", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(76, "Os", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(77, "Ir", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(78, "Pt", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(79, "Au", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(80, "Hg", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(81, "Tl", Group.OTHER_METAL)),
                    cell(new ElementPanel(82, "Pb", Group.OTHER_METAL)),
                    cell(new ElementPanel(83, "Bi", Group.OTHER_METAL)),
                    cell(new ElementPanel(84, "Po", Group.OTHER_METAL)),
                    cell(new ElementPanel(85, "At", Group.HALOGEN)),
                    cell(new ElementPanel(86, "Rn", Group.NOBLE_GAS))
                ),
                row(4,
                    cell(new ElementPanel(87, "Fr", Group.ALKALI_METAL)),
                    cell(new ElementPanel(88, "Ra", Group.ALKALINE_EARTH_METAL)),
                    cell(new ElementPanel(89, "Ac", Group.ACTINIDE)),
                    cell(new ElementPanel(104, "Rf", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(105, "Db", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(106, "Sg", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(107, "Bh", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(108, "Hs", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(109, "Mt", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(110, "Ds", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(111, "Rg", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(112, "Cn", Group.TRANSITION_METAL)),
                    cell(new ElementPanel(113, "Nh", Group.OTHER_METAL)),
                    cell(new ElementPanel(114, "Fl", Group.OTHER_METAL)),
                    cell(new ElementPanel(115, "Mc", Group.OTHER_METAL)),
                    cell(new ElementPanel(116, "Lv", Group.OTHER_METAL)),
                    cell(new ElementPanel(117, "Ts", Group.HALOGEN)),
                    cell(new ElementPanel(118, "Og", Group.NOBLE_GAS))
                ),
                row(4,
                    cell(new ElementPanel()),
                    cell(new ElementPanel()),
                    cell(new ElementPanel()),
                    cell(new ElementPanel(58, "Ce", Group.LANTHANIDE)),
                    cell(new ElementPanel(59, "Pr", Group.LANTHANIDE)),
                    cell(new ElementPanel(60, "Nd", Group.LANTHANIDE)),
                    cell(new ElementPanel(61, "Pm", Group.LANTHANIDE)),
                    cell(new ElementPanel(62, "Sm", Group.LANTHANIDE)),
                    cell(new ElementPanel(63, "Eu", Group.LANTHANIDE)),
                    cell(new ElementPanel(64, "Gd", Group.LANTHANIDE)),
                    cell(new ElementPanel(65, "Tb", Group.LANTHANIDE)),
                    cell(new ElementPanel(66, "Dy", Group.LANTHANIDE)),
                    cell(new ElementPanel(67, "Ho", Group.LANTHANIDE)),
                    cell(new ElementPanel(68, "Er", Group.LANTHANIDE)),
                    cell(new ElementPanel(69, "Tm", Group.LANTHANIDE)),
                    cell(new ElementPanel(70, "Yb", Group.LANTHANIDE)),
                    cell(new ElementPanel(71, "Lu", Group.LANTHANIDE)),
                    cell(new ElementPanel())
                ),
                row(4,
                    cell(new ElementPanel()),
                    cell(new ElementPanel()),
                    cell(new ElementPanel()),
                    cell(new ElementPanel(90, "Th", Group.ACTINIDE)),
                    cell(new ElementPanel(91, "Pa", Group.ACTINIDE)),
                    cell(new ElementPanel(92, "U", Group.ACTINIDE)),
                    cell(new ElementPanel(93, "Np", Group.ACTINIDE)),
                    cell(new ElementPanel(94, "Pu", Group.ACTINIDE)),
                    cell(new ElementPanel(95, "Am", Group.ACTINIDE)),
                    cell(new ElementPanel(96, "Cm", Group.ACTINIDE)),
                    cell(new ElementPanel(97, "Bk", Group.ACTINIDE)),
                    cell(new ElementPanel(98, "Cf", Group.ACTINIDE)),
                    cell(new ElementPanel(99, "Es", Group.ACTINIDE)),
                    cell(new ElementPanel(100, "Fm", Group.ACTINIDE)),
                    cell(new ElementPanel(101, "Md", Group.ACTINIDE)),
                    cell(new ElementPanel(102, "No", Group.ACTINIDE)),
                    cell(new ElementPanel(103, "Lr", Group.ACTINIDE)),
                    cell(new ElementPanel())
                ),
                cell(new JSeparator()),
                row(8,
                    glue(),
                    cell(new KeyPanel("Alkali metal", Group.ALKALI_METAL)),
                    cell(new KeyPanel("Alkaline earth metal", Group.ALKALINE_EARTH_METAL)),
                    cell(new KeyPanel("Lanthanide", Group.LANTHANIDE)),
                    cell(new KeyPanel("Actinide", Group.ACTINIDE)),
                    cell(new KeyPanel("Transition metal", Group.TRANSITION_METAL)),
                    cell(new KeyPanel("Other metal", Group.OTHER_METAL)),
                    cell(new KeyPanel("Metalloid", Group.METALLOID)),
                    cell(new KeyPanel("Other non-metal", Group.OTHER_NONMETAL)),
                    cell(new KeyPanel("Halogen", Group.HALOGEN)),
                    cell(new KeyPanel("Noble gas", Group.NOBLE_GAS)),
                    glue()
                )
            ),
            glue()
        ).with(contentPane -> contentPane.setBorder(new EmptyBorder(8, 8, 8, 8))).getComponent());

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new PeriodicTableTest());
    }
}
