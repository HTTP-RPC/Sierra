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

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.lang.reflect.Constructor;
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

    private Deque<JComponent> components = new LinkedList<>();

    private JComponent root = null;

    private static final String RESOURCE_PREFIX = "$";

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
        var xmlInputFactory = XMLInputFactory.newInstance();

        try (var inputStream = owner.getClass().getResourceAsStream(name)) {
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

            if (name.equals("name")) {
                // TODO Inject member
            } else if (name.startsWith("on")) {
                // TODO Add event listener
            } else {
                var value = xmlStreamReader.getAttributeValue(i);

                if (name.equals("weight")) {
                    try {
                        constraints = Double.valueOf(value);
                    } catch (NumberFormatException exception) {
                        throw new IOException("Invalid weight value.", exception);
                    }
                } else if (name.equals("size")) {
                    int size;
                    try {
                        size = Integer.parseInt(value);
                    } catch (NumberFormatException exception) {
                        throw new IOException("Invalid size value.", exception);
                    }

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
                        if (name.equals("horizontalAlignment") || name.equals("verticalAlignment")) {
                            // TODO
                            argument = SwingConstants.CENTER;
                        } else {
                            try {
                                argument = Integer.valueOf(value);
                            } catch (NumberFormatException exception) {
                                throw new IOException("Invalid integer value.", exception);
                            }
                        }
                    } else if (type == Long.TYPE || type == Long.class) {
                        try {
                            argument = Long.valueOf(value);
                        } catch (NumberFormatException exception) {
                            throw new IOException("Invalid long value.", exception);
                        }
                    } else if (type == Float.TYPE || type == Float.class) {
                        try {
                            argument = Float.valueOf(value);
                        } catch (NumberFormatException exception) {
                            throw new IOException("Invalid float value.", exception);
                        }
                    } else if (type == Double.TYPE || type == Double.class) {
                        try {
                            argument = Double.valueOf(value);
                        } catch (NumberFormatException exception) {
                            throw new IOException("Invalid double value.", exception);
                        }
                    } else if (type == String.class) {
                        if (value.startsWith(RESOURCE_PREFIX)) {
                            value = value.substring(RESOURCE_PREFIX.length());

                            if (value.isEmpty()) {
                                throw new IOException("Invalid resource name.");
                            }

                            if (resourceBundle != null && !value.startsWith(RESOURCE_PREFIX)) {
                                value = resourceBundle.getString(value);
                            }
                        }

                        argument = value;
                    } else if (type == Color.class) {
                        // TODO Parse color
                        continue;
                    } else if (type == Border.class) {
                        // TODO Parse border
                        continue;
                    } else if (type == HorizontalAlignment.class) {
                        // TODO
                        continue;
                    } else if (type == VerticalAlignment.class) {
                        // TODO
                        continue;
                    } else if (type == ImagePane.ScaleMode.class) {
                        // TODO
                        continue;
                    } else if (type == Icon.class) {
                        // TODO Handle SVG documents
                        continue;
                    } else if (type == Image.class) {
                        // TODO
                        continue;
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
}
