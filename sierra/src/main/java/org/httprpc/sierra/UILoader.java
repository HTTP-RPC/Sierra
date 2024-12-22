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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    private static Map<String, Class<? extends JComponent>> bindings = new HashMap<>();

    static {
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
                    case XMLStreamConstants.START_ELEMENT -> {
                        processStartElement(xmlStreamReader);
                    }
                    case XMLStreamConstants.END_ELEMENT -> {
                        processEndElement();
                    }
                }
            }
        } catch (XMLStreamException exception) {
            throw new IOException(exception);
        }

        return root;
    }

    private void processStartElement(XMLStreamReader xmlStreamReader) throws IOException {
        var tag = xmlStreamReader.getLocalName();

        var type = bindings.get(tag);

        if (type == null) {
            throw new IOException("Invalid tag.");
        }

        Constructor<? extends JComponent> constructor;
        try {
            constructor = type.getDeclaredConstructor();
        } catch (NoSuchMethodException exception) {
            throw new UnsupportedOperationException(exception);
        }

        JComponent component;
        try {
            component = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException  | InvocationTargetException exception) {
            throw new UnsupportedOperationException(exception);
        }

        for (int i = 0, n = xmlStreamReader.getAttributeCount(); i < n; i++) {
            var name = xmlStreamReader.getAttributeLocalName(i);
            var value = xmlStreamReader.getAttributeValue(i);

            if (value.startsWith(RESOURCE_PREFIX)) {
                value = value.substring(RESOURCE_PREFIX.length());

                if (value.isEmpty()) {
                    throw new IOException("Invalid resource name.");
                }

                if (resourceBundle != null && !value.startsWith(RESOURCE_PREFIX)) {
                    value = resourceBundle.getString(value);
                }
            }

            // TODO Set properties
            // TODO Inject instance
            // TODO Add event listeners
            // TODO Capture constraints (weight)

            // TODO Handle "size" attribute for spacers
        }

        var parent = components.peek();

        if (parent != null) {
            // TODO Weight constraint
            parent.add(component);
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

        bindings.put(tag, type);
    }
}
