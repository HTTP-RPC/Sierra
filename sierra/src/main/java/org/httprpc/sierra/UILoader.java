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
import org.httprpc.kilo.io.Encoder;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static org.httprpc.kilo.util.Optionals.*;

/**
 * Provides support for deserializing a component hierarchy from markup.
 */
public class UILoader {
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

    private static class DTDEncoder extends Encoder<Void> {
        List<Class<?>> typeList;
        Map<Class<?>, String> tags;

        static final String CDATA = "CDATA";

        DTDEncoder(List<Class<?>> typeList, Map<Class<?>, String> tags) {
            this.typeList = typeList;
            this.tags = tags;
        }

        @Override
        public void write(Void value, Writer writer) throws IOException {
            startEntityDeclaration(UILoader.class, null, writer);

            appendAttributeDeclaration(NAME, CDATA, writer);
            appendAttributeDeclaration(GROUP, CDATA, writer);

            appendAttributeDeclaration(BORDER, CDATA, writer);
            appendAttributeDeclaration(PADDING, CDATA, writer);
            appendAttributeDeclaration(WEIGHT, CDATA, writer);
            appendAttributeDeclaration(SIZE, CDATA, writer);

            appendAttributeDeclaration(STYLE, CDATA, writer);
            appendAttributeDeclaration(STYLE_CLASS, CDATA, writer);

            endEntityDeclaration(writer);

            for (var type : typeList) {
                startEntityDeclaration(type, type.getSuperclass(), writer);

                for (var entry : BeanAdapter.getProperties(type).entrySet()) {
                    var property = entry.getValue();
                    var mutator = property.getMutator();

                    if (mutator == null || mutator.getDeclaringClass() != type) {
                        continue;
                    }

                    var attributeName = entry.getKey();

                    var propertyType = mutator.getParameterTypes()[0];

                    String attributeType;
                    if (propertyType == Integer.TYPE || propertyType == Integer.class) {
                        if (attributeName.equals(HORIZONTAL_ALIGNMENT)) {
                            attributeType = String.format("(%s|%s|%s|%s|%s)", LEFT, CENTER, RIGHT, LEADING, TRAILING);
                        } else if (attributeName.equals(VERTICAL_ALIGNMENT)) {
                            attributeType = String.format("(%s|%s|%s)", TOP, CENTER, BOTTOM);
                        } else if (attributeName.equals(ORIENTATION)) {
                            attributeType = String.format("(%s|%s)", HORIZONTAL, VERTICAL);
                        } else if (attributeName.equals(FOCUS_LOST_BEHAVIOR)) {
                            attributeType = String.format("(%s|%s|%s|%s)", COMMIT, COMMIT_OR_REVERT, REVERT, PERSIST);
                        } else {
                            attributeType = CDATA;
                        }
                    } else if (propertyType == Boolean.TYPE || propertyType == Boolean.class) {
                        attributeType = String.format("(%b|%b)", true, false);
                    } else if (Enum.class.isAssignableFrom(propertyType)) {
                        var attributeTypeBuilder = new StringBuilder();

                        attributeTypeBuilder.append('(');

                        var fields = propertyType.getDeclaredFields();

                        var i = 0;

                        for (var j = 0; j < fields.length; j++) {
                            var field = fields[j];

                            if (!field.isEnumConstant()) {
                                continue;
                            }

                            if (i > 0) {
                                attributeTypeBuilder.append('|');
                            }

                            attributeTypeBuilder.append(field.getName().toLowerCase().replace('_', '-'));

                            i++;
                        }

                        attributeTypeBuilder.append(')');

                        attributeType = attributeTypeBuilder.toString();
                    } else if (propertyType.isPrimitive()
                        || Number.class.isAssignableFrom(propertyType)
                        || propertyType == String.class
                        || propertyType == Color.class
                        || propertyType == Font.class
                        || propertyType == Icon.class
                        || propertyType == Image.class) {
                        attributeType = CDATA;
                    } else {
                        attributeType = null;
                    }

                    if (attributeType != null) {
                        appendAttributeDeclaration(attributeName, attributeType, writer);
                    }
                }

                if (type == JTextField.class) {
                    appendAttributeDeclaration(PLACEHOLDER_TEXT, CDATA, writer);
                    appendAttributeDeclaration(SHOW_CLEAR_BUTTON, String.format("(%b|%b)", true, false), writer);
                    appendAttributeDeclaration(LEADING_ICON, CDATA, writer);
                    appendAttributeDeclaration(TRAILING_ICON, CDATA, writer);
                }

                endEntityDeclaration(writer);

                var tag = tags.get(type);

                if (tag != null) {
                    declareElement(tag, type, writer);
                    declareAttributeList(tag, type, writer);
                }
            }

            writer.flush();
        }

