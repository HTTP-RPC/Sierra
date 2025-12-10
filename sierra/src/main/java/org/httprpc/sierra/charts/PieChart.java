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
    private K key;

    private int outerRadius;
    private int innerRadius;

    /**
     * Constructs a new pie chart.
     *
     * @param key
     * The chart key.
     *
     * @param outerRadius
     * The chart's outer radius.
     *
     * @param innerRadius
     * The chart's inner radius.
     */
    public PieChart(K key, int outerRadius, int innerRadius) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        if (innerRadius < 0 || innerRadius > outerRadius) {
            throw new IllegalArgumentException();
        }

        this.key = key;

        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
    }

    /**
     * Returns the chart key.
     *
     * @return
     * The chart key.
     */
    public K getKey() {
        return key;
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
     * Draws the pie chart.
     * {@inheritDoc}
     */
    @Override
    public void draw(Graphics2D graphics, int width, int height) {
        // TODO
        graphics.setColor(Color.RED);
        graphics.drawRect(0, 0, width, height);
    }
}
