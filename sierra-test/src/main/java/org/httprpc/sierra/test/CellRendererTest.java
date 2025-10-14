/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.httprpc.sierra.test;

import com.formdev.flatlaf.FlatLightLaf;
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.UILoader;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;

public class CellRendererTest extends JFrame implements Runnable {
    private @Outlet JScrollPane scrollPane = null;
    private @Outlet JList<Flag> flagList = null;

    private List<Flag> flags = listOf(
        new Flag("alpha.png", "Alpha", "I have a diver down; keep well clear at slow speed."),
        new Flag("bravo.png", "Bravo", "I am taking in or discharging or carrying dangerous goods."),
        new Flag("charlie.png", "Charlie", "Affirmative."),
        new Flag("delta.png", "Delta", "Keep clear of me; I am maneuvering with difficulty."),
        new Flag("echo.png", "Echo", "I am altering my course to starboard."),
        new Flag("foxtrot.png", "Foxtrot", "I am disabled; communicate with me."),
        new Flag("golf.png", "Golf", "I require a pilot."),
        new Flag("hotel.png", "Hotel", "I have a pilot on board."),
        new Flag("india.png", "India", "I am altering my course to port."),
        new Flag("juliet.png", "Juliet", "I am leaking dangerous cargo."),
        new Flag("kilo.png", "Kilo", "I wish to communicate with you."),
        new Flag("lima.png", "Lima", "You should stop your vessel instantly."),
        new Flag("mike.png", "Mike", "My vessel is stopped and making no way through the water."),
        new Flag("november.png", "November", "Negative."),
        new Flag("oscar.png", "Oscar", "Man overboard."),
        new Flag("papa.png", "Papa", "My nets have come fast upon an obstruction."),
        new Flag("quebec.png", "Quebec", "My vessel is \"healthy\" and I request free pratique."),
        new Flag("romeo.png", "Romeo", "No ICS meaning as single flag."),
        new Flag("sierra.png", "Sierra", "I am operating astern propulsion."),
        new Flag("tango.png", "Tango", "Keep clear of me."),
        new Flag("uniform.png", "Uniform", "You are running into danger."),
        new Flag("victor.png", "Victor", "I require assistance."),
        new Flag("whisky.png", "Whiskey", "I require medical assistance."),
        new Flag("xray.png", "Xray", "Stop carrying out your intentions and watch for my signals."),
        new Flag("yankee.png", "Yankee", "I am dragging my anchor."),
        new Flag("zulu.png", "Zulu", "I require a tug.")
    );

    private CellRendererTest() {
        super("Cell Renderer Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "CellRendererTest.xml"));

        scrollPane.setBorder(null);

        flagList.setModel(new ListModel<>() {
            @Override
            public int getSize() {
                return flags.size();
            }

            @Override
            public Flag getElementAt(int index) {
                return flags.get(index);
            }

            @Override
            public void addListDataListener(ListDataListener listener) {
                // No-op
            }

            @Override
            public void removeListDataListener(ListDataListener listener) {
                // No-op
            }
        });

        flagList.setCellRenderer(new FlagCellRenderer());

        setSize(420, 560);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new CellRendererTest());
    }
}
