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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.SortedMap;

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
public class DataSet<K extends Comparable<? super K>, V> {
    private String label;
    private Color color;

    private BasicStroke stroke = defaultStroke;

    private SortedMap<K, V> dataPoints = sortedMapOf();

    private static final BasicStroke defaultStroke = new BasicStroke(1.25f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

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
     * The data set's stroke.
     */
    public BasicStroke getStroke() {
        return stroke;
    }

    /**
     * Sets the data set's stroke.
     *
     * @param stroke
     * The data set's stroke.
     */
    public void setStroke(BasicStroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException();
        }

        this.stroke = stroke;
    }

    /**
     * Returns the data set's data points.
     *
     * @return
     * The data set's data points.
     */
    public SortedMap<K, V> getDataPoints() {
        return dataPoints;
    }

    /**
     * Sets the data set's data points.
     *
     * @param dataPoints
     * The data set's data points.
     */
    public void setDataPoints(SortedMap<K, V> dataPoints) {
        if (dataPoints == null) {
            throw new IllegalArgumentException();
        }

        this.dataPoints = dataPoints;
    }
}
