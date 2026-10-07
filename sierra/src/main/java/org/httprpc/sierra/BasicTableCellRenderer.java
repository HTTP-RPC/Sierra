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

import javax.swing.table.DefaultTableCellRenderer;
import java.util.function.Function;

import static org.httprpc.kilo.util.Optionals.*;

/**
 * Basic table cell renderer.
 *
 * @param <V>
 * The value type.
 */
public class BasicTableCellRenderer<V> extends DefaultTableCellRenderer {
    private Function<? super V, String> valueMapper;

    /**
     * Constructs a new basic table cell renderer.
     *
     * @param valueMapper
     * The value mapper.
     */
    public BasicTableCellRenderer(Function<? super V, String> valueMapper) {
        if (valueMapper == null) {
            throw new IllegalArgumentException();
        }

        this.valueMapper = valueMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Object value) {
        setText(map((V)value, valueMapper));
    }
}
