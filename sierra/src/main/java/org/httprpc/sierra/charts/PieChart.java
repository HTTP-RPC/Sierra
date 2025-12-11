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

import org.httprpc.sierra.RowPanel;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Pie chart.
 */
public class PieChart<K, V extends Number> extends Chart<K, V> {
    private static class LegendIcon implements Icon {
        Color color;

        Ellipse2D.Float shape = new Ellipse2D.Float();

        static final int DIAMETER = 12;

        LegendIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            paintIcon((Graphics2D)graphics, x, y);
        }

        void paintIcon(Graphics2D graphics, int x, int y) {
            shape.setFrame(x, y, DIAMETER, DIAMETER);

            graphics.setColor(color);
            graphics.fill(shape);
        }

        @Override
        public int getIconWidth() {
            return DIAMETER;
        }

        @Override
        public int getIconHeight() {
            return DIAMETER;
        }
    }

    private Area pieShape = new Area();

    private RowPanel legendPanel = new RowPanel();

    @Override
    protected void validate() {
        pieShape.reset();

        legendPanel.removeAll();

        var dataSets = getDataSets();

        var n = dataSets.size();

        var total = 0.0;

        var dataSetValues = new ArrayList<Double>(n);

        for (var i = 0; i < n; i++) {
            var dataSet = dataSets.get(i);

            dataSetValues.add(0.0);

            for (var dataPoint : dataSet.getDataPoints()) {
                var value = dataPoint.getValue().doubleValue();

                total += value;

                dataSetValues.set(i, dataSetValues.get(i) + value);
            }

            legendPanel.add(new JLabel(dataSet.getLabel(), new LegendIcon(dataSet.getColor()), SwingConstants.CENTER));
        }

        var width = getWidth();
        var height = getHeight();

        var legendSize = legendPanel.getPreferredSize();

        legendPanel.setLocation(width / 2 - legendSize.width / 2, height - legendSize.height);
        legendPanel.setSize(legendSize);

        legendPanel.setSpacing(16);

        legendPanel.setComponentOrientation(getComponentOrientation());

        legendPanel.doLayout();

        for (var i = 0; i < n; i++) {
            var percentage = dataSetValues.get(i) / total;

            // TODO
        }
    }

    @Override
    protected void draw(Graphics2D graphics) {
        drawShape(graphics, pieShape);

        paintComponent(graphics, legendPanel);
    }
}
