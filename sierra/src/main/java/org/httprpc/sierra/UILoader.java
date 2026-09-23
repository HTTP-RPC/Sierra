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

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.httprpc.kilo.beans.BeanAdapter;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RepaintManager;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.httprpc.kilo.util.Optionals.*;

/**
 * Provides support for deserializing a component hierarchy from markup.
 */
public class UILoader {
    /**
     * Represents a markup attribute.
     */
    public enum Attribute {
        /**
         * Name attribute.
         */
        NAME("name", String.class),

        /**
         * Title attribute.
         */
        TITLE("title", String.class),

        /**
         * Title color attribute.
         */
        TITLE_COLOR("titleColor", String.class),

        /**
         * Title font attribute.
         */
        TITLE_FONT("titleFont", String.class),

        /**
         * Title justification attribute.
         */
        TITLE_JUSTIFICATION("titleJustification", TitleJustification.class),

        /**
         * Title position attribute.
         */
        TITLE_POSITION("titlePosition", TitlePosition.class),

        /**
         * Border attribute.
         */
        BORDER("border", String.class),

        /**
         * Padding attribute.
         */
        PADDING("padding", String.class),

        /**
         * Width attribute.
         */
        WIDTH("width", String.class),

        /**
         * Height attribute.
         */
        HEIGHT("height", String.class),

        /**
         * Size attribute.
         */
        SIZE("size", String.class),

        /**
         * Weight attribute.
         */
        WEIGHT("weight", Double.class),

        /**
         * Label attribute.
         */
        LABEL("label", String.class),

        /**
         * Column span attribute.
         */
        COLUMN_SPAN("columnSpan", Integer.class),

        /**
         * Group attribute.
         */
        GROUP("group", String.class),

        /**
         * Tab title attribute.
         */
        TAB_TITLE("tabTitle", String.class),

        /**
         * Tab icon attribute.
         */
        TAB_ICON("tabIcon", String.class),

        /**
         * Style attribute.
         */
        STYLE("style", String.class),

        /**
         * Style class attribute.
         */
        STYLE_CLASS("styleClass", String.class),

        /**
         * Placeholder text attribute.
         */
        PLACEHOLDER_TEXT("placeholderText", String.class),

        /**
         * Show clear button attribute.
         */
        SHOW_CLEAR_BUTTON("showClearButton", Boolean.class),

        /**
         * Leading icon attribute.
         */
        LEADING_ICON("leadingIcon", String.class),

        /**
         * Trailing icon attribute.
         */
        TRAILING_ICON("trailingIcon", String.class),

        /**
         * Horizontal alignment attribute.
         */
        HORIZONTAL_ALIGNMENT("horizontalAlignment", HorizontalAlignment.class),

        /**
         * Vertical alignment attribute.
         */
        VERTICAL_ALIGNMENT("verticalAlignment", VerticalAlignment.class),

        /**
         * Horizontal text position attribute.
         */
        HORIZONTAL_TEXT_POSITION("horizontalTextPosition", HorizontalAlignment.class),

        /**
         * Vertical text position attribute.
         */
        VERTICAL_TEXT_POSITION("verticalTextPosition", VerticalAlignment.class),

        /**
         * Orientation attribute.
         */
        ORIENTATION("orientation", Orientation.class),

        /**
         * Focus lost behavior attribute.
         */
        FOCUS_LOST_BEHAVIOR("focusLostBehavior", FocusLostBehavior.class),

        /**
         * Horizontal scroll bar policy attribute.
         */
        HORIZONTAL_SCROLL_BAR_POLICY("horizontalScrollBarPolicy", HorizontalScrollBarPolicy.class),

        /**
         * Vertical scroll bar policy attribute.
         */
        VERTICAL_SCROLL_BAR_POLICY("verticalScrollBarPolicy", VerticalScrollBarPolicy.class),

        /**
         * Selection mode attribute.
         */
        SELECTION_MODE("selectionMode", ListSelectionMode.class),

        /**
         * Layout orientation attribute.
         */
        LAYOUT_ORIENTATION("layoutOrientation", LayoutOrientation.class),

        /**
         * Auto-resize mode attribute.
         */
        AUTO_RESIZE_MODE("autoResizeMode", AutoResizeMode.class),

        /**
         * Tab placement attribute.
         */
        TAB_PLACEMENT("tabPlacement", TabPlacement.class),

        /**
         * Tab layout policy attribute.
         */
        TAB_LAYOUT_POLICY("tabLayoutPolicy", TabLayoutPolicy.class);

        private final String name;
        private final Class<?> type;

        Attribute(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }
    }

    /**
     * Title justification options.
     */
    public enum TitleJustification implements ConstantAdapter {
        /**
         * Left justification.
         */
        LEFT("left", TitledBorder.LEFT),

        /**
         * Right justification.
         */
        RIGHT("right", TitledBorder.RIGHT),

        /**
         * Center justification.
         */
        CENTER("center", TitledBorder.CENTER),

        /**
         * Leading justification.
         */
        LEADING("leading", TitledBorder.LEADING),

        /**
         * Trailing justification.
         */
        TRAILING("trailing", TitledBorder.TRAILING);

        private final String key;
        private final int value;

