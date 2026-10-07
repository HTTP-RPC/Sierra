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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;
import java.util.function.Function;

/**
 * Basic list cell renderer.
 *
 * @param <E>
 * The element type.
 */
public class BasicListCellRenderer<E> extends DefaultListCellRenderer {
    private Function<? super E, String> labelMapper;

    /**
     * Constructs a new basic list cell renderer.
     *
     * @param labelMapper
     * The label mapper.
     */
    public BasicListCellRenderer(Function<? super E, String> labelMapper) {
        if (labelMapper == null) {
            throw new IllegalArgumentException();
        }

        this.labelMapper = labelMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getListCellRendererComponent(JList<?> list,
        Object value, int index,
        boolean selected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list,
            (value == null) ? null : labelMapper.apply((E)value), index,
            selected, cellHasFocus);
    }
}