        void startEntityDeclaration(Class<?> type, Class<?> baseType, Writer writer) throws IOException {
            writer.append("<!ENTITY % ");
            writer.append(type.getSimpleName());
            writer.append(" \"");

            if (baseType != null) {
                writer.append("%");

                if (baseType == Object.class) {
                    writer.append(UILoader.class.getSimpleName());
                } else {
                    writer.append(baseType.getSimpleName());
                }

                writer.append("; ");
            }
        }

        void appendAttributeDeclaration(String name, String type, Writer writer) throws IOException {
            writer.append(name);
            writer.append(" ");
            writer.append(type);
            writer.append(" ");
        }

        void endEntityDeclaration(Writer writer) throws IOException {
            writer.append("\">\n");
        }

        void declareElement(String tag, Class<?> type, Writer writer) throws IOException {
            writer.append("<!ELEMENT ");
            writer.append(tag);
            writer.append(" ");

            if (type == JScrollPane.class || JPanel.class.isAssignableFrom(type)) {
                writer.append("(ANY)");
            } else {
                writer.append("EMPTY");
            }

            writer.append(">\n");
        }

        void declareAttributeList(String tag, Class<?> type, Writer writer) throws IOException {
            writer.append("<!ATTLIST ");
            writer.append(tag);
            writer.append(" %");
            writer.append(type.getSimpleName());
            writer.append(";>\n");
        }
    }

    private Object owner;
    private String name;
    private ResourceBundle resourceBundle;

    private Map<String, Field> fields = new HashMap<>();
    private Map<String, ButtonGroup> groups = new HashMap<>();

    private Deque<JComponent> components = new LinkedList<>();

    private JComponent root = null;

    private static final String NAME = "name";
    private static final String GROUP = "group";

    private static final String BORDER = "border";
    private static final String PADDING = "padding";
    private static final String WEIGHT = "weight";
    private static final String SIZE = "size";

    private static final String STYLE = "style";
    private static final String STYLE_CLASS = "styleClass";

    private static final String PLACEHOLDER_TEXT = "placeholderText";
    private static final String SHOW_CLEAR_BUTTON = "showClearButton";
    private static final String LEADING_ICON = "leadingIcon";
    private static final String TRAILING_ICON = "trailingIcon";

    private static final String HORIZONTAL_ALIGNMENT = "horizontalAlignment";
    private static final String VERTICAL_ALIGNMENT = "verticalAlignment";
    private static final String ORIENTATION = "orientation";
    private static final String FOCUS_LOST_BEHAVIOR = "focusLostBehavior";

    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String LEADING = "leading";
    private static final String TRAILING = "trailing";

    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";

    private static final String CENTER = "center";

    private static final String HORIZONTAL = "horizontal";
    private static final String VERTICAL = "vertical";

    private static final String COMMIT = "commit";
    private static final String COMMIT_OR_REVERT = "commit-or-revert";
    private static final String REVERT = "revert";
    private static final String PERSIST = "persist";


    private static Map<String, Class<? extends JComponent>> types = new HashMap<>();
    private static Map<String, Supplier<? extends JComponent>> suppliers = new HashMap<>();

    private static Map<String, Color> colors = new HashMap<>();

