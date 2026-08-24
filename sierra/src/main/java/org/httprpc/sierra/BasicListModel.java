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

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import java.util.List;

/**
 * Basic list model.
 *
 * @param <E>
 * The element type.
 */
public class BasicListModel<E> implements ListModel<E> {
    private List<E> elements;

    /**
     * Constructs a new basic list model.
     *
     * @param elements
     * The list elements.
     */
    public BasicListModel(List<E> elements) {
        if (elements == null) {
            throw new IllegalArgumentException();
        }

        this.elements = elements;
    }

    @Override
    public int getSize() {
        return elements.size();
    }

    @Override
    public E getElementAt(int index) {
        return elements.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener listener) {
        // No-op
    }

    @Override
    public void removeListDataListener(ListDataListener listener) {
        // No-op
    }
}
