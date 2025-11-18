/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.httprpc.sierra.tools.previewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import org.fife.ui.autocomplete.Completion;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

/**
 * Unit test for SierraXMLCompletionProvider focusing only on core element and
 * attribute completion logic. This test ensures that the provider correctly
 * reads tags and attributes from UILoader and suggests: 
 * 1. All UI element tags when starting a tag. 
 * 2. Relevant attributes when inside a known tag.
 */
public class SierraXMLCompletionProviderTest {

    private SierraXMLCompletionProvider provider;
    private JTextComponent mockComponent;

    @BeforeEach
    void setUp() {
        // Initializes the provider, which attempts to load and parse the DTD from the classpath.
        provider = new SierraXMLCompletionProvider();
        mockComponent = mock(JTextComponent.class);

        // Use a simple PlainDocument for getText() calls to work cleanly
        Document doc = new PlainDocument();
        when(mockComponent.getDocument()).thenReturn(doc);
    }

    /**
     * Helper to simulate text insertion into the component and set the caret
     * position. It also mocks the getText(int, int) call to read from the
     * internal document.
     */
    private void setComponentText(String text, int caretOffset) throws BadLocationException {
        var doc = mockComponent.getDocument();
        doc.remove(0, doc.getLength());
        doc.insertString(0, text, null);

        when(mockComponent.getText(anyInt(), anyInt())).thenAnswer(invocation -> {
            int start = invocation.getArgument(0);
            int len = invocation.getArgument(1);
            return doc.getText(start, len);
        });
        when(mockComponent.getCaretPosition()).thenReturn(caretOffset);
    }

    @Test
    void testAllUiElements() throws BadLocationException {
        // Simulate typing '<' to trigger tag completion (caret at offset 1)
        setComponentText("<", 1);

        var suggestions = provider.getCompletions(mockComponent);
        assertFalse(suggestions.isEmpty(), "The provider should suggest elements after typing '<'.");

        // Collect the suggested tag names
        var tagNames = suggestions.stream()
                .map(Completion::getInputText)
                .collect(Collectors.toSet());

        final var EXPECTED_ELEMENT_COUNT = 42;
        assertEquals(EXPECTED_ELEMENT_COUNT, suggestions.size(),
                "Should have parsed exactly " + EXPECTED_ELEMENT_COUNT + " elements based on the DTD content.");

        // Verify a selection of known tags from the DTD are present
        assertTrue(tagNames.contains("button"), "Should contain 'button' tag.");
        assertTrue(tagNames.contains("label"), "Should contain 'label' tag.");
        assertTrue(tagNames.contains("activity-indicator"), "Should contain 'activity-indicator' tag.");

        // Ensure suggestions are alphabetically sorted
        assertSuggestionsSorted(suggestions);
    }

    @Test
    void testButtonAttributes() throws BadLocationException {
        var text = "<button ";
        var caretOffset = text.length(); // Caret is right after the space in "<button "

        setComponentText(text, caretOffset);

        var suggestions = provider.getCompletions(mockComponent);
        assertFalse(suggestions.isEmpty(), "The provider should suggest attributes inside the '<button ' tag.");

        // Collect the suggested attribute names (InputText)
        var attributeNames = suggestions.stream()
                .map(Completion::getInputText)
                .collect(Collectors.toSet());

        assertTrue(attributeNames.contains("name"), "Should suggest the mandatory 'name' attribute.");
        assertTrue(attributeNames.contains("text"), "Should suggest the mandatory 'text' attribute.");

        // Verify other essential attributes are present (from the DTD)
        assertTrue(attributeNames.contains("background"), "Should also suggest the 'background' attribute.");

        // Ensure suggestions are alphabetically sorted
        assertSuggestionsSorted(suggestions);
    }

