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

/**
 * Bar chart.
 */
public class BarChart<K, V extends Number> extends Chart<K, V> {
    private boolean stacked;

    /**
     * Constructs a new bar chart.
     *
     * @param stacked
     * {@code true} to stack the bars; {@code false}, otherwise.
     */
    public BarChart(boolean stacked) {
        this.stacked = stacked;
    }

    /**
     * Indicates that the bars are stacked.
     *
     * @return
     * {@code true} if the bars are stacked; {@code false}, otherwise.
     */
    public boolean isStacked() {
        return stacked;
    }

    @Override
    public void draw(Graphics2D graphics) {
        // TODO
    }
}
