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

import java.awt.Color;
import java.awt.Stroke;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;

/**
 * Represents a data set.
 *
 * @param <K>
 * The key type.
 *
 * @param <V>
 * The value type.
 */
public class DataSet<K, V> {
    private String label;
    private Color color;

    private Stroke stroke = null;

    private List<DataPoint<K, V>> dataPoints = listOf();

    /**
     * Constructs a new data set.
     *
     * @param label
     * The data set's label.
     *
     * @param color
     * The data set's color.
     */
    public DataSet(String label, Color color) {
        if (label == null || color == null) {
            throw new IllegalArgumentException();
        }

        this.label = label;
        this.color = color;
    }

    /**
     * Returns the data set's label.
     *
     * @return
     * The data set's label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the data set's color.
     *
     * @return
     * The data set's color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the data set's stroke.
     *
     * @return
     * The data set's stroke, or {@code null} if no stroke is set.
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * Sets the data set's stroke.
     *
     * @param stroke
     * The data set's stroke, or {@code null} for no stroke.
     */
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    /**
     * Returns the data set's data points.
     *
     * @return
     * The data set's data points.
     */
    public List<DataPoint<K, V>> getDataPoints() {
        return dataPoints;
    }

    /**
     * Sets the data set's data points.
     *
     * @param dataPoints
     * The data set's data points.
     */
    public void setDataPoints(List<DataPoint<K, V>> dataPoints) {
        if (dataPoints == null) {
            throw new IllegalArgumentException();
        }

        this.dataPoints = dataPoints;
    }
}
