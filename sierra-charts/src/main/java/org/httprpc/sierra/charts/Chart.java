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
import java.awt.Graphics2D;
import java.awt.Stroke;
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

    private Color horizontalGridColor = null;
    private Stroke horizontalGridStroke = null;

    private Color verticalGridColor = null;
    private Stroke verticalGridStroke = null;

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
     * Returns the chart's horizontal grid color.
     *
     * @return
     * The chart's horizontal grid color.
     */
    public Color getHorizontalGridColor() {
        return horizontalGridColor;
    }

    /**
     * Sets the chart's horizontal grid color.
     *
     * @param horizontalGridColor
     * The chart's horizontal grid color.
     */
    public void setHorizontalGridColor(Color horizontalGridColor) {
        this.horizontalGridColor = horizontalGridColor;
    }

    /**
     * Returns the chart's horizontal grid stroke.
     *
     * @return
     * The chart's horizontal grid stroke.
     */
    public Stroke getHorizontalGridStroke() {
        return horizontalGridStroke;
    }

    /**
     * Sets the chart's horizontal grid stroke.
     *
     * @param horizontalGridStroke
     * The chart's horizontal grid stroke.
     */
    public void setHorizontalGridStroke(Stroke horizontalGridStroke) {
        this.horizontalGridStroke = horizontalGridStroke;
    }

    /**
     * Returns the chart's vertical grid color.
     *
     * @return
     * The chart's vertical grid color.
     */
    public Color getVerticalGridColor() {
        return verticalGridColor;
    }

    /**
     * Sets the chart's vertical grid color.
     *
     * @param verticalGridColor
     * The chart's vertical grid color.
     */
    public void setVerticalGridColor(Color verticalGridColor) {
        this.verticalGridColor = verticalGridColor;
    }

    /**
     * Returns the chart's vertical grid stroke.
     *
     * @return
     * The chart's vertical grid stroke.
     */
    public Stroke getVerticalGridStroke() {
        return verticalGridStroke;
    }

    /**
     * Sets the chart's vertical grid stroke.
     *
     * @param verticalGridStroke
     * The chart's vertical grid stroke.
     */
    public void setVerticalGridStroke(Stroke verticalGridStroke) {
        this.verticalGridStroke = verticalGridStroke;
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
