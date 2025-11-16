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
package org.httprpc.sierra.previewer;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.httprpc.kilo.beans.BeanAdapter;
import org.httprpc.sierra.UILoader;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Provides context-aware autocompletion for Sierra DSL XML by
 * asking UILoader to obtain valid elements and attributes for the
 * current tag. Includes attribute value definitions in the description.
 */
public class SierraXMLCompletionProvider extends DefaultCompletionProvider {
    // Regex to find already defined attributes in a tag, e.g., focusable="true"
    private static final Pattern DEFINED_ATTR_PATTERN = Pattern.compile("\\s+([a-zA-Z]+)\\s*=");

    // Map to store the final resolved attributes for each tag
    // TagName -> {AttributeName -> Description/ValueDefinitionString}
    private final Map<String, Map<String, String>> elementAttributeDefinitions = new HashMap<>();

    private final Map<String, Map<String, String>> baseClassAttributeDefinitions = new HashMap<>();

    private final List<Completion> tagCompletions = new ArrayList<>();

    public SierraXMLCompletionProvider() {
        loadTags();
        setAutoActivationRules(true, "< ");
    }

    private void loadTags() {
        for (var tag : UILoader.getTags()) {
            tagCompletions.add(new BasicCompletion(this, tag, "Sierra UI Element"));
            elementAttributeDefinitions.put(tag, getAttributesForClass(UILoader.getType(tag)));
        }

        tagCompletions.sort(Comparator.comparing(Completion::getInputText));

        baseClassAttributeDefinitions.put(JComponent.class.getName(), getAttributesForClass(JComponent.class));
        addCommonAttributes();

    }

    private void addCommonAttributes() {
        Map<String, String> attributes = new HashMap<>();
        // Add common attributes applicable to all components
        attributes.put("name", "String");
        attributes.put("group", "String");
        attributes.put("border", "String");
        attributes.put("padding", "String");
        attributes.put("weight", "String");
        attributes.put("size", "String");
        attributes.put("tabTitle", "String");
        attributes.put("tabIcon", "String");
        attributes.put("style", "String");
        attributes.put("styleClass", "String");
        baseClassAttributeDefinitions.put("CommonAttributes", attributes);
    }

    private Map<String, String> getAttributesForClass(Class<?> type){
        Map<String, String> attributes = new HashMap<>();
        for (var entry : BeanAdapter.getProperties(type).entrySet()) {
            var property = entry.getValue();
            var mutator = property.getMutator();

            if (mutator == null) {
                continue;
            }

            var propertyType = mutator.getParameterTypes()[0];

            if (propertyType.isPrimitive()
                || Number.class.isAssignableFrom(propertyType)
                || Enum.class.isAssignableFrom(propertyType)
                || propertyType == String.class
                || propertyType == Color.class
                || propertyType == Font.class
                || propertyType == Icon.class
                || propertyType == Image.class) {
                attributes.put(entry.getKey(), propertyType.getSimpleName());
            }
        }
        return attributes;
    }

    @Override
    public List<Completion> getCompletions(JTextComponent comp) {
        var offset = comp.getCaretPosition();
        var context = getCompletionContext(comp, offset);

        if (context.type == CompletionType.TAG_NAME) {
            return new ArrayList<>(tagCompletions);
        }

        if (context.type == CompletionType.ATTRIBUTE_NAME && context.tagName != null) {

            var allAttributes = elementAttributeDefinitions.get(context.tagName);
            if (allAttributes == null) {
                return new ArrayList<>();
            }

            // parent class
            // @todo we need to walk all the class hierarchy here?
            allAttributes.putAll(baseClassAttributeDefinitions.get(JComponent.class.getName()));
            allAttributes.putAll(baseClassAttributeDefinitions.get("CommonAttributes"));

            var definedAttributes = getAlreadyDefinedAttributes(comp, context.tagStartOffset, offset);
            List<String> availableAttributeNames = new ArrayList<>();

            for (var attr : allAttributes.keySet()) {
                if (!definedAttributes.contains(attr)) {
                    availableAttributeNames.add(attr);
                }
            }

            Collections.sort(availableAttributeNames);

            List<Completion> suggestions = new ArrayList<>();
            for (var attr : availableAttributeNames) {
                var description = allAttributes.get(attr);

                // Create a richer HTML tooltip using the full description
                var tooltip = "<html><b>Attribute for &lt;" + context.tagName + "&gt;</b><br>" + description
                        + "</html>";

                // Use the brief description (e.g., "Values: true, false") for the main popup
                // list
                suggestions.add(new BasicCompletion(this, attr, description, tooltip));
            }
            return suggestions;
        }

        return new ArrayList<>();
    }

    // --- Helper methods remain the same ---

    private CompletionContext getCompletionContext(JTextComponent comp, int offset) {
        // ... (implementation remains the same as previous step) ...
        try {
            var text = comp.getText(0, offset);
            var lastOpenAngle = text.lastIndexOf('<');
            var lastCloseAngle = text.lastIndexOf('>');

            if (lastOpenAngle == -1 || lastCloseAngle > lastOpenAngle) {
                return new CompletionContext(CompletionType.NONE);
            }

            var lastQuote = Math.max(text.lastIndexOf('"'), text.lastIndexOf('\''));
            if (lastQuote > lastOpenAngle) {
                var quoteCount = 0;
                var quoteChar = text.charAt(lastQuote);
                for (var i = lastOpenAngle; i < offset; i++) {
                    if (text.charAt(i) == quoteChar) {
                        quoteCount++;
                    }
                }
                if (quoteCount % 2 != 0) {
                    return new CompletionContext(CompletionType.NONE);
                }
            }

            var tagContent = text.substring(lastOpenAngle + 1);
            var m = Pattern.compile("^([a-z-]+)").matcher(tagContent);
            String tagName = null;
            if (m.find()) {
                tagName = m.group(1);
            }

            var textSinceOpen = text.substring(lastOpenAngle);
            if (!textSinceOpen.contains(" ") && !textSinceOpen.contains("\n")) {
                return new CompletionContext(CompletionType.TAG_NAME, tagName, lastOpenAngle);
            }

            var lastChar = text.charAt(offset - 1);
            if (Character.isWhitespace(lastChar)) {
                return new CompletionContext(CompletionType.ATTRIBUTE_NAME, tagName, lastOpenAngle);
            }

        } catch (BadLocationException e) {
            // Ignore
        }
        return new CompletionContext(CompletionType.NONE);
    }

    private Set<String> getAlreadyDefinedAttributes(JTextComponent comp, int tagStartOffset, int caretOffset) {
        Set<String> definedAttributes = new HashSet<>();
        try {
            var tagText = comp.getText(tagStartOffset, caretOffset - tagStartOffset);
            var m = DEFINED_ATTR_PATTERN.matcher(tagText);
            while (m.find()) {
                definedAttributes.add(m.group(1));
            }
        } catch (BadLocationException e) {
            // Ignore
        }
        return definedAttributes;
    }

    private enum CompletionType {
        TAG_NAME, ATTRIBUTE_NAME, NONE
    }

    private record CompletionContext(CompletionType type, String tagName, int tagStartOffset) {
        CompletionContext(CompletionType type) {
            this(type, null, -1);
        }
    }
}