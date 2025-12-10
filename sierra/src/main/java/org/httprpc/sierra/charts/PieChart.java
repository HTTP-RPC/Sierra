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
    private boolean ring;

    /**
     * Constructs a new pie chart.
     */
    public PieChart() {
        this(false);
    }

    /**
     * Constructs a new pie chart.
     *
     * @param ring
     * Indicates that the chart will be displayed as a ring.
     */
    public PieChart(boolean ring) {
        this.ring = ring;
    }

    /**
     * Indicates that the chart will be displayed as a ring.
     *
     * @return
     * {@code true} if the chart's will be displayed as a ring; {@code false},
     * otherwise.
     */
    public boolean isRing() {
        return ring;
    }

    @Override
    public void draw(Graphics2D graphics, int width, int height) {
        // TODO
        graphics.setColor(Color.RED);
        graphics.drawRect(0, 0, width, height);
    }
}