    private static Map<String, Font> fonts = new HashMap<>();

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
        bind("table", JTable.class, JTable::new);
        bind("tree", JTree.class, JTree::new);

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
        define("white", new Color(0xffffff));
        define("silver", new Color(0xc0c0c0));
        define("gray", new Color(0x808080));
        define("black", new Color(0x000000));
        define("red", new Color(0xff0000));
        define("maroon", new Color(0x800000));
        define("yellow", new Color(0xffff00));
        define("olive", new Color(0x808000));
        define("lime", new Color(0x00ff00));
        define("green", new Color(0x008000));
        define("aqua", new Color(0x00ffff));
        define("teal", new Color(0x008080));
        define("blue", new Color(0x0000ff));
        define("navy", new Color(0x000080));
        define("fuschia", new Color(0xff00ff));
        define("purple", new Color(0x800080));
    }

    private UILoader(Object owner, String name, ResourceBundle resourceBundle) {
        this.owner = owner;
        this.name = name;
        this.resourceBundle = resourceBundle;
    }

    private JComponent load() {
        var type = owner.getClass();

        var fields = type.getDeclaredFields();

        for (var i = 0; i < fields.length; i++) {
            var field = fields[i];

            if (JComponent.class.isAssignableFrom(field.getType())) {
                this.fields.put(field.getName(), field);
            }
        }

        var xmlInputFactory = XMLInputFactory.newInstance();

        xmlInputFactory.setProperty("javax.xml.stream.isNamespaceAware", false);
        xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        xmlInputFactory.setProperty("javax.xml.stream.supportDTD", false);

        try (var inputStream = type.getResourceAsStream(name)) {
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

        LineBorder lineBorder = null;
        EmptyBorder emptyBorder = null;

        Object constraints = null;

        for (int i = 0, n = xmlStreamReader.getAttributeCount(); i < n; i++) {
            var name = xmlStreamReader.getAttributeLocalName(i);
            var value = xmlStreamReader.getAttributeValue(i);

            if (name.equals(NAME)) {
                var field = fields.get(value);

                if (field == null) {
                    throw new UnsupportedOperationException(String.format("Invalid field name (%s).", value));
                }

                field.setAccessible(true);

                try {
                    if (field.get(owner) != null) {
                        throw new UnsupportedOperationException(String.format("Field is already assigned (%s).", value));
                    }

                    field.set(owner, component);
                } catch (IllegalAccessException exception) {
                    throw new UnsupportedOperationException(exception);
                }
            } else if (name.equals(GROUP)) {
                if (!(component instanceof AbstractButton button)) {
                    throw new UnsupportedOperationException("Component is not a button.");
                }

                groups.computeIfAbsent(value, key -> new ButtonGroup()).add(button);
            } else if (name.equals(BORDER)) {
                lineBorder = parseBorder(value);
            } else if (name.equals(PADDING)) {
                emptyBorder = parsePadding(value);
            } else if (name.equals(WEIGHT)) {
                var weight = Double.parseDouble(value);

                if (weight <= 0.0) {
                    throw new IllegalArgumentException("Invalid weight.");
                }

                constraints = weight;
            } else if (name.equals(SIZE)) {
                component.setPreferredSize(parseSize(value));
            } else if (name.equals(STYLE) || name.equals(STYLE_CLASS)) {
                component.putClientProperty(String.format("FlatLaf.%s", name), value);
            } else if (name.equals(PLACEHOLDER_TEXT)) {
                component.putClientProperty(String.format("%s.%s", JTextField.class.getSimpleName(), name), getText(value));
            } else if (name.equals(SHOW_CLEAR_BUTTON)) {
                component.putClientProperty(String.format("%s.%s", JTextField.class.getSimpleName(), name), Boolean.valueOf(value));
            } else if (name.equals(LEADING_ICON) || name.equals(TRAILING_ICON)) {
                component.putClientProperty(String.format("%s.%s", JTextField.class.getSimpleName(), name), getIcon(value));
            } else {
                var mutator = map(properties.get(name), BeanAdapter.Property::getMutator);

                if (mutator == null) {
                    throw new UnsupportedOperationException(String.format("Invalid attribute name (%s).", name));
                }

                var propertyType = mutator.getParameterTypes()[0];

                Object argument;
                if (propertyType == Integer.TYPE || propertyType == Integer.class) {
                    argument = switch (name) {
                        case HORIZONTAL_ALIGNMENT -> switch (value) {
                            case LEFT -> SwingConstants.LEFT;
                            case CENTER -> SwingConstants.CENTER;
                            case RIGHT -> SwingConstants.RIGHT;
                            case LEADING -> SwingConstants.LEADING;
                            case TRAILING -> SwingConstants.TRAILING;
                            default -> throw new IllegalArgumentException("Invalid horizontal alignment.");
                        };
                        case VERTICAL_ALIGNMENT -> switch (value) {
                            case TOP -> SwingConstants.TOP;
                            case CENTER -> SwingConstants.CENTER;
                            case BOTTOM -> SwingConstants.BOTTOM;
                            default -> throw new IllegalArgumentException("Invalid vertical alignment.");
                        };
                        case ORIENTATION -> switch (value) {
                            case HORIZONTAL -> SwingConstants.HORIZONTAL;
                            case VERTICAL -> SwingConstants.VERTICAL;
                            default -> throw new IllegalArgumentException("Invalid orientation.");
                        };
                        case FOCUS_LOST_BEHAVIOR -> switch(value) {
                            case COMMIT -> JFormattedTextField.COMMIT;
                            case COMMIT_OR_REVERT -> JFormattedTextField.COMMIT_OR_REVERT;
                            case REVERT -> JFormattedTextField.REVERT;
                            case PERSIST -> JFormattedTextField.PERSIST;
                            default -> throw new IllegalArgumentException("Invalid focus lost behavior.");
                        };
                        default -> Integer.valueOf(value);
                    };
                } else if (propertyType == String.class) {
                    argument = getText(value);
                } else if (propertyType == Color.class) {
                    argument = parseColor(value);
                } else if (propertyType == Font.class) {
                    argument = parseFont(value);
                } else if (propertyType == Icon.class) {
                    argument = getIcon(value);
                } else if (propertyType == Image.class) {
                    argument = getImage(value);
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

        var parent = components.peek();

        if (parent != null) {
            if (parent instanceof JScrollPane scrollPane) {
                scrollPane.setViewportView(component);
            } else {
                parent.add(component, constraints);
            }
        }

        components.push(component);
    }

    private String getText(String value) {
        if (resourceBundle == null) {
            return value;
        } else {
            return resourceBundle.getString(value);
        }
    }

    private Icon getIcon(String value) {
        if (value.endsWith(".svg")) {
            return new FlatSVGIcon(owner.getClass().getResource(value));
        } else {
            throw new UnsupportedOperationException("Unsupported icon type.");
        }
    }

    private Image getImage(String value) {
        try {
            return ImageIO.read(owner.getClass().getResource(value));
        } catch (IOException exception) {
            throw new UnsupportedOperationException(exception);
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
     * Associates a font with a color.
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

    /**
     * Generates a DTD.
     *
     * @param args
     * Command-line arguments (unused).
     */
    public static void main(String[] args) throws IOException {
        var typeSet = new HashSet<Class<?>>();

        var tags = new HashMap<Class<?>, String>();

        for (var entry : types.entrySet()) {
            var tag = entry.getKey();

            Class<?> type = entry.getValue();

            tags.put(type, tag);

            while (type != Object.class) {
                typeSet.add(type);

                type = type.getSuperclass();
            }
        }

        var typeList = new ArrayList<>(typeSet);

        typeList.sort(Comparator.comparing(UILoader::getDepth).thenComparing(Class::getSimpleName));

        var dtdEncoder = new DTDEncoder(typeList, tags);

        var file = new File(new File(System.getProperty("user.dir")), "sierra.dtd");

        try (var outputStream = new FileOutputStream(file)) {
            dtdEncoder.write(null, outputStream);
        }
    }

    private static int getDepth(Class<?> type) {
        var depth = 0;

        while (type != Object.class) {
            depth++;

            type = type.getSuperclass();
        }

        return depth;
    }
}
