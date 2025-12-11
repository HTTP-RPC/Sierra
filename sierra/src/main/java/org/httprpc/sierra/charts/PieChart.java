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

    private RowPanel legendPanel = new RowPanel();

    @Override
    protected void validate() {
        var dataSets = getDataSets();

        var n = dataSets.size();

        var total = 0.0;

        var dataSetTotals = new ArrayList<Double>(n);

        for (var i = 0; i < n; i++) {
            dataSetTotals.add(0.0);

            for (var dataPoint : dataSets.get(i).getDataPoints()) {
                var value = dataPoint.getValue().doubleValue();

                total += value;

                dataSetTotals.set(i, dataSetTotals.get(i) + value);
            }
        }

        legendPanel.removeAll();

        legendPanel.setSpacing(16);

        for (var i = 0; i < n; i++) {
            var dataSet = dataSets.get(i);

            var percentage = dataSetTotals.get(i) / total;

            // TODO
            System.out.printf("%s %,.1f%%\n", dataSet.getLabel(), percentage * 100.0);

            legendPanel.add(new JLabel(dataSet.getLabel(), new LegendIcon(dataSet.getColor()), SwingConstants.CENTER));
        }

        legendPanel.setSize(legendPanel.getPreferredSize());
        legendPanel.setComponentOrientation(getComponentOrientation());

        legendPanel.doLayout();
    }

    @Override
    protected void draw(Graphics2D graphics) {
        drawLegend(graphics);
    }

    protected void drawLegend(Graphics2D graphics) {
        var width = getWidth();
        var height = getHeight();

        var legendSize = legendPanel.getSize();

        graphics = (Graphics2D)graphics.create();

        graphics.translate(width / 2 - legendSize.width / 2, height - legendSize.height);

        legendPanel.paint(graphics);
    }
}
