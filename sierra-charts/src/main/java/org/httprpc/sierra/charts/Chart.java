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

import java.awt.Graphics2D;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;

/**
 * Abstract base class for charts.
 *
 * @param <K>
 * The key type.
 *
 * @param <V>
 * The value type.
 */
public abstract class Chart<K, V> {
    /**
     * Chart orientation options.
     */
    public enum Orientation {
        /**
         * Horizontal orientation.
         */
        HORIZONTAL,

        /**
         * Vertical orientation.
         */
        VERTICAL
    }

    /**
     * Chart axis options.
     */
    public enum Axis {
        /**
         * Leading axis.
         */
        LEADING,

        /**
         * Trailing axis.
         */
        TRAILING
    }

    private int width = 0;
    private int height = 0;

    private List<DataSet<K, V>> dataSets = listOf();

    /**
     * Returns the chart's width.
     *
     * @return
     * The chart width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the chart's height.
     *
     * @return
     * The chart height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the chart's size.
     *
     * @param width
     * The chart width.
     *
     * @param height
     * The chart height.
     */
    public void setSize(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }

        this.width = width;
        this.height = height;
    }

    /**
     * Returns the chart's data sets.
     *
     * @return
     * The chart's data sets.
     */
    public List<DataSet<K, V>> getDataSets() {
        return dataSets;
    }

    /**
     * Sets the chart's data sets.
     *
     * @param dataSets
     * The chart's data sets.
     */
    public void setDataSets(List<DataSet<K, V>> dataSets) {
        if (dataSets == null) {
            throw new IllegalArgumentException();
        }

        this.dataSets = dataSets;
    }

    /**
     * Draws the chart.
     *
     * @param graphics
     * The graphics context in which the chart will be drawn.
     */
    public abstract void draw(Graphics2D graphics);
}
