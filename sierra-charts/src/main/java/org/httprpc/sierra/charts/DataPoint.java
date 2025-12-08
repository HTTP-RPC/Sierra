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

package org.httprpc.sierra.charts;

import javax.swing.Icon;
import java.awt.Color;

/**
 * Represents a data point.
 *
 * @param <K>
 * The key type.
 *
 * @param <V>
 * The value type.
 */
public class DataPoint<K, V> {
    private K key;
    private V value;

    private String label = null;
    private Icon icon = null;
    private Color color = null;

    /**
     * Constructs a new data point.
     *
     * @param key
     * The data point's key.
     *
     * @param value
     * The data point's value.
     */
    public DataPoint(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }

        this.key = key;
        this.value = value;
    }

    /**
     * Returns the data point's key.
     *
     * @return
     * The data point's key.
     */
    public K getKey() {
        return key;
    }

    /**
     * Returns the data point's value.
     *
     * @return
     * The data point's value.
     */
    public V getValue() {
        return value;
    }

    /**
     * Returns the data point's label.
     *
     * @return
     * The data point's label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the data point's label.
     *
     * @param label
     * The data point's label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the data point's icon.
     *
     * @return
     * The data point's icon.
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Sets the data point's icon.
     *
     * @param icon
     * The data point's icon.
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    /**
     * Returns the data point's color.
     *
     * @return
     * The data point's color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the data point's color.
     *
     * @param color
     * The data point's color.
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