    @Test
    void testTextAreaAtttributes() throws BadLocationException {
        var text = "<text-area ";
        var caretOffset = text.length();

        setComponentText(text, caretOffset);

        var suggestions = provider.getCompletions(mockComponent);
        assertFalse(suggestions.isEmpty(), "The provider should suggest attributes inside the 'text-area ' tag.");

        // Collect the suggested attribute names (InputText)
        var attributeNames = suggestions.stream()
                .map(Completion::getInputText)
                .collect(Collectors.toSet());

        assertTrue(attributeNames.contains("name"), "Should suggest the mandatory 'name' attribute.");
        assertTrue(attributeNames.contains("text"), "Should suggest the mandatory 'text' attribute.");

        // specific to text area
        assertTrue(attributeNames.contains("lineWrap"), "Should suggest the mandatory 'lineWrap' attribute.");

        // Verify other essential attributes are present (from the DTD)
        assertTrue(attributeNames.contains("background"), "Should also suggest the 'background' attribute.");
        
        assertFalse(attributeNames.contains("tabLayoutPolicy"), "Should not suggest 'tabLayoutPolicy.");

        // Ensure suggestions are alphabetically sorted
        assertSuggestionsSorted(suggestions);
    }

    @Test
    void testAttributeNarrowing() throws BadLocationException {
        // Start with just '<button ' (caret after space)
        var base = "<button ";
        setComponentText(base, base.length());

        var suggestionsAll = provider.getCompletions(mockComponent);
        assertFalse(suggestionsAll.isEmpty(), "Should suggest attributes when inside a tag.");
        assertTrue(suggestionsAll.stream().anyMatch(c -> c.getInputText().equals("name")),
                "Suggestions should include 'name' initially.");

        // Type one character: 'n'
        var oneChar = "<button n";
        setComponentText(oneChar, oneChar.length());

        var suggestionsN = provider.getCompletions(mockComponent);
        assertFalse(suggestionsN.isEmpty(), "Should still suggest attributes after typing 'n'.");
        for (var c : suggestionsN) {
            assertTrue(c.getInputText().toLowerCase().startsWith("n"),
                    "All suggestions after typing 'n' should start with 'n'.");
        }

        // Type a second character: 'a' -> prefix 'na'
        var twoChar = "<button na";
        setComponentText(twoChar, twoChar.length());

        var suggestionsNA = provider.getCompletions(mockComponent);
        assertFalse(suggestionsNA.isEmpty(), "Should still suggest attributes after typing 'na'.");
        for (var c : suggestionsNA) {
            assertTrue(c.getInputText().toLowerCase().startsWith("na"),
                    "All suggestions after typing 'na' should start with 'na'.");
        }
        assertTrue(suggestionsNA.stream().anyMatch(c -> c.getInputText().equals("name")),
                "Suggestions after 'na' should include the 'name' attribute.");
    }

    @Test
    void testTagNarrowing() throws BadLocationException {
        // Start with just '<' (caret after '<')
        var base = "<";
        setComponentText(base, base.length());

        var suggestionsAll = provider.getCompletions(mockComponent);
        assertFalse(suggestionsAll.isEmpty(), "Should suggest tags when starting a tag.");
        assertTrue(suggestionsAll.stream().anyMatch(c -> c.getInputText().equals("button")),
                "Suggestions should include 'button' initially.");

        // Type one character: 'b'
        var oneChar = "<b";
        setComponentText(oneChar, oneChar.length());

        var suggestionsB = provider.getCompletions(mockComponent);
        assertFalse(suggestionsB.isEmpty(), "Should still suggest tags after typing 'b'.");
        for (var c : suggestionsB) {
            assertTrue(c.getInputText().toLowerCase().startsWith("b"),
                    "All tag suggestions after typing 'b' should start with 'b'.");
        }

        // Type a second character: 'u' -> prefix 'bu'
        var twoChar = "<bu";
        setComponentText(twoChar, twoChar.length());

        var suggestionsBU = provider.getCompletions(mockComponent);
        assertFalse(suggestionsBU.isEmpty(), "Should still suggest tags after typing 'bu'.");
        for (var c : suggestionsBU) {
            assertTrue(c.getInputText().toLowerCase().startsWith("bu"),
                    "All tag suggestions after typing 'bu' should start with 'bu'.");
        }
        assertTrue(suggestionsBU.stream().anyMatch(c -> c.getInputText().equals("button")),
                "Suggestions after 'bu' should include the 'button' tag.");
    }
    
    private void assertSuggestionsSorted(List<Completion> suggestions) {
        var actualNames = suggestions.stream()
                .map(Completion::getInputText)
                .toList(); // Use .collect(Collectors.toList()) if on Java < 16

        var expectedSortedNames = new ArrayList<>(actualNames);
        Collections.sort(expectedSortedNames);

        assertEquals(expectedSortedNames, actualNames, "Suggestions should be sorted alphabetically");
    }

}
