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
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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

            appendAttributeDeclaration(NAME, "ID", writer);

            appendAttributeDeclaration(BORDER, CDATA, writer);
            appendAttributeDeclaration(PADDING, CDATA, writer);
            appendAttributeDeclaration(WEIGHT, CDATA, writer);
            appendAttributeDeclaration(SIZE, CDATA, writer);

            appendAttributeDeclaration(FLAT_LAF_STYLE, CDATA, writer);
            appendAttributeDeclaration(FLAT_LAF_STYLE_CLASS, CDATA, writer);

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

    private Deque<JComponent> components = new LinkedList<>();

    private JComponent root = null;

    private static final String NAME = "name";

    private static final String BORDER = "border";
    private static final String PADDING = "padding";
    private static final String WEIGHT = "weight";
    private static final String SIZE = "size";

    private static final String FLAT_LAF_STYLE = "FlatLaf.style";
    private static final String FLAT_LAF_STYLE_CLASS = "FlatLaf.styleClass";

    private static final String HORIZONTAL_ALIGNMENT = "horizontalAlignment";
    private static final String VERTICAL_ALIGNMENT = "verticalAlignment";
    private static final String ORIENTATION = "orientation";

    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String LEADING = "leading";
    private static final String TRAILING = "trailing";

    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";

    private static final String CENTER = "center";

    private static final String HORIZONTAL = "horizontal";
    private static final String VERTICAL = "vertical";

    private static Map<String, Constructor<? extends JComponent>> constructors = new HashMap<>();
    private static Map<String, Map<String, BeanAdapter.Property>> properties = new HashMap<>();

    private static Map<String, Color> colors = new HashMap<>();

    private static Map<String, Font> fonts = new HashMap<>();

    static {
        bind("label", JLabel.class);
        bind("button", JButton.class);
        bind("toggle-button", JToggleButton.class);
        bind("radio-button", JRadioButton.class);
        bind("check-box", JCheckBox.class);
        bind("text-field", JTextField.class);
        bind("password-field", JPasswordField.class);
        bind("combo-box", JComboBox.class);
        bind("spinner", JSpinner.class);
        bind("slider", JSlider.class);
        bind("progress-bar", JProgressBar.class);
        bind("separator", JSeparator.class);
        bind("scroll-pane", JScrollPane.class);
        bind("list", JList.class);
        bind("text-area", JTextArea.class);
        bind("table", JTable.class);
        bind("tree", JTree.class);

        bind("row-panel", RowPanel.class);
        bind("column-panel", ColumnPanel.class);
        bind("stack-panel", StackPanel.class);
        bind("spacer", Spacer.class);
        bind("text-pane", TextPane.class);
        bind("image-pane", ImagePane.class);
        bind("menu-button", MenuButton.class);
        bind("date-picker", DatePicker.class);
        bind("time-picker", TimePicker.class);
        bind("suggestion-picker", SuggestionPicker.class);
        bind("activity-indicator", ActivityIndicator.class);
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

        var constructor = constructors.get(tag);

        if (constructor == null) {
            throw new UnsupportedOperationException(String.format("Invalid tag (%s).", tag));
        }

        JComponent component;
        try {
            component = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException  | InvocationTargetException exception) {
            throw new UnsupportedOperationException(exception);
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
                    field.set(owner, component);
                } catch (IllegalAccessException exception) {
                    throw new UnsupportedOperationException(exception);
                }
            } else if (name.equals(BORDER)) {
                lineBorder = parseBorder(value);
            } else if (name.equals(PADDING)) {
                emptyBorder = parsePadding(value);
            } else if (name.equals(WEIGHT)) {
                constraints = Double.valueOf(value);
            } else if (name.equals(SIZE)) {
                var size = Integer.parseInt(value);

                component.setPreferredSize(new Dimension(size, size));
            } else if (name.equals(FLAT_LAF_STYLE) || name.equals(FLAT_LAF_STYLE_CLASS)) {
                component.putClientProperty(name, value);
            } else {
                var mutator = map(properties.get(tag).get(name), BeanAdapter.Property::getMutator);

                if (mutator == null) {
                    throw new UnsupportedOperationException(String.format("Invalid attribute name (%s).", name));
                }

                var type = mutator.getParameterTypes()[0];

                Object argument;
                if (type == Integer.TYPE || type == Integer.class) {
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
                        default -> Integer.valueOf(value);
                    };
                } else if (type == String.class) {
                    if (resourceBundle == null) {
                        argument = value;
                    } else {
                        argument = resourceBundle.getString(value);
                    }
                } else if (type == Color.class) {
                    argument = parseColor(value);
                } else if (type == Font.class) {
                    argument = parseFont(value);
                } else if (type == Icon.class) {
                    if (value.endsWith(".svg")) {
                        argument = new FlatSVGIcon(owner.getClass().getResource(value));
                    } else {
                        throw new UnsupportedOperationException("Unsupported icon type.");
                    }
                } else if (type == Image.class) {
                    try {
                        argument = ImageIO.read(owner.getClass().getResource(value));
                    } catch (IOException exception) {
                        throw new UnsupportedOperationException(exception);
                    }
                } else {
                    if (Enum.class.isAssignableFrom(type)) {
                        value = value.toUpperCase().replace('-', '_');
                    }

                    argument = BeanAdapter.coerce(value, type);
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
     */
    public static void bind(String tag, Class<? extends JComponent> type) {
        if (tag == null || type == null) {
            throw new IllegalArgumentException();
        }

        if (Modifier.isAbstract(type.getModifiers())) {
            throw new IllegalArgumentException("Invalid type.");
        }

        Constructor<? extends JComponent> constructor;
        try {
            constructor = type.getDeclaredConstructor();
        } catch (NoSuchMethodException exception) {
            throw new UnsupportedOperationException(exception);
        }

        constructors.put(tag, constructor);

        properties.put(tag, BeanAdapter.getProperties(type));
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
            var size = Integer.parseInt(components[0].trim());

            return new EmptyBorder(size, size, size, size);
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

    private static Color parseColor(String value) {
        var color = colors.get(value);

        return (color == null) ? Color.decode(value) : color;
    }

    private static Font parseFont(String value) {
        var font = fonts.get(value);

        return (font == null) ? Font.decode(value) : font;
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

        for (var entry : constructors.entrySet()) {
            var tag = entry.getKey();
            var type = (Class<?>)entry.getValue().getDeclaringClass();

            tags.put(type, tag);

            while (type != Object.class) {
                typeSet.add(type);

                type = type.getSuperclass();
            }
        }

        var typeList = new ArrayList<>(typeSet);

        typeList.sort(Comparator.comparing(UILoader::getDepth).thenComparing(Class::getSimpleName));

        var dtdEncoder = new DTDEncoder(typeList, tags);

        dtdEncoder.write(null, System.out);
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
