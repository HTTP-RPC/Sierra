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
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
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
         * Group attribute.
         */
        GROUP("group", String.class),

        /**
         * Border attribute.
         */
        BORDER("border", String.class),

        /**
         * Padding attribute.
         */
        PADDING("padding", String.class),

        /**
         * Title attribute.
         */
        TITLE("title", String.class),

        /**
         * Weight attribute.
         */
        WEIGHT("weight", Double.class),

        /**
         * Size attribute.
         */
        SIZE("size", String.class),

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
         * Horizontal alignment attribute.
         */
        HORIZONTAL_TEXT_POSITION("horizontalTextPosition", HorizontalAlignment.class),

        /**
         * Vertical alignment attribute.
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
         * Layout orientation attribute.
         */
        LAYOUT_ORIENTATION("layoutOrientation", LayoutOrientation.class),

        /**
         * Selection mode attribute.
         */
        SELECTION_MODE("selectionMode", SelectionMode.class),

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
     * Orientation options.
     */
    public enum Orientation implements ConstantAdapter {
        /**
         * Horizontal orientation.
         */
        HORIZONTAL("horizontal", SwingConstants.HORIZONTAL),

        /**
         * Vertical orientation.
         */
        VERTICAL("vertical", SwingConstants.VERTICAL);

        private final String key;
        private final int value;

        Orientation(String key, int value) {
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
     * Selection mode options.
     */
    public enum SelectionMode implements ConstantAdapter {
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

        SelectionMode(String key, int value) {
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
        bind("color-chooser", JColorChooser.class, JColorChooser::new);
        bind("separator", JSeparator.class, JSeparator::new);
        bind("scroll-pane", JScrollPane.class, JScrollPane::new);
        bind("list", JList.class, JList::new);
        bind("text-area", JTextArea.class, JTextArea::new);
        bind("table", JTable.class, JTable::new);
        bind("tree", JTree.class, JTree::new);
        bind("split-pane", JSplitPane.class, JSplitPane::new);
        bind("tabbed-pane", JTabbedPane.class, JTabbedPane::new);
        bind("tool-bar", JToolBar.class, JToolBar::new);
        bind("tool-bar-separator", JToolBar.Separator.class, JToolBar.Separator::new);
        bind("menu-bar", JMenuBar.class, JMenuBar::new);
        bind("menu", JMenu.class, JMenu::new);
        bind("menu-item", JMenuItem.class, JMenuItem::new);
        bind("check-box-menu-item", JCheckBoxMenuItem.class, JCheckBoxMenuItem::new);
        bind("radio-button-menu-item", JRadioButtonMenuItem.class, JRadioButtonMenuItem::new);
        bind("popup-menu-separator", JPopupMenu.Separator.class, JPopupMenu.Separator::new);

        bind("row-panel", RowPanel.class, RowPanel::new);
        bind("column-panel", ColumnPanel.class, ColumnPanel::new);
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
    }

    static {
        define("medium-violet-red", new Color(0xc71585));
        define("deep-pink", new Color(0xff1493));
        define("pale-violet-red", new Color(0xdb7093));
        define("hot-pink", new Color(0xff69b4));
        define("light-pink", new Color(0xffb6c1));
        define("pink", new Color(0xffc0cb));

        define("dark-red", new Color(0x8b0000));
        define("red", new Color(0xff0000));
        define("firebrick", new Color(0xb22222));
        define("crimson", new Color(0xdc143c));
        define("indian-red", new Color(0xcd5c5c));
        define("light-coral", new Color(0xf08080));
        define("salmon", new Color(0xfa8072));
        define("dark-salmon", new Color(0xe9967a));
        define("light-salmon", new Color(0xffa07a));

        define("orange-red", new Color(0xff4500));
        define("tomato", new Color(0xff6347));
        define("dark-orange", new Color(0xff8c00));
        define("coral", new Color(0xff7f50));
        define("orange", new Color(0xffa500));

        define("dark-khaki", new Color(0xbdb76b));
        define("gold", new Color(0xffd700));
        define("khaki", new Color(0xf0e68c));
        define("peach-puff", new Color(0xffdab9));
        define("yellow", new Color(0xffff00));
        define("pale-goldenrod", new Color(0xeee8aa));
        define("moccasin", new Color(0xffe4b5));
        define("papaya-whip", new Color(0xffefd5));
        define("light-goldenrod-yellow", new Color(0xfafad2));
        define("lemon-chiffon", new Color(0xfffacd));
        define("light-yellow", new Color(0xffffe0));

        define("maroon", new Color(0x800000));
        define("brown", new Color(0xa52a2a));
        define("saddle-brown", new Color(0x8b4513));
        define("sienna", new Color(0xa0522d));
        define("chocolate", new Color(0xd2691e));
        define("dark-goldenrod", new Color(0xb8860b));
        define("peru", new Color(0xcd853f));
        define("rosy-brown", new Color(0xbc8f8f));
        define("goldenrod", new Color(0xdaa520));
        define("sandy-brown", new Color(0xf4a460));
        define("tan", new Color(0xd2b48c));
        define("burlywood", new Color(0xdeb887));
        define("wheat", new Color(0xf5deb3));
        define("navajo-white", new Color(0xffdead));
        define("bisque", new Color(0xffe4c4));
        define("blanched-almond", new Color(0xffebcd));
        define("cornsilk", new Color(0xfff8dc));

        define("indigo", new Color(0x4b0082));
        define("purple", new Color(0x800080));
        define("dark-magenta", new Color(0x8b008b));
        define("dark-violet", new Color(0x9400d3));
        define("dark-slate-blue", new Color(0x483d8b));
        define("blue-violet", new Color(0x8a2be2));
        define("dark-orchid", new Color(0x9932cc));
        define("fuchsia", new Color(0xff00ff));
        define("magenta", new Color(0xff00ff));
        define("slate-blue", new Color(0x6a5acd));
        define("medium-slate-blue", new Color(0x7b68ee));
        define("medium-orchid", new Color(0xba55d3));
        define("medium-purple", new Color(0x9370db));
        define("orchid", new Color(0xda70d6));
        define("violet", new Color(0xee82ee));
        define("plum", new Color(0xdda0dd));
        define("thistle", new Color(0xd8bfd8));
        define("lavender", new Color(0xe6e6fa));

        define("midnight-blue", new Color(0x191970));
        define("navy", new Color(0x000080));
        define("dark-blue", new Color(0x00008b));
        define("medium-blue", new Color(0x0000cd));
        define("blue", new Color(0x0000ff));
        define("royal-blue", new Color(0x4169e1));
        define("steel-blue", new Color(0x4682b4));
        define("dodger-blue", new Color(0x1e90ff));
        define("deep-sky-blue", new Color(0x00bfff));
        define("cornflower-blue", new Color(0x6495ed));
        define("skyblue", new Color(0x87ceeb));
        define("light-sky-blue", new Color(0x87cefa));
        define("light-steel-blue", new Color(0xb0c4de));
        define("light-blue", new Color(0xadd8e6));
        define("powder-blue", new Color(0xb0e0e6));

        define("teal", new Color(0x008080));
        define("dark-cyan", new Color(0x008b8b));
        define("light-sea-green", new Color(0x20b2aa));
        define("cadet-blue", new Color(0x5f9ea0));
        define("dark-turquoise", new Color(0x00ced1));
        define("medium-turquoise", new Color(0x48d1cc));
        define("turquoise", new Color(0x40e0d0));
        define("aqua", new Color(0x00ffff));
        define("cyan", new Color(0x00ffff));
        define("aquamarine", new Color(0x7fffd4));
        define("pale-turquoise", new Color(0xafeeee));
        define("light-cyan", new Color(0xe0ffff));

        define("dark-green", new Color(0x006400));
        define("green", new Color(0x008000));
        define("dark-olive-green", new Color(0x556b2f));
        define("forest-green", new Color(0x228b22));
        define("sea-green", new Color(0x2e8b57));
        define("olive", new Color(0x808000));
        define("olive-drab", new Color(0x6b8e23));
        define("medium-sea-green", new Color(0x3cb371));
        define("lime-green", new Color(0x32cd32));
        define("lime", new Color(0x00ff00));
        define("spring-green", new Color(0x00ff7f));
        define("medium-spring-green", new Color(0x00fa9a));
        define("dark-sea-green", new Color(0x8fbc8f));
        define("medium-aquamarine", new Color(0x66cdaa));
        define("yellow-green", new Color(0x9acd32));
        define("lawn-green", new Color(0x7cfc00));
        define("chartreuse", new Color(0x7fff00));
        define("light-green", new Color(0x90ee90));
        define("green-yellow", new Color(0xadff2f));
        define("pale-green", new Color(0x98fb98));

        define("misty-rose", new Color(0xffe4e1));
        define("antique-white", new Color(0xfaebd7));
        define("linen", new Color(0xfaf0e6));
        define("beige", new Color(0xf5f5dc));
        define("white-smoke", new Color(0xf5f5f5));
        define("lavender-blush", new Color(0xfff0f5));
        define("old-lace", new Color(0xfdf5e6));
        define("alice-blue", new Color(0xf0f8ff));
        define("seashell", new Color(0xfff5ee));
        define("ghost-white", new Color(0xf8f8ff));
        define("honeydew", new Color(0xf0fff0));
        define("floral-white", new Color(0xfffaf0));
        define("azure", new Color(0xf0ffff));
        define("mint-cream", new Color(0xf5fffa));
        define("snow", new Color(0xfffafa));
        define("ivory", new Color(0xfffff0));
        define("white", new Color(0xffffff));

        define("black", new Color(0x000000));
        define("dark-slate-gray", new Color(0x2f4f4f));
        define("dim-gray", new Color(0x696969));
        define("slate-gray", new Color(0x708090));
        define("gray", new Color(0x808080));
        define("light-slate-gray", new Color(0x778899));
        define("dark-gray", new Color(0xa9a9a9));
        define("silver", new Color(0xc0c0c0));
        define("light-gray", new Color(0xd3d3d3));
        define("gainsboro", new Color(0xdcdcdc));
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

        if (type == null) {
            throw new UnsupportedOperationException(String.format("Invalid tag (%s).", tag));
        }

        var properties = BeanAdapter.getProperties(type);

        var component = suppliers.get(tag).get();

        if (component == null) {
            return;
        }

        if (component instanceof JSplitPane splitPane) {
            splitPane.setLeftComponent(null);
            splitPane.setRightComponent(null);
        }

        LineBorder lineBorder = null;
        EmptyBorder emptyBorder = null;

        String title = null;

        Object constraints = null;

        String tabTitle = null;
        Icon tabIcon = null;

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
            } else if (name.equals(Attribute.GROUP.getName())) {
                if (!(component instanceof AbstractButton button)) {
                    throw new UnsupportedOperationException("Component is not a button.");
                }

                groups.computeIfAbsent(value, key -> new ButtonGroup()).add(button);
            } else if (name.equals(Attribute.BORDER.getName())) {
                lineBorder = parseBorder(value);
            } else if (name.equals(Attribute.PADDING.getName())) {
                emptyBorder = parsePadding(value);
            } else if (name.equals(Attribute.TITLE.getName())) {
                title = getText(value);
            } else if (name.equals(Attribute.WEIGHT.getName())) {
                var weight = Double.parseDouble(value);

                if (weight <= 0.0) {
                    throw new IllegalArgumentException("Invalid weight.");
                }

                constraints = weight;
            } else if (name.equals(Attribute.SIZE.getName())) {
                component.setPreferredSize(parseSize(value));
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
                    } else if (name.equals(Attribute.LAYOUT_ORIENTATION.getName())) {
                        argument = getValue(value, LayoutOrientation.values());
                    } else if (name.equals(Attribute.SELECTION_MODE.getName())) {
                        argument = getValue(value, SelectionMode.values());
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

        if (lineBorder != null || emptyBorder != null) {
            component.setBorder(new CompoundBorder(lineBorder, emptyBorder));
        }

        if (title != null) {
            component.setBorder(new TitledBorder(title));
        }

        var parent = components.peek();

        if (parent != null) {
            if (parent instanceof JPanel) {
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

    private int getValue(String key, ConstantAdapter[] values) {
        for (var i = 0; i < values.length; i++) {
            var value = values[i];

            if (key.equals(value.getKey())) {
                return value.getValue();
            }
        }

        throw new IllegalArgumentException("Invalid key.");
    }

    private String getText(String value) {
        if (resourceBundle == null) {
            return value;
        } else {
            return resourceBundle.getString(value.trim());
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
     * @param tag
     * The markup tag.
     *
     * @param type
     * The component type.
     *
     * @param supplier
     * The component supplier.
     */
    public static <C extends JComponent> void bind(String tag, Class<C> type, Supplier<C> supplier) {
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

    private static LineBorder parseBorder(String value) {
        var components = value.split(",");

        var color = parseColor(components[0].trim());

        if (components.length == 1) {
            return new LineBorder(color);
        } else {
            var thickness = Integer.parseInt(components[1].trim());

            if (components.length == 2) {
                return new LineBorder(color, thickness);
            } else {
                throw new IllegalArgumentException("Invalid border.");
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

    private Dimension parseSize(String value) {
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
        return coalesce(colors.get(value), () -> coalesce(UIManager.getColor(value), () -> Color.decode(value)));
    }

    private static Font parseFont(String value) {
        return coalesce(fonts.get(value), () -> coalesce(UIManager.getFont(value), () -> Font.decode(value)));
    }
}
