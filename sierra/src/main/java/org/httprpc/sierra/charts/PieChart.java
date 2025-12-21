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

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Pie chart.
 */
public class PieChart<K extends Comparable<? super K>, V extends Number> extends Chart<K, V> {
    private static class LegendIcon implements Icon {
        DataSet<?, ?> dataSet;

        Ellipse2D.Double shape = new Ellipse2D.Double();

        static final int SIZE = 12;

        LegendIcon(DataSet<?, ?> dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            paintIcon((Graphics2D)graphics, x, y);
        }

        void paintIcon(Graphics2D graphics, int x, int y) {
            shape.setFrame(x, y, SIZE, SIZE);

            graphics.setColor(dataSet.getColor());
            graphics.fill(shape);
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }

    private List<Arc2D.Double> sliceArcs = listOf();

    @Override
    protected void validate(Graphics2D graphics) {
        sliceArcs.clear();

        var dataSets = getDataSets();

        var n = dataSets.size();

        var dataSetValues = new ArrayList<Double>(n);

        var total = 0.0;

        for (var i = 0; i < n; i++) {
            var dataSet = dataSets.get(i);

            dataSetValues.add(0.0);

            for (var entry : dataSet.getDataPoints().entrySet()) {
                var value = coalesce(map(entry.getValue(), Number::doubleValue), () -> 0.0);

                if (value < 0.0) {
                    throw new UnsupportedOperationException("Negative value in data set.");
                }

                dataSetValues.set(i, dataSetValues.get(i) + value);

                total += value;
            }
        }

        if (total == 0.0) {
            return;
        }

        var width = getWidth();
        var height = getHeight();

        var pieBounds = new Rectangle2D.Double(width / 2.0 - height / 2.0, 0.0, height, height);

        var start = 90.0;

        for (var i = 0; i < n; i++) {
            var extent = -360.0 * (dataSetValues.get(i) / total);

            sliceArcs.add(new Arc2D.Double(pieBounds, start, extent, Arc2D.PIE));

            start += extent;
        }
    }

    @Override
    protected void draw(Graphics2D graphics) {
        var i = 0;

        for (var dataSet : getDataSets()) {
            var sliceArc = sliceArcs.get(i);

            graphics.setColor(dataSet.getColor());
            graphics.fill(sliceArc);

            i++;
        }
    }
}
