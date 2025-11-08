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
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.UILoader;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.util.Enumeration;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;

public class ColorChooserTest extends JFrame implements Runnable {
    private abstract static class ColorTreeNode implements TreeNode {
        String name;
        List<? extends TreeNode> children;

        ColorTreeNode(String name, List<? extends TreeNode> children) {
            this.name = name;
            this.children = children;
        }

        @Override
        public TreeNode getParent() {
            return null;
        }

        @Override
        public TreeNode getChildAt(int index) {
            return children.get(index);
        }

        @Override
        public int getChildCount() {
            return children == null ? 0 : children.size();
        }

        @Override
        public int getIndex(TreeNode node) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getAllowsChildren() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isLeaf() {
            return children == null;
        }

        @Override
        public Enumeration<? extends TreeNode> children() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class RootNode extends ColorTreeNode {
        RootNode(String name, List<ColorGroupNode> children) {
            super(name, children);
        }
    }

    private static class ColorGroupNode extends ColorTreeNode {
        ColorGroupNode(String name, List<ColorNode> children) {
            super(name, children);
        }
    }

    private static class ColorNode extends ColorTreeNode {
        ColorNode(String name) {
            super(name, null);
        }
    }

    private @Outlet JColorChooser colorChooser = null;
    private @Outlet JTree colorTree = null;

    private boolean updatingColor = false;

