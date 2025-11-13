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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 * Provides context-aware autocompletion for Sierra DSL XML by dynamically 
 * parsing the sierra.dtd to obtain valid elements and attributes for the 
 * current tag. Includes attribute value definitions in the description.
 */
public class SierraXMLCompletionProvider extends DefaultCompletionProvider {

    // --- DTD Parsing Regex ---
    private static final Pattern ELEMENT_PATTERN = Pattern.compile("<!ELEMENT\\s+([a-z-]+)");
    private static final Pattern ENTITY_PATTERN = Pattern.compile("<!ENTITY\\s+%\\s*([A-Za-z0-9]+)\\s+\"([^\"]+)\"");
    private static final Pattern ATTLIST_PATTERN = Pattern.compile("<!ATTLIST\\s+([a-z-]+)\\s+%([A-Za-z0-9]+);");

    // Regex to find already defined attributes in a tag, e.g., focusable="true"
    private static final Pattern DEFINED_ATTR_PATTERN = Pattern.compile("\\s+([a-zA-Z]+)\\s*=");

    // --- Lookup Maps ---
    private final Map<String, String> entityDefinitions = new HashMap<>();
    
    // NEW: Map to store the final resolved attributes for each tag
    // TagName -> {AttributeName -> Description/ValueDefinitionString}
    private final Map<String, Map<String, String>> elementAttributeDefinitions = new HashMap<>();
    
    private final List<Completion> tagCompletions = new ArrayList<>();

    public SierraXMLCompletionProvider() {
        String dtdContent = loadDTDContent("/sierra.dtd");
        parseDTD(dtdContent);
        setAutoActivationRules(true, "< ");
    }

    private String loadDTDContent(String path) {
        StringBuilder content = new StringBuilder();
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Error: Could not find " + path + " in resources.");
                return "";
            }
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading " + path + ": " + e.getMessage());
        }
        return content.toString();
    }

    private void parseDTD(String content) {
        Map<String, String> elementToEntityName = new HashMap<>();
        Map<String, Map<String, String>> entityAttributeMap = new HashMap<>();

        // 1. Find all element tags
        Matcher elementMatcher = ELEMENT_PATTERN.matcher(content);
        while (elementMatcher.find()) {
            String tagName = elementMatcher.group(1);
            tagCompletions.add(new BasicCompletion(this, tagName, "Sierra UI Element"));
        }
        tagCompletions.sort(Comparator.comparing(Completion::getInputText)); 

        // 2. Find all entity definitions
        Matcher entityMatcher = ENTITY_PATTERN.matcher(content);
        while (entityMatcher.find()) {
            entityDefinitions.put(entityMatcher.group(1), entityMatcher.group(2));
        }

        // 3. Find all ATTLIST mappings
        Matcher attListMatcher = ATTLIST_PATTERN.matcher(content);
        while (attListMatcher.find()) {
            elementToEntityName.put(attListMatcher.group(1), attListMatcher.group(2));
        }

        // 4. Resolve the full attribute definition for every entity
        for (String entityName : entityDefinitions.keySet()) {
            Map<String, String> resolvedDetails = new HashMap<>();
            resolveEntityAttributes(entityName, resolvedDetails, new HashSet<>());
            entityAttributeMap.put(entityName, resolvedDetails);
        }

        // 5. Map Elements to their Resolved Attribute Definitions
        for (Map.Entry<String, String> entry : elementToEntityName.entrySet()) {
            String tagName = entry.getKey();
            String entityName = entry.getValue();
            
            Map<String, String> attributes = entityAttributeMap.get(entityName);
            if (attributes != null) {
                elementAttributeDefinitions.put(tagName, attributes);
            }
        }
    }

