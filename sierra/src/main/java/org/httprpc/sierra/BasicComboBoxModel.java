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

import javax.swing.ComboBoxModel;
import java.util.List;

import static org.httprpc.kilo.util.Iterables.*;

/**
 * Basic combo box model.
 *
 * @param <E>
 * The element type.
 */
public class BasicComboBoxModel<E> extends BasicListModel<E> implements ComboBoxModel<E> {
    private Object selectedItem;

    /**
     * Constructs a new basic combo box model.
     *
     * @param elements
     * The combo box elements.
     */
    public BasicComboBoxModel(List<? extends E> elements) {
        super(elements);

        selectedItem = firstOf(elements);
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void setSelectedItem(Object selectedItem) {
        this.selectedItem = selectedItem;
    }
}