    private RootNode rootNode = new RootNode("root", listOf(
        new ColorGroupNode("Pink colors", listOf(
            new ColorNode("medium-violet-red"),
            new ColorNode("deep-pink"),
            new ColorNode("pale-violet-red"),
            new ColorNode("hot-pink"),
            new ColorNode("light-pink"),
            new ColorNode("pink")
        )),
        new ColorGroupNode("Red colors", listOf(
            new ColorNode("dark-red"),
            new ColorNode("red"),
            new ColorNode("firebrick"),
            new ColorNode("crimson"),
            new ColorNode("indian-red"),
            new ColorNode("light-coral"),
            new ColorNode("salmon"),
            new ColorNode("dark-salmon"),
            new ColorNode("light-salmon")
        )),
        new ColorGroupNode("Orange colors", listOf(
            new ColorNode("orange-red"),
            new ColorNode("tomato"),
            new ColorNode("dark-orange"),
            new ColorNode("coral"),
            new ColorNode("orange")
        )),
        new ColorGroupNode("Yellow colors", listOf(
            new ColorNode("dark-khaki"),
            new ColorNode("gold"),
            new ColorNode("khaki"),
            new ColorNode("peach-puff"),
            new ColorNode("yellow"),
            new ColorNode("pale-goldenrod"),
            new ColorNode("moccasin"),
            new ColorNode("papaya-whip"),
            new ColorNode("light-goldenrod-yellow"),
            new ColorNode("lemon-chiffon"),
            new ColorNode("light-yellow")
        )),
        new ColorGroupNode("Brown colors", listOf(
            new ColorNode("maroon"),
            new ColorNode("brown"),
            new ColorNode("saddle-brown"),
            new ColorNode("sienna"),
            new ColorNode("chocolate"),
            new ColorNode("dark-goldenrod"),
            new ColorNode("peru"),
            new ColorNode("rosy-brown"),
            new ColorNode("goldenrod"),
            new ColorNode("sandy-brown"),
            new ColorNode("tan"),
            new ColorNode("burlywood"),
            new ColorNode("wheat"),
            new ColorNode("navajo-white"),
            new ColorNode("bisque"),
            new ColorNode("blanched-almond"),
            new ColorNode("cornsilk")
        )),
        new ColorGroupNode("Purple, violet, and magenta colors", listOf(
            new ColorNode("indigo"),
            new ColorNode("purple"),
            new ColorNode("dark-magenta"),
            new ColorNode("dark-violet"),
            new ColorNode("dark-slate-blue"),
            new ColorNode("blue-violet"),
            new ColorNode("dark-orchid"),
            new ColorNode("fuchsia"),
            new ColorNode("magenta"),
            new ColorNode("slate-blue"),
            new ColorNode("medium-slate-blue"),
            new ColorNode("medium-orchid"),
            new ColorNode("medium-purple"),
            new ColorNode("orchid"),
            new ColorNode("violet"),
            new ColorNode("plum"),
            new ColorNode("thistle"),
            new ColorNode("lavender")
        )),
        new ColorGroupNode("Blue colors", listOf(
            new ColorNode("midnight-blue"),
            new ColorNode("navy"),
            new ColorNode("dark-blue"),
            new ColorNode("medium-blue"),
            new ColorNode("blue"),
            new ColorNode("royal-blue"),
            new ColorNode("steel-blue"),
            new ColorNode("dodger-blue"),
            new ColorNode("deep-sky-blue"),
            new ColorNode("cornflower-blue"),
            new ColorNode("skyblue"),
            new ColorNode("light-sky-blue"),
            new ColorNode("light-steel-blue"),
            new ColorNode("light-blue"),
            new ColorNode("powder-blue")
        )),
        new ColorGroupNode("Cyan colors", listOf(
            new ColorNode("teal"),
            new ColorNode("dark-cyan"),
            new ColorNode("light-sea-green"),
            new ColorNode("cadet-blue"),
            new ColorNode("dark-turquoise"),
            new ColorNode("medium-turquoise"),
            new ColorNode("turquoise"),
            new ColorNode("aqua"),
            new ColorNode("cyan"),
            new ColorNode("aquamarine"),
            new ColorNode("pale-turquoise"),
            new ColorNode("light-cyan")
        )),
        new ColorGroupNode("Green colors", listOf(
            new ColorNode("dark-green"),
            new ColorNode("green"),
            new ColorNode("dark-olive-green"),
            new ColorNode("forest-green"),
            new ColorNode("sea-green"),
            new ColorNode("olive"),
            new ColorNode("olive-drab"),
            new ColorNode("medium-sea-green"),
            new ColorNode("lime-green"),
            new ColorNode("lime"),
            new ColorNode("spring-green"),
            new ColorNode("medium-spring-green"),
            new ColorNode("dark-sea-green"),
            new ColorNode("medium-aquamarine"),
            new ColorNode("yellow-green"),
            new ColorNode("lawn-green"),
            new ColorNode("chartreuse"),
            new ColorNode("light-green"),
            new ColorNode("green-yellow"),
            new ColorNode("pale-green")
        )),
        new ColorGroupNode("White colors", listOf(
            new ColorNode("misty-rose"),
            new ColorNode("antique-white"),
            new ColorNode("linen"),
            new ColorNode("beige"),
            new ColorNode("white-smoke"),
            new ColorNode("lavender-blush"),
            new ColorNode("old-lace"),
            new ColorNode("alice-blue"),
            new ColorNode("seashell"),
            new ColorNode("ghost-white"),
            new ColorNode("honeydew"),
            new ColorNode("floral-white"),
            new ColorNode("azure"),
            new ColorNode("mint-cream"),
            new ColorNode("snow"),
            new ColorNode("ivory"),
            new ColorNode("white")
        )),
        new ColorGroupNode("Gray and black colors", listOf(
            new ColorNode("black"),
            new ColorNode("dark-slate-gray"),
            new ColorNode("dim-gray"),
            new ColorNode("slate-gray"),
            new ColorNode("gray"),
            new ColorNode("light-slate-gray"),
            new ColorNode("dark-gray"),
            new ColorNode("silver"),
            new ColorNode("light-gray"),
            new ColorNode("gainsboro")
        ))
    ));

    private ColorChooserTest() {
        super("Color Chooser Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "ColorChooserTest.xml"));

        colorTree.setModel(new DefaultTreeModel(rootNode));

        colorTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        colorTree.addTreeSelectionListener(event -> {
            var selectionPath = colorTree.getSelectionPath();

            if (selectionPath == null) {
                return;
            }

            var selectedNode = (TreeNode)selectionPath.getLastPathComponent();

            if (selectedNode.isLeaf()) {
                updatingColor = true;

                colorChooser.setColor(UILoader.getColor(selectedNode.toString()));

                updatingColor = false;
            }
        });

        colorChooser.getSelectionModel().addChangeListener(event -> {
            if (!updatingColor) {
                colorTree.clearSelection();
            }
        });

        setSize(1024, 480);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ColorChooserTest());
    }
}
