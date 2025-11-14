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
package org.httprpc.sierra.previewer;

import org.fife.ui.autocomplete.Completion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.Document;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for SierraXMLCompletionProvider focusing only on core element and attribute completion logic.
 * This test ensures that the provider correctly reads the 'sierra.dtd' and suggests:
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
     * Helper to simulate text insertion into the component and set the caret position.
     * It also mocks the getText(int, int) call to read from the internal document.
     */
    private void setComponentText(String text, int caretOffset) throws BadLocationException {
        Document doc = mockComponent.getDocument();
        doc.remove(0, doc.getLength());
        doc.insertString(0, text, null);
        
        when(mockComponent.getText(anyInt(), anyInt())).thenAnswer(invocation -> {
            int start = invocation.getArgument(0);
            int len = invocation.getArgument(1);
            return doc.getText(start, len);
        });
        when(mockComponent.getCaretPosition()).thenReturn(caretOffset);
    }

    // --- Core Test Case: 1. Tag Completion ---
    
    @Test
    void testCompletion_StartTag_SuggestsAllUIElements() throws BadLocationException {
        // Simulate typing '<' to trigger tag completion (caret at offset 1)
        setComponentText("<", 1); 
        
        List<Completion> suggestions = provider.getCompletions(mockComponent);
        assertFalse(suggestions.isEmpty(), "The provider should suggest elements after typing '<'.");

        // Collect the suggested tag names
        Set<String> tagNames = suggestions.stream()
            .map(Completion::getInputText)
            .collect(Collectors.toSet());

        final int EXPECTED_ELEMENT_COUNT = 42; 
        assertEquals(EXPECTED_ELEMENT_COUNT, suggestions.size(), 
            "Should have parsed exactly " + EXPECTED_ELEMENT_COUNT + " elements based on the DTD content.");

        // Verify a selection of known tags from the DTD are present
        assertTrue(tagNames.contains("button"), "Should contain 'button' tag.");
        assertTrue(tagNames.contains("label"), "Should contain 'label' tag.");
        assertTrue(tagNames.contains("activity-indicator"), "Should contain 'activity-indicator' tag.");
        
        // Ensure suggestions are alphabetically sorted
        for (int i = 0; i < suggestions.size() - 1; i++) {
            String current = suggestions.get(i).getInputText();
            String next = suggestions.get(i + 1).getInputText();
            assertTrue(current.compareTo(next) <= 0, "Suggestions should be sorted alphabetically.");
        }
    }
    
    // --- Core Test Case: 2. Attribute Completion for <button> ---

    @Test
    void testCompletion_InsideButtonTag_SuggestsCoreAttributes() throws BadLocationException {
        String text = "<button ";
        int caretOffset = text.length(); // Caret is right after the space in "<button "
        
        setComponentText(text, caretOffset);
        
        List<Completion> suggestions = provider.getCompletions(mockComponent);
        assertFalse(suggestions.isEmpty(), "The provider should suggest attributes inside the '<button ' tag.");
        
        // Collect the suggested attribute names (InputText)
        Set<String> attributeNames = suggestions.stream()
            .map(Completion::getInputText)
            .collect(Collectors.toSet());

        assertTrue(attributeNames.contains("name"), "Should suggest the mandatory 'name' attribute.");
        //assertTrue(attributeNames.contains("text"), "Should suggest the mandatory 'text' attribute.");

        // Verify other essential attributes are present (from the DTD)
        assertTrue(attributeNames.contains("background"), "Should also suggest the 'background' attribute.");
        
        // Ensure suggestions are sorted alphabetically
        for (int i = 0; i < suggestions.size() - 1; i++) {
            String current = suggestions.get(i).getInputText();
            String next = suggestions.get(i + 1).getInputText();
            assertTrue(current.compareTo(next) <= 0, "Suggestions should be sorted alphabetically.");
        }
    }
    
        @Test
    void testCompletion_InsideTextAreaTag_SuggestsCoreAttributes() throws BadLocationException {
        String text = "<text-area ";
        int caretOffset = text.length();
        
        setComponentText(text, caretOffset);
        
        List<Completion> suggestions = provider.getCompletions(mockComponent);
        assertFalse(suggestions.isEmpty(), "The provider should suggest attributes inside the 'text-area ' tag.");
        
        // Collect the suggested attribute names (InputText)
        Set<String> attributeNames = suggestions.stream()
            .map(Completion::getInputText)
            .collect(Collectors.toSet());

        assertTrue(attributeNames.contains("name"), "Should suggest the mandatory 'name' attribute.");
        //assertTrue(attributeNames.contains("text"), "Should suggest the mandatory 'text' attribute.");
        
        // specific to text area                
        assertTrue(attributeNames.contains("lineWrap"), "Should suggest the mandatory 'lineWrap' attribute.");

        // Verify other essential attributes are present (from the DTD)
        assertTrue(attributeNames.contains("background"), "Should also suggest the 'background' attribute.");
        
        // Ensure suggestions are sorted alphabetically
        for (int i = 0; i < suggestions.size() - 1; i++) {
            String current = suggestions.get(i).getInputText();
            String next = suggestions.get(i + 1).getInputText();
            assertTrue(current.compareTo(next) <= 0, "Suggestions should be sorted alphabetically.");
        }
    }
}