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

/**
 * Pie chart.
 */
public class PieChart<K, V extends Number> extends Chart<K, V> {
    private int outerRadius;

    private int innerRadius = 0;
    private int columns = 1;

    /**
     * Constructs a new pie chart.
     *
     * @param outerRadius
     * The chart's outer radius.
     */
    public PieChart(int outerRadius) {
        if (outerRadius < 0) {
            throw new IllegalArgumentException();
        }

        this.outerRadius = outerRadius;
    }

    /**
     * Returns the chart's outer radius.
     *
     * @return
     * The chart's outer radius.
     */
    public int getOuterRadius() {
        return outerRadius;
    }

    /**
     * Returns the chart's inner radius.
     *
     * @return
     * The chart's inner radius.
     */
    public int getInnerRadius() {
        return innerRadius;
    }

    /**
     * Sets the chart's inner radius.
     *
     * @param innerRadius
     * The chart's inner radius.
     */
    public void setInnerRadius(int innerRadius) {
        if (innerRadius < 0 || innerRadius > outerRadius) {
            throw new IllegalArgumentException();
        }

        this.innerRadius = innerRadius;
    }

    /**
     * Returns the column count.
     *
     * @return
     * The column count.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Sets the column count.
     *
     * @param columns
     * The column count.
     */
    public void setColumns(int columns) {
        if (columns < 1) {
            throw new IllegalArgumentException();
        }

        this.columns = columns;
    }

    /**
     * Draws the pie chart.
     * {@inheritDoc}
     */
    @Override
    public void draw(Graphics2D graphics) {
        // TODO
        graphics.setColor(Color.RED);

        var width = getWidth();
        var height = getHeight();

        graphics.drawRect(0, 0, width, height);
    }
}
