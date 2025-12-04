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

package org.httprpc.sierra.tools.dtd;

import org.httprpc.kilo.beans.BeanAdapter;
import org.httprpc.kilo.io.Encoder;
import org.httprpc.sierra.ConstantAdapter;
import org.httprpc.sierra.HorizontalAlignment;
import org.httprpc.sierra.MenuButton;
import org.httprpc.sierra.UILoader;
import org.httprpc.sierra.VerticalAlignment;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DTDEncoder extends Encoder<Void> {
    private List<Class<?>> typeList;
    private Map<Class<?>, String> tags;

    private static final String CDATA = "CDATA";

    private DTDEncoder(List<Class<?>> typeList, Map<Class<?>, String> tags) {
        this.typeList = typeList;
        this.tags = tags;
    }

    @Override
    public void write(Void value, Writer writer) throws IOException {
        startEntityDeclaration(UILoader.class, null, writer);

        appendAttributeDeclaration(UILoader.Attribute.NAME.getName(), CDATA, writer);
        appendAttributeDeclaration(UILoader.Attribute.GROUP.getName(), CDATA, writer);
        appendAttributeDeclaration(UILoader.Attribute.BORDER.getName(), CDATA, writer);
        appendAttributeDeclaration(UILoader.Attribute.PADDING.getName(), CDATA, writer);
        appendAttributeDeclaration(UILoader.Attribute.TITLE.getName(), CDATA, writer);
        appendAttributeDeclaration(UILoader.Attribute.WEIGHT.getName(), CDATA, writer);
        appendAttributeDeclaration(UILoader.Attribute.SIZE.getName(), CDATA, writer);
        appendAttributeDeclaration(UILoader.Attribute.TAB_TITLE.getName(), CDATA, writer);
        appendAttributeDeclaration(UILoader.Attribute.TAB_ICON.getName(), CDATA, writer);
        appendAttributeDeclaration(UILoader.Attribute.STYLE.getName(), CDATA, writer);
        appendAttributeDeclaration(UILoader.Attribute.STYLE_CLASS.getName(), CDATA, writer);

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
                    if (attributeName.equals(UILoader.Attribute.HORIZONTAL_ALIGNMENT.getName())) {
                        attributeType = getAttributeType(HorizontalAlignment.values());
                    } else if (attributeName.equals(UILoader.Attribute.VERTICAL_ALIGNMENT.getName())) {
                        attributeType = getAttributeType(VerticalAlignment.values());
                    } else if (attributeName.equals(UILoader.Attribute.ORIENTATION.getName())) {
                        attributeType = getAttributeType(UILoader.Orientation.values());
                    } else if (attributeName.equals(UILoader.Attribute.FOCUS_LOST_BEHAVIOR.getName())) {
                        attributeType = getAttributeType(UILoader.FocusLostBehavior.values());
                    } else if (attributeName.equals(UILoader.Attribute.HORIZONTAL_SCROLL_BAR_POLICY.getName())) {
                        attributeType = getAttributeType(UILoader.HorizontalScrollBarPolicy.values());
                    } else if (attributeName.equals(UILoader.Attribute.VERTICAL_SCROLL_BAR_POLICY.getName())) {
                        attributeType = getAttributeType(UILoader.VerticalScrollBarPolicy.values());
                    } else if (attributeName.equals(UILoader.Attribute.TAB_PLACEMENT.getName())) {
                        attributeType = getAttributeType(UILoader.TabPlacement.values());
                    } else if (attributeName.equals(UILoader.Attribute.TAB_LAYOUT_POLICY.getName())) {
                        attributeType = getAttributeType(UILoader.TabLayoutPolicy.values());
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
                    || propertyType == Image.class
                    || propertyType == KeyStroke.class) {
                    attributeType = CDATA;
                } else {
                    attributeType = null;
                }

                if (attributeType != null) {
                    appendAttributeDeclaration(attributeName, attributeType, writer);
                }
            }

            if (type == JTextField.class) {
                appendAttributeDeclaration(UILoader.Attribute.PLACEHOLDER_TEXT.getName(), CDATA, writer);
                appendAttributeDeclaration(UILoader.Attribute.SHOW_CLEAR_BUTTON.getName(), String.format("(%b|%b)", true, false), writer);
                appendAttributeDeclaration(UILoader.Attribute.LEADING_ICON.getName(), CDATA, writer);
                appendAttributeDeclaration(UILoader.Attribute.TRAILING_ICON.getName(), CDATA, writer);
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

    private String getAttributeType(ConstantAdapter[] values) {
        var attributeTypeBuilder = new StringBuilder();

        attributeTypeBuilder.append('(');

        for (var i = 0; i < values.length; i++) {
            if (i > 0) {
                attributeTypeBuilder.append('|');
            }

            attributeTypeBuilder.append(values[i].getKey());
        }

        attributeTypeBuilder.append(')');

        return attributeTypeBuilder.toString();
    }

    private void startEntityDeclaration(Class<?> type, Class<?> baseType, Writer writer) throws IOException {
        writer.append("<!ENTITY % ");
        writer.append(type.getCanonicalName());
        writer.append(" \"");

        if (baseType != null) {
            writer.append("%");

            if (baseType == Object.class) {
                writer.append(UILoader.class.getCanonicalName());
            } else {
                writer.append(baseType.getCanonicalName());
            }

            writer.append("; ");
        }
    }

    private void appendAttributeDeclaration(String name, String type, Writer writer) throws IOException {
        writer.append(name);
        writer.append(" ");
        writer.append(type);
        writer.append(" ");
    }

    private void endEntityDeclaration(Writer writer) throws IOException {
        writer.append("\">\n");
    }

    private void declareElement(String tag, Class<?> type, Writer writer) throws IOException {
        writer.append("<!ELEMENT ");
        writer.append(tag);
        writer.append(" ");

        if (JPanel.class.isAssignableFrom(type)
            || JScrollPane.class.isAssignableFrom(type)
            || JSplitPane.class.isAssignableFrom(type)
            || JTabbedPane.class.isAssignableFrom(type)
            || JToolBar.class.isAssignableFrom(type)
            || JMenuBar.class.isAssignableFrom(type)
            || JMenu.class.isAssignableFrom(type)
            || MenuButton.class.isAssignableFrom(type)) {
            writer.append("(ANY)");
        } else {
            writer.append("EMPTY");
        }

        writer.append(">\n");
    }

    private void declareAttributeList(String tag, Class<?> type, Writer writer) throws IOException {
        writer.append("<!ATTLIST ");
        writer.append(tag);
        writer.append(" %");
        writer.append(type.getCanonicalName());
        writer.append(";>\n");
    }

    public static void main(String[] args) throws Exception {
        var workingPath = Path.of(System.getProperty("user.dir"));

        if (args.length > 0) {
            var classLoader = ClassLoader.getSystemClassLoader();

            if (args.length > 1) {
                try (var paths = Files.walk(workingPath.resolve(args[1]))) {
                    classLoader = new URLClassLoader(paths.map(path -> {
                        try {
                            return path.toUri().toURL();
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    }).toArray(URL[]::new), classLoader);
                }
            }

            var bindings = new Properties();

            try (var inputStream = Files.newInputStream(workingPath.resolve(args[0]))) {
                bindings.load(inputStream);
            }

            UILoader.bind(bindings, classLoader);
        }

        var typeSet = new HashSet<Class<?>>();

        var tags = new HashMap<Class<?>, String>();

        for (var tag : UILoader.getTags()) {
            var type = (Class<?>)UILoader.getType(tag);

            tags.put(type, tag);

            while (type != Object.class) {
                typeSet.add(type);

                type = type.getSuperclass();
            }
        }

        var typeList = new ArrayList<>(typeSet);

        typeList.sort(Comparator.comparing(DTDEncoder::getDepth).thenComparing(Class::getCanonicalName));

        try (var outputStream = Files.newOutputStream(workingPath.resolve("sierra.dtd"))) {
            var dtdEncoder = new DTDEncoder(typeList, tags);

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
