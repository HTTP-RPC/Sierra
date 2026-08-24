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

import org.httprpc.kilo.beans.BeanAdapter;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Iterables.*;

/**
 * Basic table model.
 *
 * @param <R>
 * The row type.
 */
public class BasicTableModel<R> implements TableModel {
    private Map<String, BeanAdapter.Property> properties;

    private List<BeanAdapter> rows;
    private List<String> columnNames;

    private ResourceBundle resourceBundle;

    /**
     * Constructs a new basic table model.
     *
     * @param type
     * The row type.
     *
     * @param rows
     * The row values.
     *
     * @param columnNames
     * The column names.
     *
     * @param resourceBundle
     * The resource bundle, or {@code null} for no resource bundle.
     */
    public BasicTableModel(Class<R> type, List<? extends R> rows, List<String> columnNames, ResourceBundle resourceBundle) {
        if (type == null) {
            throw new IllegalArgumentException();
        }

        properties = BeanAdapter.getProperties(type);

        if (rows == null || columnNames == null) {
            throw new IllegalArgumentException();
        }

        this.rows = listOf(mapAll(rows, BeanAdapter::new));
        this.columnNames = columnNames;

        this.resourceBundle = resourceBundle;
    }

    /**
     * Returns the row at a given index.
     *
     * @param index
     * The row index.
     *
     * @return
     * The row at the given index.
     */
    @SuppressWarnings("unchecked")
    public R getRow(int index) {
        return (R)rows.get(index).getBean();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        var columnName = columnNames.get(columnIndex);

        return (resourceBundle == null) ? columnName : resourceBundle.getString(columnName);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return properties.get(columnNames.get(columnIndex)).getAccessor().getReturnType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).get(columnNames.get(columnIndex));
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTableModelListener(TableModelListener listener) {
        // No-op
    }

    @Override
    public void removeTableModelListener(TableModelListener listener) {
        // No-op
    }
}
