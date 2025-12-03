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
package org.httprpc.sierra.tools.previewer;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.httprpc.kilo.beans.BeanAdapter;
import org.httprpc.sierra.UILoader;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.sierra.UILoader.Attribute.*;

/**
 * Provides context-aware autocompletion for Sierra DSL XML by asking UILoader
 * to obtain valid elements and attributes for the current tag. Includes
 * attribute value definitions in the description.
 */
public class SierraXMLCompletionProvider extends DefaultCompletionProvider {

    // Regex to find already defined attributes in a tag, e.g., focusable="true"
    private static final Pattern DEFINED_ATTR_PATTERN = Pattern.compile("\\s+([a-zA-Z]+)\\s*=");

    // Map to store the final resolved attributes for each tag
    // TagName -> {AttributeName -> Description/ValueDefinitionString}
    private final Map<String, Map<String, String>> elementAttributeDefinitions = new HashMap<>();

    private final Map<Class<?>, Map<String, String>> sierraAttributeDefinitions = mapOf(
            entry(JComponent.class, mapOf(
                    entry(NAME.getName(), NAME.getType().getSimpleName()),
                    entry(GROUP.getName(), GROUP.getType().getSimpleName()),
                    entry(BORDER.getName(), BORDER.getType().getSimpleName()),
                    entry(PADDING.getName(), PADDING.getType().getSimpleName()),
                    entry(TITLE.getName(), TITLE.getType().getSimpleName()),
                    entry(WEIGHT.getName(), WEIGHT.getType().getSimpleName()),
                    entry(SIZE.getName(), SIZE.getType().getSimpleName()),
                    entry(TAB_ICON.getName(), TAB_ICON.getType().getSimpleName()),
                    entry(TAB_TITLE.getName(), TAB_TITLE.getType().getSimpleName()),
                    entry(HORIZONTAL_ALIGNMENT.getName(), HORIZONTAL_ALIGNMENT.getType().getSimpleName()),
                    entry(VERTICAL_ALIGNMENT.getName(), VERTICAL_ALIGNMENT.getType().getSimpleName()),
                    entry(ORIENTATION.getName(), ORIENTATION.getType().getSimpleName()),
                    entry(FOCUS_LOST_BEHAVIOR.getName(), FOCUS_LOST_BEHAVIOR.getType().getSimpleName())
            )),
            entry(JTabbedPane.class, mapOf(
                    entry(TAB_LAYOUT_POLICY.getName(), TAB_LAYOUT_POLICY.getType().getSimpleName()),
                    entry(TAB_PLACEMENT.getName(), TAB_PLACEMENT.getType().getSimpleName())
            )),
            entry(JTextField.class, mapOf(
                    entry(PLACEHOLDER_TEXT.getName(), PLACEHOLDER_TEXT.getType().getSimpleName()),
                    entry(LEADING_ICON.getName(), LEADING_ICON.getType().getSimpleName()),
                    entry(TRAILING_ICON.getName(), TRAILING_ICON.getType().getSimpleName()),
                    entry(SHOW_CLEAR_BUTTON.getName(), SHOW_CLEAR_BUTTON.getType().getSimpleName())
            ))
    );

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
    }

    protected Map<String, String> getAttributesForClass(Class<?> componentClass) {
        Map<String, String> attributes = new HashMap<>();
        for (var entry : BeanAdapter.getProperties(componentClass).entrySet()) {
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
                    || propertyType == Image.class
                    || propertyType == KeyStroke.class) {
                attributes.put(entry.getKey(), propertyType.getSimpleName());
            }
        }
        // now check if we have any Sierra attributes to add
        for (var entry : sierraAttributeDefinitions.entrySet()) {
            if (entry.getKey().isAssignableFrom(componentClass)) {
                attributes.putAll(entry.getValue());
            }
        }
        return attributes;
    }

    @Override
    public List<Completion> getCompletions(JTextComponent comp) {
        var offset = comp.getCaretPosition();
        var context = getCompletionContext(comp, offset);

        if (context.type == CompletionType.TAG_NAME) {
            // Narrow tag suggestions by the already-entered prefix (case-insensitive)
            var prefix = getAlreadyEnteredText(comp);
            var lowerPrefix = prefix == null ? "" : prefix.toLowerCase();

            List<Completion> filtered = new ArrayList<>();
            for (var c : tagCompletions) {
                var text = c.getInputText();
                if (!lowerPrefix.isEmpty() && !text.toLowerCase().startsWith(lowerPrefix)) {
                    continue;
                }
                filtered.add(c);
            }

            return filtered;
        }

        if (context.type == CompletionType.ATTRIBUTE_NAME && context.tagName != null) {

            var allAttributes = elementAttributeDefinitions.get(context.tagName);
            if (allAttributes == null) {
                return new ArrayList<>();
            }

            var definedAttributes = getAlreadyDefinedAttributes(comp, context.tagStartOffset, offset);
            List<String> availableAttributeNames = new ArrayList<>();

            for (var attr : allAttributes.keySet()) {
                if (!definedAttributes.contains(attr)) {
                    availableAttributeNames.add(attr);
                }
            }

            Collections.sort(availableAttributeNames);

            // Determine the already-entered text (prefix) so we can narrow suggestions
            var prefix = getAlreadyEnteredText(comp);
            var lowerPrefix = prefix == null ? "" : prefix.toLowerCase();

            List<Completion> suggestions = new ArrayList<>();
            for (var attr : availableAttributeNames) {
                // If there's a prefix, filter attributes that don't start with it (case-insensitive)
                if (!lowerPrefix.isEmpty() && !attr.toLowerCase().startsWith(lowerPrefix)) {
                    continue;
                }

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
            // If there is no space after the tag name, we're still typing the tag name
            if (!textSinceOpen.contains(" ") && !textSinceOpen.contains("\n")) {
                return new CompletionContext(CompletionType.TAG_NAME, tagName, lastOpenAngle);
            }

            // If we have reached the attribute area (there is a space/newline after '<...'),
            // consider this attribute name completion context even if the last character
            // isn't whitespace (so typing 'na' after '<button ' still triggers attribute
            // suggestions). We already checked for being inside a quoted attribute value
            // above.
            if (textSinceOpen.contains(" ") || textSinceOpen.contains("\n")) {
                return new CompletionContext(CompletionType.ATTRIBUTE_NAME, tagName, lastOpenAngle);
            }

        } catch (BadLocationException e) {
            // Ignore
        }
        return new CompletionContext(CompletionType.NONE);
    }

    @Override
    public String getAlreadyEnteredText(JTextComponent comp) {
        // Return the partial attribute name currently being typed so AutoCompletion
        // can filter the list of completions.
        var pos = comp.getCaretPosition();
        try {
            var text = comp.getText(0, pos);
            var lastOpen = text.lastIndexOf('<');
            if (lastOpen == -1) {
                return "";
            }

            // Scan backwards from the caret until we hit whitespace or a character that
            // can't be part of an attribute name (e.g., '=', '<', quotes).
            var i = pos - 1;
            while (i > lastOpen) {
                var c = text.charAt(i);
                if (Character.isWhitespace(c) || c == '<' || c == '=' || c == '"' || c == '\'') {
                    break;
                }
                i--;
            }

            return text.substring(i + 1, pos);
        } catch (BadLocationException e) {
            return "";
        }
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
