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

import org.httprpc.sierra.Orientation;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Bar chart.
 */
public class BarChart<K, V extends Number> extends Chart<K, V> {
    private Orientation orientation;
    private boolean stacked;

    /**
     * Constructs a new bar chart.
     */
    public BarChart() {
        this(Orientation.VERTICAL);
    }

    /**
     * Constructs a new bar chart.
     *
     * @param orientation
     * The chart's orientation.
     */
    public BarChart(Orientation orientation) {
        this(orientation, false);
    }

    /**
     * Constructs a new bar chart.
     *
     * @param orientation
     * The chart's orientation.
     *
     * @param stacked
     * {@code true} to stack the chart's bars; {@code false}, otherwise.
     */
    public BarChart(Orientation orientation, boolean stacked) {
        if (orientation == null) {
            throw new IllegalArgumentException();
        }

        this.orientation = orientation;
        this.stacked = stacked;
    }

    /**
     * Returns the chart's orientation.
     *
     * @return
     * The chart's orientation.
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Indicates that the chart's bars are stacked.
     *
     * @return
     * {@code true} if the chart's bars are stacked; {@code false}, otherwise.
     */
    public boolean isStacked() {
        return stacked;
    }

    @Override
    protected void draw(Graphics2D graphics) {
        // TODO
        graphics.setColor(Color.GREEN);
        graphics.drawRect(0, 0, getWidth(), getHeight());
    }
}
