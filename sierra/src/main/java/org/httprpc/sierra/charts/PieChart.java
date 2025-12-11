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
    @Override
    protected void draw(Graphics2D graphics) {
        var width = getWidth();
        var height = getHeight();

        graphics.translate(0, height);
        graphics.scale(1.0, -1.0);

        graphics.setColor(Color.RED);

        graphics.drawLine(0, 0, width / 2, height);
        graphics.drawLine(width / 2, height, width, 0);
    }
}
