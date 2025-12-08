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
    private String name;
    private String label;

    private Color color = null;
    private Chart.Axis axis = null;

    private List<DataPoint<K, V>> dataPoints = listOf();

    /**
     * Constructs a new data set.
     *
     * @param name
     * The data set's name.
     *
     * @param label
     * The data set's label.
     */
    public DataSet(String name, String label) {
        if (name == null || label == null) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.label = label;
    }

    /**
     * Returns the data set's name.
     *
     * @return
     * The data set's name.
     */
    public String getName() {
        return name;
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
     * Sets the data set's color.
     *
     * @param color
     * The data set's color.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the data set's axis.
     *
     * @return
     * The data set's axis.
     */
    public Chart.Axis getAxis() {
        return axis;
    }

    /**
     * Sets the data set's axis.
     *
     * @param axis
     * The data set's axis.
     */
    public void setAxis(Chart.Axis axis) {
        this.axis = axis;
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
        this.dataPoints = dataPoints;
    }
}
