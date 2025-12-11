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

    private DataPoint() {
        key = null;
        value = null;
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
     * The data point's label, or {@code null} if no label is set.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the data point's label.
     *
     * @param label
     * The data point's label, or {@code null} for no label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the data point's icon.
     *
     * @return
     * The data point's icon, or {@code null} if no icon is set.
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Sets the data point's icon.
     *
     * @param icon
     * The data point's icon, or {@code null} for no icon.
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    /**
     * Creates a domain marker.
     *
     * @param <K>
     * The key type.
     *
     * @param key
     * The data point key.
     *
     * @return
     * A new domain marker.
     */
    public static <K> DataPoint<K, Void> domainMarker(K key) {
        var domainMarker = new DataPoint<K, Void>();

        domainMarker.key = key;

        return domainMarker;
    }

    /**
     * Creates a new range marker.
     *
     * @param <V>
     * The value type.
     *
     * @param value
     * The data point value.
     *
     * @return
     * A new range marker.
     */
    public static <V> DataPoint<Void, V> rangeMarker(V value) {
        var rangeMarker = new DataPoint<Void, V>();

        rangeMarker.value = value;

        return rangeMarker;
    }
}
