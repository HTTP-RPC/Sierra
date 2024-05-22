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

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import java.util.Collections;
import java.util.List;

/**
 * Text field that provides a list of suggested values.
 */
public class SuggestionPicker extends Picker {
    private class SuggestionPickerListModel implements ListModel<String> {
        @Override
        public int getSize() {
            return suggestions.size();
        }

        @Override
        public String getElementAt(int index) {
            return suggestions.get(index);
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            // No-op
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            // No-op
        }
    }

    private List<String> suggestions = Collections.emptyList();

    private int maximumRowCount = 8;

    /**
     * Constructs a new suggestion picker.
     *
     * @param columns
     * The column count.
     */
    public SuggestionPicker(int columns) {
        super(columns);
    }

    /**
     * Returns the suggestion list.
     *
     * @return
     * The suggestion list.
     */
    public List<String> getSuggestions() {
        return suggestions;
    }

    /**
     * Sets the suggestion list.
     *
     * @param suggestions
     * The suggestion list.
     */
    public void setSuggestions(List<String> suggestions) {
        if (suggestions == null) {
            throw new IllegalArgumentException();
        }

        this.suggestions = suggestions;
    }

    /**
     * Returns the maximum row count.
     *
     * @return
     * The maximum row count.
     */
    public int getMaximumRowCount() {
        return maximumRowCount;
    }

    /**
     * Sets the maximum row count.
     *
     * @param maximumRowCount
     * The maximum row count.
     */
    public void setMaximumRowCount(int maximumRowCount) {
        if (maximumRowCount <= 0) {
            throw new IllegalArgumentException();
        }

        this.maximumRowCount = maximumRowCount;
    }

    /**
     * Returns {@code true}.
     * {@inheritDoc}
     */
    @Override
    protected boolean isPopupEnabled() {
        return !suggestions.isEmpty();
    }

    /**
     * Returns a suggestion picker popup component.
     * {@inheritDoc}
     */
    @Override
    protected JComponent getPopupComponent() {
        var list = new JList<String>();

        list.setModel(new SuggestionPickerListModel());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(Math.min(suggestions.size(), maximumRowCount));
        list.setFocusable(false);

        var scrollPane = new JScrollPane(list);

        list.setSelectedValue(getText(), true);

        list.addListSelectionListener(event -> {
            setText(list.getSelectedValue());

            super.fireActionPerformed();

            hidePopup();
        });

        return scrollPane;
    }
}