/**
     * Recursively resolves all attributes and their value definitions for a given entity.
     * Note: This method populates a Map<String, String> with attributeName -> formattedDescription.
     */
    private void resolveEntityAttributes(String entityName, Map<String, String> resolvedDetails, Set<String> visited) {
        if (!entityDefinitions.containsKey(entityName) || visited.contains(entityName)) {
            return;
        }
        visited.add(entityName);
        String definition = entityDefinitions.get(entityName);

        // 1. Find and resolve parent entities (e.g., "%JComponent;")
        Pattern parentEntityPattern = Pattern.compile("%([A-Za-z0-9]+);");
        Matcher parentMatcher = parentEntityPattern.matcher(definition);
        while (parentMatcher.find()) {
            // Merge parent details first (current entity's attributes can override)
            resolveEntityAttributes(parentMatcher.group(1), resolvedDetails, visited);
        }

        // 2. Find direct attributes in this entity
        // Pattern: ([a-zA-Z]+) -> attribute name
        //          \s+
        //          ((CDATA|\([^)]+\))) -> attribute definition (capturing CDATA or the enumeration)
        Pattern attrPattern = Pattern.compile("([a-zA-Z]+)\\s+((CDATA|\\([^)]+\\)))");
        Matcher attrMatcher = attrPattern.matcher(definition);
        while (attrMatcher.find()) {
            String attrName = attrMatcher.group(1);
            String rawDefinition = attrMatcher.group(2).trim();
            
            String formattedDescription;
            if (rawDefinition.startsWith("CDATA")) {
                // MODIFIED: Changed from "Type: String (CDATA)" to just "Type: String"
                formattedDescription = "Type: String"; 
            } else if (rawDefinition.startsWith("(")) {
                // Example: (true|false) -> Values: true, false
                String values = rawDefinition.substring(1, rawDefinition.length() - 1)
                                             .replace("|", ", ");
                formattedDescription = "Values: " + values;
            } else {
                formattedDescription = "Type: Undefined"; // Should not happen with the regex
            }
            
            resolvedDetails.put(attrName, formattedDescription);
        }
    }

    @Override
    public List<Completion> getCompletions(JTextComponent comp) {
        int offset = comp.getCaretPosition();
        CompletionContext context = getCompletionContext(comp, offset);

        if (context.type == CompletionType.TAG_NAME) {
            return new ArrayList<>(tagCompletions);
        }

        if (context.type == CompletionType.ATTRIBUTE_NAME && context.tagName != null) {
            
            Map<String, String> allAttributes = elementAttributeDefinitions.get(context.tagName);
            if (allAttributes == null) {
                return new ArrayList<>();
            }
            
            Set<String> definedAttributes = getAlreadyDefinedAttributes(comp, context.tagStartOffset, offset);
            List<String> availableAttributeNames = new ArrayList<>();
            
            for (String attr : allAttributes.keySet()) {
                if (!definedAttributes.contains(attr)) {
                    availableAttributeNames.add(attr);
                }
            }
            
            Collections.sort(availableAttributeNames);

            List<Completion> suggestions = new ArrayList<>();
            for (String attr : availableAttributeNames) {
                String description = allAttributes.get(attr);
                
                // Create a richer HTML tooltip using the full description
                String tooltip = "<html><b>Attribute for &lt;" + context.tagName + "&gt;</b><br>" + description + "</html>";
                
                // Use the brief description (e.g., "Values: true, false") for the main popup list
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
            String text = comp.getText(0, offset);
            int lastOpenAngle = text.lastIndexOf('<');
            int lastCloseAngle = text.lastIndexOf('>');

            if (lastOpenAngle == -1 || lastCloseAngle > lastOpenAngle) {
                return new CompletionContext(CompletionType.NONE);
            }

            int lastQuote = Math.max(text.lastIndexOf('"'), text.lastIndexOf('\''));
            if (lastQuote > lastOpenAngle) {
                int quoteCount = 0;
                char quoteChar = text.charAt(lastQuote);
                for (int i = lastOpenAngle; i < offset; i++) {
                    if (text.charAt(i) == quoteChar) {
                        quoteCount++;
                    }
                }
                if (quoteCount % 2 != 0) {
                    return new CompletionContext(CompletionType.NONE);
                }
            }
            
            String tagContent = text.substring(lastOpenAngle + 1);
            Matcher m = Pattern.compile("^([a-z-]+)").matcher(tagContent);
            String tagName = null;
            if (m.find()) {
                tagName = m.group(1);
            }

            String textSinceOpen = text.substring(lastOpenAngle);
            if (!textSinceOpen.contains(" ") && !textSinceOpen.contains("\n")) {
                return new CompletionContext(CompletionType.TAG_NAME, tagName, lastOpenAngle);
            }

            char lastChar = text.charAt(offset - 1);
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
            String tagText = comp.getText(tagStartOffset, caretOffset - tagStartOffset);
            Matcher m = DEFINED_ATTR_PATTERN.matcher(tagText);
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

    private static class CompletionContext {
        final CompletionType type;
        final String tagName;
        final int tagStartOffset;

        CompletionContext(CompletionType type) {
            this(type, null, -1);
        }
        
        CompletionContext(CompletionType type, String tagName, int tagStartOffset) {
            this.type = type;
            this.tagName = tagName;
            this.tagStartOffset = tagStartOffset;
        }
    }
}