        TitleJustification(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Title position options.
     */
    public enum TitlePosition implements ConstantAdapter {
        /**
         * Above top position.
         */
        ABOVE_TOP("above-top", TitledBorder.ABOVE_TOP),

        /**
         * Top position.
         */
        TOP("top", TitledBorder.TOP),

        /**
         * Below top position.
         */
        BELOW_TOP("below-top", TitledBorder.BELOW_TOP),

        /**
         * Above bottom position.
         */
        ABOVE_BOTTOM("above-bottom", TitledBorder.ABOVE_BOTTOM),

        /**
         * Bottom position.
         */
        BOTTOM("bottom", TitledBorder.BOTTOM),

        /**
         * Below bottom position.
         */
        BELOW_BOTTOM("below-bottom", TitledBorder.BELOW_BOTTOM);

        private final String key;
        private final int value;

        TitlePosition(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Focus lost behavior options.
     */
    public enum FocusLostBehavior implements ConstantAdapter {
        /**
         * Commit.
         */
        COMMIT("commit", JFormattedTextField.COMMIT),

        /**
         * Commit or revert.
         */
        COMMIT_OR_REVERT("commit-or-revert", JFormattedTextField.COMMIT_OR_REVERT),

        /**
         * Revert.
         */
        REVERT("revert", JFormattedTextField.REVERT),

        /**
         * Persist.
         */
        PERSIST("persist", JFormattedTextField.PERSIST);

        private final String key;
        private final int value;

        FocusLostBehavior(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Horizontal scroll bar policy options.
     */
    public enum HorizontalScrollBarPolicy implements ConstantAdapter {
        /**
         * As needed.
         */
        AS_NEEDED("as-needed", ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),

        /**
         * Never.
         */
        NEVER("never", ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),

        /**
         * Always.
         */
        ALWAYS("always", ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        private final String key;
        private final int value;

        HorizontalScrollBarPolicy(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Vertical scroll bar policy options.
     */
    public enum VerticalScrollBarPolicy implements ConstantAdapter {
        /**
         * As needed.
         */
        AS_NEEDED("as-needed", ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED),

        /**
         * Never.
         */
        NEVER("never", ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER),

        /**
         * Always.
         */
        ALWAYS("always", ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        private final String key;
        private final int value;

        VerticalScrollBarPolicy(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * List selection mode options.
     */
    public enum ListSelectionMode implements ConstantAdapter {
        /**
         * Single selection.
         */
        SINGLE_SELECTION("single-selection", ListSelectionModel.SINGLE_SELECTION),

        /**
         * Single interval selection.
         */
        SINGLE_INTERVAL_SELECTION("single-interval-selection", ListSelectionModel.SINGLE_INTERVAL_SELECTION),

        /**
         * Multiple interval selection.
         */
        MULTIPLE_INTERVAL_SELECTION("multiple-interval-selection", ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        private final String key;
        private final int value;

        ListSelectionMode(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Layout orientation options.
     */
    public enum LayoutOrientation implements ConstantAdapter {
        /**
         * Vertical.
         */
        VERTICAL("vertical", JList.VERTICAL),

        /**
         * Vertical wrap.
         */
        VERTICAL_WRAP("vertical-wrap", JList.VERTICAL_WRAP),

        /**
         * Horizontal wrap.
         */
        HORIZONTAL_WRAP("horizontal-wrap", JList.HORIZONTAL_WRAP);

        private final String key;
        private final int value;

        LayoutOrientation(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Auto-resize mode options.
     */
    public enum AutoResizeMode implements ConstantAdapter {
        /**
         * Auto-resize off.
         */
        OFF("off", JTable.AUTO_RESIZE_OFF),

        /**
         * Auto-resize next column.
         */
        NEXT_COLUMN("next-column", JTable.AUTO_RESIZE_NEXT_COLUMN),

        /**
         * Auto-resize subsequent columns.
         */
        SUBSEQUENT_COLUMNS("subsequent-columns", JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS),

        /**
         * Auto-resize last column.
         */
        LAST_COLUMN("last-column", JTable.AUTO_RESIZE_LAST_COLUMN),

        /**
         * Auto-resize all columns.
         */
        ALL_COLUMNS("all-columns", JTable.AUTO_RESIZE_ALL_COLUMNS);

        private final String key;
        private final int value;

        AutoResizeMode(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Tree selection mode options.
     */
    public enum TreeSelectionMode implements ConstantAdapter {
        /**
         * Single tree selection.
         */
        SINGLE_TREE_SELECTION("single-tree-selection", TreeSelectionModel.SINGLE_TREE_SELECTION),

        /**
         * Contiguous tree selection.
         */
        CONTIGUOUS_TREE_SELECTION("contiguous-tree-selection", TreeSelectionModel.CONTIGUOUS_TREE_SELECTION),

        /**
         * Discontiguous tree selection.
         */
        DISCONTIGUOUS_TREE_SELECTION("discontiguous-tree-selection", TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        private final String key;
        private final int value;

        TreeSelectionMode(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Tab placement options.
     */
    public enum TabPlacement implements ConstantAdapter {
        /**
         * Top tab placement.
         */
        TOP("top", JTabbedPane.TOP),

        /**
         * Left tab placement.
         */
        LEFT("left", JTabbedPane.LEFT),

        /**
         * Bottom tab placement.
         */
        BOTTOM("bottom", JTabbedPane.BOTTOM),

        /**
         * Right tab placement.
         */
        RIGHT("right", JTabbedPane.RIGHT);

        private final String key;
        private final int value;

        TabPlacement(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Tab layout policy options.
     */
    public enum TabLayoutPolicy implements ConstantAdapter {
        /**
         * Wrap tab layout policy.
         */
        WRAP_TAB_LAYOUT("wrap-tab-layout", JTabbedPane.WRAP_TAB_LAYOUT),

        /**
         * Scroll tab layout policy.
         */
        SCROLL_TAB_LAYOUT("scroll-tab-layout", JTabbedPane.SCROLL_TAB_LAYOUT);

        private final String key;
        private final int value;

        TabLayoutPolicy(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    /**
     * Internal extension of {@link JTable}.
     */
    public static class JxTable extends JTable {
        /**
         * Returns the row selection mode.
         *
         * @return
         * The row selection mode.
         */
        public ListSelectionMode getRowSelectionMode() {
            return getValue(getSelectionModel().getSelectionMode(), ListSelectionMode.values());
        }

        /**
         * Sets the row selection mode.
         *
         * @param rowSelectionMode
         * The row selection mode.
         */
        @SuppressWarnings("MagicConstant")
        public void setRowSelectionMode(ListSelectionMode rowSelectionMode) {
            if (rowSelectionMode == null) {
                throw new IllegalArgumentException();
            }

            getSelectionModel().setSelectionMode(rowSelectionMode.getValue());
        }

        /**
         * Returns the column selection mode.
         *
         * @return
         * The column selection mode.
         */
        public ListSelectionMode getColumnSelectionMode() {
            return getValue(getColumnModel().getSelectionModel().getSelectionMode(), ListSelectionMode.values());
        }

        /**
         * Sets the column selection mode.
         *
         * @param columnSelectionMode
         * The column selection mode.
         */
        @SuppressWarnings("MagicConstant")
        public void setColumnSelectionMode(ListSelectionMode columnSelectionMode) {
            if (columnSelectionMode == null) {
                throw new IllegalArgumentException();
            }

            getColumnModel().getSelectionModel().setSelectionMode(columnSelectionMode.getValue());
        }
    }

    /**
     * Internal extension of {@link JTree}.
     */
    public static class JxTree extends JTree {
        /**
         * Returns the selection mode.
         *
         * @return
         * The selection mode.
         */
        public TreeSelectionMode getSelectionMode() {
            return getValue(getSelectionModel().getSelectionMode(), TreeSelectionMode.values());
        }

        /**
         * Sets the selection mode.
         *
         * @param selectionMode
         * The selection mode.
         */
        @SuppressWarnings("MagicConstant")
        public void setSelectionMode(TreeSelectionMode selectionMode) {
            if (selectionMode == null) {
                throw new IllegalArgumentException();
            }

            getSelectionModel().setSelectionMode(selectionMode.getValue());
        }
    }

    private static class RoundedLineBorder implements Border {
        Color color;
        BasicStroke stroke;
        int cornerRadius;

        RoundedLineBorder(Color color, BasicStroke stroke, int cornerRadius) {
            this.color = color;
            this.stroke = stroke;
            this.cornerRadius = cornerRadius;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            paintBorder((JComponent)component, (Graphics2D)graphics, x, y, width, height);
        }

        void paintBorder(JComponent component, Graphics2D graphics, int x, int y, int width, int height) {
            graphics = (Graphics2D)graphics.create();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            if (component.getBorder() instanceof CompoundBorder compoundBorder
                && compoundBorder.getOutsideBorder() == this) {
                var maskColor = getOpaqueBackground(component.getParent());

                if (maskColor != null) {
                    var insets = component.isOpaque() ? new Insets(0, 0, 0, 0) : compoundBorder.getBorderInsets(component);

                    var maskThickness = Math.ceil(cornerRadius * (Math.sqrt(2) - 1));

                    var maskEdge = maskThickness / Math.sqrt(2);

                    if (insets.top < maskEdge
                        || insets.left < maskEdge
                        || insets.bottom < maskEdge
                        || insets.right < maskEdge) {
                        graphics.setColor(maskColor);
                        graphics.setStroke(new BasicStroke((float)maskThickness));

                        var maskArc = cornerRadius * 2 + maskThickness;

                        graphics.draw(new RoundRectangle2D.Double(-maskThickness / 2, -maskThickness / 2,
                            width + maskThickness, height + maskThickness,
                            maskArc, maskArc));
                    }
                }
            }

            graphics.setColor(color);
            graphics.setStroke(stroke);

            var thickness = (double)stroke.getLineWidth();

            var arc = cornerRadius * 2 - thickness;

            graphics.draw(new RoundRectangle2D.Double(x + thickness / 2, y + thickness / 2,
                width - thickness, height - thickness,
                arc, arc));

            graphics.dispose();
        }

        @Override
        public Insets getBorderInsets(Component component) {
            var thickness = (int)Math.floor(stroke.getLineWidth());

            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public boolean isBorderOpaque() {
            return cornerRadius == 0;
        }

        static Color getOpaqueBackground(Component component) {
            if (component == null) {
                return null;
            } else if (component.isOpaque()) {
                return component.getBackground();
            } else {
                return getOpaqueBackground(component.getParent());
            }
        }
    }

    private static class LabelColorMapper implements Function<Color, Color> {
        @Override
        public Color apply(Color color) {
            return UIManager.getColor("Label.foreground");
        }
    }

    private static class ButtonColorMapper implements Function<Color, Color> {
        JButton button;

        ButtonColorMapper(JButton button) {
            this.button = button;
        }

        @Override
        public Color apply(Color color) {
            if (button.isSelected()) {
                return UIManager.getColor("Button.selectedForeground");
            } else {
                return UIManager.getColor("Button.foreground");
            }
        }
    }

    private static class ToggleButtonColorMapper implements Function<Color, Color> {
        JToggleButton toggleButton;

        ToggleButtonColorMapper(JToggleButton toggleButton) {
            this.toggleButton = toggleButton;
        }

        @Override
        public Color apply(Color color) {
            if (toggleButton.isSelected()) {
                return UIManager.getColor("ToggleButton.selectedForeground");
            } else {
                return UIManager.getColor("ToggleButton.foreground");
            }
        }
    }

    private static class MenuItemColorMapper implements Function<Color, Color> {
        JMenuItem menuItem;

        MenuItemColorMapper(JMenuItem menuItem) {
            this.menuItem = menuItem;
        }

        @Override
        public Color apply(Color color) {
            if (menuItem.isSelected() || menuItem.isArmed()) {
                return UIManager.getColor("MenuItem.selectionForeground");
            } else {
                return UIManager.getColor("MenuItem.foreground");
            }
        }
    }

    private static class TextFieldColorMapper implements Function<Color, Color> {
        @Override
        public Color apply(Color color) {
            return UIManager.getColor("TextField.placeholderForeground");
        }
    }

    private static class TabbedPaneColorMapper implements Function<Color, Color> {
        @Override
        public Color apply(Color color) {
            return UIManager.getColor("TabbedPane.foreground");
        }
    }

    private static class LoadException extends RuntimeException {
        Location location;

        LoadException(RuntimeException exception, Location location) {
            super(exception);

            this.location = location;
        }

        LoadException(Exception exception) {
            super(exception);

            location = null;
        }

        @Override
        public String getMessage() {
            var message = getCause().getMessage();

            if (location == null) {
                return message;
            } else {
                var lineNumber = location.getLineNumber();

                if (lineNumber == -1) {
                    return message;
                } else {
                    return String.format("[Line %d] %s", lineNumber, message);
                }
            }
        }
    }

    private Object owner;
    private String name;
    private ResourceBundle resourceBundle;

    private Path path;

    private Map<String, Field> fields = new HashMap<>();
    private Map<String, ButtonGroup> groups = new HashMap<>();

    private Map<String, Icon> icons = new HashMap<>();
    private Map<String, Image> images = new HashMap<>();

    private Deque<JComponent> components = new LinkedList<>();

    private JComponent root = null;

    private static final Map<String, Class<? extends JComponent>> types = new HashMap<>();
    private static final Map<String, Supplier<? extends JComponent>> suppliers = new HashMap<>();

    private static final Map<String, Color> colors = new HashMap<>();
    private static final Map<String, Font> fonts = new HashMap<>();

    private static final Map<String, Integer> keyCodes = new HashMap<>();

    static {
        bind("label", JLabel.class, JLabel::new);
        bind("button", JButton.class, JButton::new);
        bind("toggle-button", JToggleButton.class, JToggleButton::new);
        bind("radio-button", JRadioButton.class, JRadioButton::new);
        bind("check-box", JCheckBox.class, JCheckBox::new);
        bind("text-field", JTextField.class, JTextField::new);
        bind("formatted-text-field", JFormattedTextField.class, JFormattedTextField::new);
        bind("password-field", JPasswordField.class, JPasswordField::new);
        bind("combo-box", JComboBox.class, JComboBox::new);
        bind("spinner", JSpinner.class, JSpinner::new);
        bind("slider", JSlider.class, JSlider::new);
        bind("progress-bar", JProgressBar.class, JProgressBar::new);
        bind("separator", JSeparator.class, JSeparator::new);
        bind("scroll-pane", JScrollPane.class, JScrollPane::new);
        bind("list", JList.class, JList::new);
        bind("text-area", JTextArea.class, JTextArea::new);
        bind("tool-bar", JToolBar.class, JToolBar::new);
        bind("tool-bar-separator", JToolBar.Separator.class, JToolBar.Separator::new);
        bind("menu-bar", JMenuBar.class, JMenuBar::new);
        bind("menu", JMenu.class, JMenu::new);
        bind("menu-item", JMenuItem.class, JMenuItem::new);
        bind("check-box-menu-item", JCheckBoxMenuItem.class, JCheckBoxMenuItem::new);
        bind("radio-button-menu-item", JRadioButtonMenuItem.class, JRadioButtonMenuItem::new);
        bind("popup-menu-separator", JPopupMenu.Separator.class, JPopupMenu.Separator::new);
        bind("split-pane", JSplitPane.class, JSplitPane::new);
        bind("tabbed-pane", JTabbedPane.class, JTabbedPane::new);

        bind("table", JxTable.class, JxTable::new);
        bind("tree", JxTree.class, JxTree::new);

        bind("row-panel", RowPanel.class, RowPanel::new);
        bind("column-panel", ColumnPanel.class, ColumnPanel::new);
        bind("form-panel", FormPanel.class, FormPanel::new);
        bind("table-panel", TablePanel.class, TablePanel::new);
        bind("stack-panel", StackPanel.class, StackPanel::new);
        bind("spacer", Spacer.class, Spacer::new);
        bind("text-pane", TextPane.class, TextPane::new);
        bind("image-pane", ImagePane.class, ImagePane::new);
        bind("number-field", NumberField.class, NumberField::new);
        bind("validated-text-field", ValidatedTextField.class, ValidatedTextField::new);
        bind("date-picker", DatePicker.class, DatePicker::new);
        bind("time-picker", TimePicker.class, TimePicker::new);
        bind("suggestion-picker", SuggestionPicker.class, SuggestionPicker::new);
        bind("menu-button", MenuButton.class, MenuButton::new);
        bind("activity-indicator", ActivityIndicator.class, ActivityIndicator::new);
        bind("badge", Badge.class, Badge::new);

        bind("chart-pane", ChartPane.class, ChartPane::new);
    }

    static {
        var fields = KeyEvent.class.getDeclaredFields();

        for (var i = 0; i < fields.length; i++) {
            var field = fields[i];

            var modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers)
                && Modifier.isStatic(modifiers)
                && Modifier.isFinal(modifiers)) {
                var name = field.getName();

                if (name.startsWith("VK_") && field.getType() == Integer.TYPE) {
                    try {
                        keyCodes.put(name, (Integer)field.get(null));
                    } catch (IllegalAccessException exception) {
                        throw new RuntimeException(exception);
                    }
                }
            }
        }
    }

    static {
        RepaintManager.setCurrentManager(new RepaintManager() {
            @Override
            public void addDirtyRegion(JComponent component, int x, int y, int width, int height) {
                if (component.getParent() instanceof JComponent parent) {
                    if (component instanceof LayoutPanel) {
                        var border = component.getBorder();

                        if (border != null && !border.isBorderOpaque()) {
                            var insets = border.getBorderInsets(component);

                            var left = Math.max(x - insets.left, 0);
                            var top = Math.max(y - insets.top, 0);

                            var size = component.getSize();

                            var right = Math.min(size.width - (x + width), 0);
                            var bottom = Math.min(size.height - (y + height), 0);

                            x = left;
                            y = top;

                            width = size.width - (left + right);
                            height = size.height - (top + bottom);
                        }
                    }

                    addDirtyRegion(parent, x + component.getX(), y + component.getY(), width, height);
                } else {
                    super.addDirtyRegion(component, x, y, width, height);
                }
            }
        });
    }

    private UILoader(Object owner, String name, ResourceBundle resourceBundle) {
        this.owner = owner;
        this.name = name;
        this.resourceBundle = resourceBundle;
    }

    private UILoader(Path path) {
        this.path = path;
    }

    private JComponent load() {
        if (owner != null) {
            var fields = owner.getClass().getDeclaredFields();

            for (var i = 0; i < fields.length; i++) {
                var field = fields[i];

                if (JComponent.class.isAssignableFrom(field.getType())) {
                    this.fields.put(field.getName(), field);
                }
            }
        }

        var xmlInputFactory = XMLInputFactory.newInstance();

        xmlInputFactory.setProperty("javax.xml.stream.isNamespaceAware", false);
        xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        xmlInputFactory.setProperty("javax.xml.stream.supportDTD", false);

        try (var inputStream = open()) {
            if (inputStream == null) {
                throw new UnsupportedOperationException("Named resource does not exist.");
            }

            var xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);

            while (xmlStreamReader.hasNext()) {
                switch (xmlStreamReader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> {
                        try {
                            processStartElement(xmlStreamReader);
                        } catch (RuntimeException exception) {
                            throw new LoadException(exception, xmlStreamReader.getLocation());
                        }
                    }
                    case XMLStreamConstants.END_ELEMENT -> processEndElement();
                }
            }
        } catch (XMLStreamException | IOException exception) {
            throw new LoadException(exception);
        }

        return root;
    }

    private InputStream open() throws IOException {
        if (owner != null) {
            return owner.getClass().getResourceAsStream(name);
        } else {
            return path.toUri().toURL().openStream();
        }
    }

    private void processStartElement(XMLStreamReader xmlStreamReader) {
        var tag = xmlStreamReader.getLocalName();

        var type = types.get(tag);

        JComponent component;
        if (type == null) {
            if (path == null) {
                throw new UnsupportedOperationException(String.format("Invalid tag (%s).", tag));
            }

            var label = new JLabel(UIManager.getIcon("OptionPane.warningIcon"), SwingConstants.CENTER);

            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.BOTTOM);
            label.setIconTextGap(0);

            label.setText(tag);

            component = label;
        } else {
            component = suppliers.get(tag).get();
        }

        if (component instanceof JSplitPane splitPane) {
            splitPane.setLeftComponent(null);
            splitPane.setRightComponent(null);
        }

        String title = null;

        Color titleColor = null;
        Font titleFont = null;

        var titleJustification = TitledBorder.DEFAULT_JUSTIFICATION;
        var titlePosition = TitledBorder.DEFAULT_POSITION;

        Border outsideBorder = null;
        EmptyBorder insideBorder = null;

        Object constraints = null;

        String tabTitle = null;
        Icon tabIcon = null;

        var properties = map(type, BeanAdapter::getProperties);

        for (int i = 0, n = xmlStreamReader.getAttributeCount(); i < n; i++) {
            var name = xmlStreamReader.getAttributeLocalName(i);
            var value = xmlStreamReader.getAttributeValue(i);

            if (name.equals(Attribute.NAME.getName())) {
                component.setName(value);

                perform(fields.get(value), field -> {
                    field.setAccessible(true);

                    try {
                        field.set(owner, component);
                    } catch (IllegalAccessException exception) {
                        throw new UnsupportedOperationException(exception);
                    }
                });
            } else if (name.equals(Attribute.TITLE.getName())) {
                title = getText(value);
            } else if (name.equals(Attribute.TITLE_COLOR.getName())) {
                titleColor = parseColor(value);
            } else if (name.equals(Attribute.TITLE_FONT.getName())) {
                titleFont = parseFont(value);
            } else if (name.equals(Attribute.TITLE_JUSTIFICATION.getName())) {
                titleJustification = getValue(value, TitleJustification.values());
            } else if (name.equals(Attribute.TITLE_POSITION.getName())) {
                titlePosition = getValue(value, TitlePosition.values());
            } else if (name.equals(Attribute.BORDER.getName())) {
                outsideBorder = parseBorder(value);
            } else if (name.equals(Attribute.PADDING.getName())) {
                insideBorder = parsePadding(value);
            } else if (name.equals(Attribute.WIDTH.getName())) {
                component.setPreferredSize(new Dimension(Integer.parseInt(value), 0));
            } else if (name.equals(Attribute.HEIGHT.getName())) {
                component.setPreferredSize(new Dimension(0, Integer.parseInt(value)));
            } else if (name.equals(Attribute.SIZE.getName())) {
                component.setPreferredSize(parseSize(value));
            } else if (name.equals(Attribute.WEIGHT.getName())) {
                constraints = Double.parseDouble(value);
            } else if (name.equals(Attribute.LABEL.getName())) {
                constraints = getText(value);
            } else if (name.equals(Attribute.COLUMN_SPAN.getName())) {
                constraints = Integer.parseInt(value);
            } else if (name.equals(Attribute.GROUP.getName())) {
                if (type == null) {
                    continue;
                }

                if (!(component instanceof AbstractButton button)) {
                    throw new UnsupportedOperationException("Component is not a button.");
                }

                groups.computeIfAbsent(value, key -> new ButtonGroup()).add(button);
            } else if (name.equals(Attribute.TAB_TITLE.getName())) {
                tabTitle = getText(value);
            } else if (name.equals(Attribute.TAB_ICON.getName())) {
                tabIcon = getIcon(value);

                if (tabIcon instanceof FlatSVGIcon flatSVGIcon) {
                    flatSVGIcon.setColorFilter(new FlatSVGIcon.ColorFilter(new TabbedPaneColorMapper()));
                }
            } else if (name.equals(Attribute.STYLE.getName()) || name.equals(Attribute.STYLE_CLASS.getName())) {
                component.putClientProperty(String.format("FlatLaf.%s", name), value);
            } else if (name.equals(Attribute.PLACEHOLDER_TEXT.getName())) {
                component.putClientProperty(String.format("%s.%s", JTextField.class.getSimpleName(), name), getText(value));
            } else if (name.equals(Attribute.SHOW_CLEAR_BUTTON.getName())) {
                component.putClientProperty(String.format("%s.%s", JTextField.class.getSimpleName(), name), Boolean.valueOf(value));
            } else if (name.equals(Attribute.LEADING_ICON.getName()) || name.equals(Attribute.TRAILING_ICON.getName())) {
                var icon = getIcon(value);

                if (icon instanceof FlatSVGIcon flatSVGIcon) {
                    flatSVGIcon.setColorFilter(new FlatSVGIcon.ColorFilter(new TextFieldColorMapper()));
                }

                component.putClientProperty(String.format("%s.%s", JTextField.class.getSimpleName(), name), icon);
            } else {
                if (type == null) {
                    continue;
                }

                var mutator = map(properties.get(name), BeanAdapter.Property::getMutator);

                if (mutator == null) {
                    throw new UnsupportedOperationException(String.format("Invalid attribute name (%s).", name));
                }

                var propertyType = mutator.getParameterTypes()[0];

                Object argument;
                if (propertyType == Integer.TYPE || propertyType == Integer.class) {
                    if (name.equals(Attribute.HORIZONTAL_ALIGNMENT.getName())
                        || name.equals(Attribute.HORIZONTAL_TEXT_POSITION.getName())) {
                        argument = getValue(value, HorizontalAlignment.values());
                    } else if (name.equals(Attribute.VERTICAL_ALIGNMENT.getName())
                        || name.equals(Attribute.VERTICAL_TEXT_POSITION.getName())) {
                        argument = getValue(value, VerticalAlignment.values());
                    } else if (name.equals(Attribute.ORIENTATION.getName())) {
                        argument = getValue(value, Orientation.values());

                        if (component instanceof JSplitPane) {
                            argument = switch ((int)argument) {
                                case SwingConstants.HORIZONTAL -> JSplitPane.HORIZONTAL_SPLIT;
                                case SwingConstants.VERTICAL -> JSplitPane.VERTICAL_SPLIT;
                                default -> throw new UnsupportedOperationException();
                            };
                        }
                    } else if (name.equals(Attribute.FOCUS_LOST_BEHAVIOR.getName())) {
                        argument = getValue(value, FocusLostBehavior.values());
                    } else if (name.equals(Attribute.HORIZONTAL_SCROLL_BAR_POLICY.getName())) {
                        argument = getValue(value, HorizontalScrollBarPolicy.values());
                    } else if (name.equals(Attribute.VERTICAL_SCROLL_BAR_POLICY.getName())) {
                        argument = getValue(value, VerticalScrollBarPolicy.values());
                    } else if (name.equals(Attribute.SELECTION_MODE.getName())) {
                        argument = getValue(value, ListSelectionMode.values());
                    } else if (name.equals(Attribute.LAYOUT_ORIENTATION.getName())) {
                        argument = getValue(value, LayoutOrientation.values());
                    } else if (name.equals(Attribute.AUTO_RESIZE_MODE.getName())) {
                        argument = getValue(value, AutoResizeMode.values());
                    } else if (name.equals(Attribute.TAB_PLACEMENT.getName())) {
                        argument = getValue(value, TabPlacement.values());
                    } else if (name.equals(Attribute.TAB_LAYOUT_POLICY.getName())) {
                        argument = getValue(value, TabLayoutPolicy.values());
                    } else {
                        argument = Integer.valueOf(value);
                    }
                } else if (propertyType == String.class) {
                    argument = getText(value);
                } else if (propertyType == Color.class) {
                    argument = parseColor(value);
                } else if (propertyType == Font.class) {
                    argument = parseFont(value);
                } else if (propertyType == Icon.class) {
                    var icon = getIcon(value);

                    if (icon instanceof FlatSVGIcon flatSVGIcon) {
                        Function<Color, Color> mapper;
                        if (component instanceof JLabel) {
                            mapper = new LabelColorMapper();
                        } else if (component instanceof JButton button) {
                            mapper = new ButtonColorMapper(button);
                        } else if (component instanceof JToggleButton toggleButton) {
                            mapper = new ToggleButtonColorMapper(toggleButton);
                        } else if (component instanceof JMenuItem menuItem) {
                            mapper = new MenuItemColorMapper(menuItem);
                        } else {
                            mapper = null;
                        }

                        if (mapper != null) {
                            flatSVGIcon.setColorFilter(new FlatSVGIcon.ColorFilter(mapper));
                        }
                    }

                    argument = icon;
                } else if (propertyType == Image.class) {
                    argument = getImage(value);
                } else if (propertyType == KeyStroke.class) {
                    var keyCode = keyCodes.get(value);

                    if (keyCode == null) {
                        throw new IllegalArgumentException("Invalid key code.");
                    }

                    var modifiers = 0;

                    if (component instanceof JMenuItem) {
                        modifiers |= Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
                    }

                    argument = KeyStroke.getKeyStroke(keyCode, modifiers);
                } else {
                    if (Enum.class.isAssignableFrom(propertyType)) {
                        value = value.toUpperCase().replace('-', '_');
                    }

                    argument = BeanAdapter.coerce(value, propertyType);
                }

                try {
                    mutator.invoke(component, argument);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    throw new UnsupportedOperationException(exception);
                }
            }
        }

        Border border;
        if (title != null) {
            var titledBorder = new TitledBorder(outsideBorder, title);

            titledBorder.setTitleColor(titleColor);
            titledBorder.setTitleFont(titleFont);

            if (titleJustification != TitledBorder.DEFAULT_JUSTIFICATION) {
                titledBorder.setTitleJustification(titleJustification);
            }

            if (titlePosition != TitledBorder.DEFAULT_POSITION) {
                titledBorder.setTitlePosition(titlePosition);
            }

            border = new CompoundBorder(titledBorder, insideBorder);
        } else if (outsideBorder != null) {
            border = new CompoundBorder(outsideBorder, insideBorder);
        } else {
            border = insideBorder;
        }

        if (border != null) {
            component.setBorder(border);
        }

        var parent = components.peek();

        if (parent != null) {
            if (parent instanceof LayoutPanel) {
                parent.add(component, constraints);
            } else if (parent instanceof JScrollPane scrollPane) {
                scrollPane.setViewportView(component);
            } else if (parent instanceof JSplitPane splitPane) {
                if (splitPane.getLeftComponent() == null) {
                    splitPane.setLeftComponent(component);
                } else if (splitPane.getRightComponent() == null) {
                    splitPane.setRightComponent(component);
                } else {
                    throw new UnsupportedOperationException("Unexpected split pane content.");
                }
            } else if (parent instanceof JTabbedPane tabbedPane) {
                tabbedPane.addTab(tabTitle, tabIcon, component);
            } else if (parent instanceof JToolBar
                || parent instanceof JMenuBar
                || parent instanceof JMenu
                || parent instanceof MenuButton) {
                parent.add(component);
            } else {
                throw new UnsupportedOperationException("Invalid parent type.");
            }
        }

        components.push(component);
    }

    private String getText(String value) {
        if (resourceBundle == null) {
            return value;
        } else {
            var key = value.trim();

            if (key.isEmpty()) {
                throw new IllegalArgumentException("Invalid resource key.");
            }

            return resourceBundle.getString(key);
        }
    }

    private Icon getIcon(String value) {
        var components = value.split(";");

        var icon = icons.computeIfAbsent(components[0].trim(), key -> new FlatSVGIcon(getURL(key)));

        if (components.length > 1) {
            var size = parseSize(components[1]);

            icon = ((FlatSVGIcon)icon).derive(size.width, size.height);
        }

        return icon;
    }

    private Image getImage(String value) {
        return images.computeIfAbsent(value.trim(), key -> {
            try {
                return ImageIO.read(getURL(key));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    private URL getURL(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Invalid resource name.");
        }

        if (owner != null) {
            return owner.getClass().getResource(name);
        } else {
            var uri = path.resolveSibling(name).toUri();

            try {
                return uri.toURL();
            } catch (MalformedURLException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private void processEndElement() {
        root = components.pop();
    }

    /**
     * Deserializes a component hierarchy from a markup document.
     *
     * @param owner
     * The document's owner.
     *
     * @param name
     * The name of the document, relative to the owner's type.
     *
     * @return
     * The deserialized component hierarchy.
     */
    public static JComponent load(Object owner, String name) {
        return load(owner, name, null);
    }

    /**
     * Deserializes a component hierarchy from a markup document.
     *
     * @param owner
     * The document's owner.
     *
     * @param name
     * The name of the document, relative to the owner's type.
     *
     * @param resourceBundle
     * The resource bundle, or {@code null} for no resource bundle.
     *
     * @return
     * The deserialized component hierarchy.
     */
    public static JComponent load(Object owner, String name, ResourceBundle resourceBundle) {
        if (owner == null || name == null) {
            throw new IllegalArgumentException();
        }

        var uiLoader = new UILoader(owner, name, resourceBundle);

        return uiLoader.load();
    }

    /**
     * Deserializes a component hierarchy from a markup document.
     *
     * @param path
     * The document's path.
     *
     * @return
     * The deserialized component hierarchy.
     */
    public static JComponent load(Path path) {
        if (path == null) {
            throw new IllegalArgumentException();
        }

        var uiLoader = new UILoader(path);

        return uiLoader.load();
    }

    /**
     * Returns the bound markup tags.
     *
     * @return
     * The bound markup tags.
     */
    public static Iterable<String> getTags() {
        return types.keySet();
    }

    /**
     * Retrieves a bound component type.
     *
     * @param tag
     * The markup tag.
     *
     * @return
     * The component type, or {@code null} if the tag is not bound.
     */
    public static Class<? extends JComponent> getType(String tag) {
        return types.get(tag);
    }

    /**
     * Associates a markup tag with a component type.
     *
     * @param <T>
     * The component type.
     *
     * @param tag
     * The markup tag.
     *
     * @param type
     * The component type.
     *
     * @param supplier
     * The component supplier.
     */
    public static <T extends JComponent> void bind(String tag, Class<T> type, Supplier<T> supplier) {
        if (tag == null || type == null || supplier == null) {
            throw new IllegalArgumentException();
        }

        types.put(tag, type);
        suppliers.put(tag, supplier);
    }

    /**
     * Applies multiple bindings.
     *
     * @param bindings
     * The bindings to apply.
     *
     * @param classLoader
     * The class loader that will be used to resolve the bindings.
     */
    @SuppressWarnings("unchecked")
    public static void bind(Properties bindings, ClassLoader classLoader) throws ClassNotFoundException {
        if (bindings == null || classLoader == null) {
            throw new IllegalArgumentException();
        }

        for (var entry : bindings.entrySet()) {
            var tag = (String)entry.getKey();
            var typeName = (String)entry.getValue();

            var type = (Class<?>)classLoader.loadClass(typeName);

            var constructors = type.getConstructors();

            if (constructors.length == 0) {
                throw new UnsupportedOperationException(String.format("%s cannot be instantiated.", typeName));
            }

            Arrays.sort(constructors, Comparator.comparing(Constructor::getParameterCount));

            var constructor = constructors[0];

            bind(tag, (Class<JComponent>)type, () -> {
                try {
                    return (JComponent)constructor.newInstance(new Object[constructor.getParameterCount()]);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
    }

    /**
     * Retrieves a named color.
     *
     * @param name
     * The color name.
     *
     * @return
     * The named color, or {@code null} if the color is not defined.
     */
    public static Color getColor(String name) {
        return colors.get(name);
    }

    /**
     * Associates a name with a color.
     *
     * @param name
     * The color name.
     *
     * @param color
     * The color value.
     */
    public static void define(String name, Color color) {
        if (name == null || color == null) {
            throw new IllegalArgumentException();
        }

        colors.put(name, color);
    }

    /**
     * Retrieves a named font.
     *
     * @param name
     * The font name.
     *
     * @return
     * The named font, or {@code null} if the font is not defined.
     */
    public static Font getFont(String name) {
        return fonts.get(name);
    }

    /**
     * Associates a name with a font.
     *
     * @param name
     * The font name.
     *
     * @param font
     * The font value.
     */
    public static void define(String name, Font font) {
        if (name == null || font == null) {
            throw new IllegalArgumentException();
        }

        fonts.put(name, font);
    }

    /**
     * Creates a rounded line border.
     *
     * @param color
     * The border color.
     *
     * @param stroke
     * The border stroke.
     *
     * @param cornerRadius
     * The corner radius.
     *
     * @return
     * A rounded line border.
     */
    public static Border createRoundedLineBorder(Color color, BasicStroke stroke, int cornerRadius) {
        return createRoundedLineBorder(color, stroke, cornerRadius, new Insets(0, 0, 0, 0));
    }

    /**
     * Creates a rounded line border.
     *
     * @param color
     * The border color.
     *
     * @param stroke
     * The border stroke.
     *
     * @param cornerRadius
     * The corner radius.
     *
     * @param padding
     * The padding insets.
     *
     * @return
     * A rounded line border.
     */
    public static Border createRoundedLineBorder(Color color, BasicStroke stroke, int cornerRadius, Insets padding) {
        if (color == null || stroke == null || cornerRadius < 0 || padding == null) {
            throw new IllegalArgumentException();
        }

        var outsideBorder = new RoundedLineBorder(color, stroke, cornerRadius);
        var insideBorder = new EmptyBorder(padding.top, padding.left, padding.bottom, padding.right);

        return new CompoundBorder(outsideBorder, insideBorder);
    }

    private static Border parseBorder(String value) {
        var border = value.trim();

        if (border.equals("none")) {
            return new EmptyBorder(0, 0, 0, 0);
        } else {
            var components = border.split(",");

            var color = parseColor(components[0].trim());

            if (components.length == 1) {
                return new LineBorder(color);
            } else {
                var thickness = Integer.parseInt(components[1].trim());

                if (components.length == 2) {
                    return new LineBorder(color, thickness);
                } else {
                    var dashArray = switch(components[2].trim()) {
                        case "solid" -> null;
                        case "dashed" -> new float[]{thickness * 2.5f, thickness * 5.0f};
                        case "dotted" -> new float[]{0.0f, thickness * 2.5f};
                        default -> throw new IllegalArgumentException("Invalid border style.");
                    };

                    int cornerRadius;
                    if (components.length == 3) {
                        cornerRadius = 0;
                    } else if (components.length == 4) {
                        cornerRadius = Integer.parseInt(components[3].trim());

                        if (cornerRadius < 0) {
                            throw new IllegalArgumentException("Invalid corner radius.");
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid border.");
                    }

                    return new RoundedLineBorder(color, new BasicStroke(thickness,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        0.0f, dashArray, 0.0f), cornerRadius);
                }
            }
        }
    }

    private static EmptyBorder parsePadding(String value) {
        var components = value.split(",");

        if (components.length == 1) {
            var padding = Integer.parseInt(components[0].trim());

            return new EmptyBorder(padding, padding, padding, padding);
        } else if (components.length == 4) {
            var top = Integer.parseInt(components[0].trim());
            var left = Integer.parseInt(components[1].trim());
            var bottom = Integer.parseInt(components[2].trim());
            var right = Integer.parseInt(components[3].trim());

            return new EmptyBorder(top, left, bottom, right);
        } else {
            throw new IllegalArgumentException("Invalid padding.");
        }
    }

    private static Dimension parseSize(String value) {
        var components = value.split(",");

        if (components.length == 1) {
            var size = Integer.parseInt(components[0].trim());

            return new Dimension(size, size);
        } else if (components.length == 2) {
            var width = Integer.parseInt(components[0].trim());
            var height = Integer.parseInt(components[1].trim());

            return new Dimension(width, height);
        } else {
            throw new IllegalArgumentException("Invalid size.");
        }
    }

    private static Color parseColor(String value) {
        var components = value.split(";");

        var name = components[0].trim();

        var color = coalesce(UIManager.getColor(name), () -> coalesce(colors.get(name), () -> Color.decode(name)));

        if (components.length == 1) {
            return color;
        } else {
            var alpha = (int)(Float.parseFloat(components[1].trim()) * 255);

            return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        }
    }

    private static Font parseFont(String value) {
        var name = value.trim();

        return coalesce(UIManager.getFont(name), () -> coalesce(fonts.get(name), () -> Font.decode(name)));
    }

    private static int getValue(String key, ConstantAdapter[] values) {
        for (var i = 0; i < values.length; i++) {
            var value = values[i];

            if (key.equals(value.getKey())) {
                return value.getValue();
            }
        }

        throw new IllegalArgumentException("Invalid key.");
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<?> & ConstantAdapter> E getValue(int constant, ConstantAdapter[] values) {
        for (var i = 0; i < values.length; i++) {
            var value = values[i];

            if (constant == value.getValue()) {
                return (E)value;
            }
        }

        throw new IllegalArgumentException("Invalid constant.");
    }
}
