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
import org.httprpc.sierra.UILoader;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;

public class ColorChooserTest extends JFrame implements Runnable {
    private record ColorGroup(String name, List<String> colors) {
        @Override
        public String toString() {
            return name;
        }
    }

    private JColorChooser colorChooser = null;
    private JTree colorTree = null;

    private List<ColorGroup> colorGroups = listOf(
        new ColorGroup("Pink colors", listOf(
            "medium-violet-red",
            "deep-pink",
            "pale-violet-red",
            "hot-pink",
            "light-pink",
            "pink"
        )),

        new ColorGroup("Red colors", listOf(
            "dark-red",
            "red",
            "firebrick",
            "crimson",
            "indian-red",
            "light-coral",
            "salmon",
            "dark-salmon",
            "light-salmon"
        ))
    );

    private ColorChooserTest() {
        super("Color Chooser Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "ColorChooserTest.xml"));

        colorTree.setModel(new TreeModel() {
            @Override
            public Object getRoot() {
                return colorGroups;
            }

            List<?> getList(Object parent) {
                return (parent instanceof List<?>) ? (List<?>)parent : ((ColorGroup)parent).colors();
            }

            @Override
            public int getChildCount(Object parent) {
                return getList(parent).size();
            }

            @Override
            public Object getChild(Object parent, int index) {
                return getList(parent).get(index);
            }

            @Override
            public int getIndexOfChild(Object parent, Object child) {
                return getList(parent).indexOf(child);
            }

            @Override
            public boolean isLeaf(Object node) {
                return node instanceof String;
            }

            @Override
            public void addTreeModelListener(TreeModelListener listener) {
                // No-op
            }

            @Override
            public void removeTreeModelListener(TreeModelListener listener) {
                // No-op
            }

            @Override
            public void valueForPathChanged(TreePath path, Object newValue) {
                // No-op
            }
        });

        setSize(960, 480);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ColorChooserTest());
    }
}
