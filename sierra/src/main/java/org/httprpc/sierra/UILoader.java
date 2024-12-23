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

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Provides support for deserializing a component hierarchy from markup.
 */
public class UILoader {
    private Object owner;
    private String name;
    private ResourceBundle resourceBundle;

    private Map<String, Field> fields = new HashMap<>();

    private Deque<JComponent> components = new LinkedList<>();

    private JComponent root = null;

    private static final String SET_PREFIX = "set";

    private static Map<String, Constructor<? extends JComponent>> constructors = new HashMap<>();
    private static Map<String, Map<String, Method>> mutators = new HashMap<>();

    static {
        // TODO Additional types
        bind("label", JLabel.class);
        bind("button", JButton.class);

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
    }

    private UILoader(Object owner, String name, ResourceBundle resourceBundle) {
        this.owner = owner;
        this.name = name;
        this.resourceBundle = resourceBundle;
    }

    private JComponent load() throws IOException {
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
            var xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);

            while (xmlStreamReader.hasNext()) {
                switch (xmlStreamReader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> processStartElement(xmlStreamReader);
                    case XMLStreamConstants.END_ELEMENT -> processEndElement();
                }
            }
        } catch (XMLStreamException exception) {
            throw new IOException(exception);
        }

        return root;
    }

    private void processStartElement(XMLStreamReader xmlStreamReader) throws IOException {
        var tag = xmlStreamReader.getLocalName();

        var constructor = constructors.get(tag);

        if (constructor == null) {
            throw new IOException("Invalid tag.");
        }

        JComponent component;
        try {
            component = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException  | InvocationTargetException exception) {
            throw new UnsupportedOperationException(exception);
        }

        Object constraints = null;

        for (int i = 0, n = xmlStreamReader.getAttributeCount(); i < n; i++) {
            var name = xmlStreamReader.getAttributeLocalName(i);
            var value = xmlStreamReader.getAttributeValue(i);

            if (name.equals("name")) {
                var field = fields.get(value);

                if (field == null) {
                    throw new IOException("Invalid field name.");
                }

                field.setAccessible(true);

                try {
                    field.set(owner, component);
                } catch (IllegalAccessException exception) {
                    throw new UnsupportedOperationException(exception);
                }
            } else {
                if (name.equals("weight")) {
                    constraints = Double.valueOf(value);
                } else if (name.equals("size")) {
                    var size = Integer.parseInt(value);

                    component.setPreferredSize(new Dimension(size, size));
                } else {
                    var mutator = mutators.get(tag).get(name);

                    if (mutator == null) {
                        throw new IOException("Invalid property name.");
                    }

                    var type = mutator.getParameterTypes()[0];

                    Object argument;
                    if (type == Boolean.TYPE || type == Boolean.class) {
                        argument = Boolean.valueOf(value);
                    } else if (type == Integer.TYPE || type == Integer.class) {
                        if (name.equals("horizontalAlignment")) {
                            argument = switch (value) {
                                case "left" -> SwingConstants.LEFT;
                                case "center" -> SwingConstants.CENTER;
                                case "right" -> SwingConstants.RIGHT;
                                case "leading" -> SwingConstants.LEADING;
                                case "trailing" -> SwingConstants.TRAILING;
                                default -> throw new IllegalArgumentException("Invalid horizontal alignment.");
                            };
                        } else if (name.equals("verticalAlignment")) {
                            argument = switch (value) {
                                case "top" -> SwingConstants.TOP;
                                case "center" -> SwingConstants.CENTER;
                                case "bottom" -> SwingConstants.BOTTOM;
                                default -> throw new IllegalArgumentException("Invalid vertical alignment.");
                            };
                        } else {
                            argument = Integer.valueOf(value);
                        }
                    } else if (type == Long.TYPE || type == Long.class) {
                        argument = Long.valueOf(value);
                    } else if (type == Float.TYPE || type == Float.class) {
                        argument = Float.valueOf(value);
                    } else if (type == Double.TYPE || type == Double.class) {
                        argument = Double.valueOf(value);
                    } else if (type == String.class) {
                        if (resourceBundle == null) {
                            argument = value;
                        } else {
                            argument = resourceBundle.getString(value);
                        }
                    } else if (type == Color.class) {
                        argument = Color.decode(value);
                    } else if (type == Font.class) {
                        argument = Font.decode(value);
                    } else if (type == Border.class) {
                        argument = parseBorder(value);
                    } else if (type == HorizontalAlignment.class) {
                        argument = switch (value) {
                            case "leading" -> HorizontalAlignment.LEADING;
                            case "trailing" -> HorizontalAlignment.TRAILING;
                            case "center" -> HorizontalAlignment.CENTER;
                            default -> throw new IllegalArgumentException("Invalid horizontal alignment.");
                        };
                    } else if (type == VerticalAlignment.class) {
                        argument = switch (value) {
                            case "top" -> VerticalAlignment.TOP;
                            case "bottom" -> VerticalAlignment.BOTTOM;
                            case "center" -> VerticalAlignment.CENTER;
                            default -> throw new IllegalArgumentException("Invalid vertical alignment.");
                        };
                    } else if (type == ImagePane.ScaleMode.class) {
                        argument = switch (value) {
                            case "none" -> ImagePane.ScaleMode.NONE;
                            case "fill-width" -> ImagePane.ScaleMode.FILL_WIDTH;
                            case "fill-height" -> ImagePane.ScaleMode.FILL_HEIGHT;
                            default -> throw new IllegalArgumentException("Invalid image pane scale mode.");
                        };
                    } else if (type == Icon.class) {
                        // TODO Handle SVG documents
                        continue;
                    } else if (type == Image.class) {
                        argument = ImageIO.read(owner.getClass().getResource(value));
                    } else {
                        throw new UnsupportedOperationException("Unsupported property type.");
                    }

                    try {
                        mutator.invoke(component, argument);
                    } catch (IllegalAccessException | InvocationTargetException exception) {
                        throw new UnsupportedOperationException(exception);
                    }
                }
            }
        }

        var parent = components.peek();

        if (parent != null) {
            parent.add(component, constraints);
        }

        components.push(component);
    }

    private void processEndElement() {
        root = components.pop();
    }

    /**
     * Loads a component hierarchy from a markup document.
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
    public static JComponent load(Object owner, String name) throws IOException {
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
    public static JComponent load(Object owner, String name, ResourceBundle resourceBundle) throws IOException {
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

        Constructor<? extends JComponent> constructor;
        try {
            constructor = type.getDeclaredConstructor();
        } catch (NoSuchMethodException exception) {
            throw new UnsupportedOperationException(exception);
        }

        constructors.put(tag, constructor);

        var methods = type.getMethods();

        for (var i = 0; i < methods.length; i++) {
            var method = methods[i];

            if (method.getDeclaringClass() == Object.class) {
                continue;
            }

            var methodName = method.getName();

            if (methodName.startsWith(SET_PREFIX)
                && method.getReturnType() == Void.TYPE
                && method.getParameterCount() == 1) {
                var j = SET_PREFIX.length();
                var n = methodName.length();

                if (j == n) {
                    continue;
                }

                var c = methodName.charAt(j++);

                if (j == n || Character.isLowerCase(methodName.charAt(j))) {
                    c = Character.toLowerCase(c);
                }

                var propertyName = c + methodName.substring(j);

                mutators.computeIfAbsent(tag, key -> new HashMap<>()).put(propertyName, method);
            }
        }
    }

    private static Border parseBorder(String value) {
        // TODO
        return new EmptyBorder(8, 8, 8, 8);
    }
}
