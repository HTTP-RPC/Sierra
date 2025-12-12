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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Bar chart.
 */
public class BarChart<K extends Comparable<K>, V extends Number> extends Chart<K, V> {
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

    private List<Line2D.Double> horizontalGridLines = listOf();
    private List<Line2D.Double> verticalGridLines = listOf();

    private List<List<Rectangle2D.Double>> barRectangles = listOf();

    private RowPanel legendPanel = new RowPanel();

    @Override
    protected void validate() {
        horizontalGridLines.clear();
        verticalGridLines.clear();

        barRectangles.clear();

        legendPanel.removeAll();

        legendPanel.setSpacing(16);
        legendPanel.setComponentOrientation(getComponentOrientation());

        var dataSets = getDataSets();

        var n = dataSets.size();

        var keys = new TreeSet<K>();
        var dataSetValueMaps = new ArrayList<Map<K, Double>>(n);

        var maximum = 0.0;
        var minimum = 0.0;

        var legendFont = getLegendFont();

        for (var i = 0; i < n; i++) {
            var dataSet = dataSets.get(i);

            var values = new TreeMap<K, Double>();

            for (var dataPoint : dataSet.getDataPoints()) {
                var key = dataPoint.getKey();

                keys.add(key);

                var value = coalesce(map(dataPoint.getValue(), Number::doubleValue), () -> 0.0);

                values.put(key, value);

                maximum = Math.max(maximum, value);
                minimum = Math.min(minimum, value);
            }

            dataSetValueMaps.add(values);

            var legendLabel = new JLabel(dataSet.getLabel(), new LegendIcon(dataSet), SwingConstants.CENTER);

            legendLabel.setFont(legendFont);

            legendPanel.add(legendLabel);
        }

        var keyCount = keys.size();

        if (keyCount == 0) {
            return;
        }

        var width = getWidth();
        var height = getHeight();

        var legendSize = legendPanel.getPreferredSize();

        legendPanel.setLocation(width / 2 - legendSize.width / 2, height - legendSize.height);
        legendPanel.setSize(legendSize);

        legendPanel.doLayout();

        var chartHeight = (double)Math.max(height - (legendSize.height + 16), 0);
        var chartWidth = (double)width;

        var rangeLabelCount = getRangeLabelCount();

        var horizontalGridStrokeWidth = getHorizontalGridLineStroke().getLineWidth();

        var horizontalGridLineSpacing = (chartHeight - horizontalGridStrokeWidth) / (rangeLabelCount - 1);

        var gridY = horizontalGridStrokeWidth / 2.0;

        for (var i = 0; i < rangeLabelCount; i++) {
            horizontalGridLines.add(new Line2D.Double(0.0, gridY, chartWidth, gridY));

            gridY += horizontalGridLineSpacing;
        }

        var verticalGridLineStrokeWidth = getVerticalGridLineStroke().getLineWidth();

        var verticalGridLineSpacing = (chartWidth - verticalGridLineStrokeWidth) / keyCount;
        var verticalGridLineCount = keyCount + 1;

        var gridX = verticalGridLineStrokeWidth / 2.0;

        for (var i = 0; i < verticalGridLineCount; i++) {
            verticalGridLines.add(new Line2D.Double(gridX, 0.0, gridX, chartHeight));

            gridX += verticalGridLineSpacing;
        }

        var barSpacing = verticalGridLineSpacing * 0.05;
        var barWidth = (verticalGridLineSpacing - (verticalGridLineStrokeWidth + barSpacing * (keyCount + 1))) / keyCount;

        var barX = (double)verticalGridLineStrokeWidth;

        var range = maximum - minimum;

        if (range == 0.0) {
            return;
        }

        var scale = chartHeight / range;

        var zeroY = maximum * scale;

        for (var key : keys) {
            var dataSetBarRectangles = new ArrayList<Rectangle2D.Double>();

            for (var dataSetValueMap : dataSetValueMaps) {
                barX += barSpacing;

                var value = dataSetValueMap.get(key);

                var barY = zeroY;
                var barHeight = Math.abs(value) * scale;

                if (value > 0.0) {
                    barY -= barHeight;
                }

                dataSetBarRectangles.add(new Rectangle2D.Double(barX, barY, barWidth, barHeight));

                barX += barWidth;
            }

            barRectangles.add(dataSetBarRectangles);

            barX += barSpacing + verticalGridLineStrokeWidth;
        }
    }

    @Override
    protected void draw(Graphics2D graphics) {
        var dataSets = getDataSets();

        for (var dataSetBarRectangles : barRectangles) {
            var i = 0;

            for (var barRectangle : dataSetBarRectangles) {
                var dataSet = dataSets.get(i++);

                graphics.setColor(dataSet.getColor());
                graphics.setStroke(dataSet.getStroke());

                graphics.fill(barRectangle);
            }
        }

        var showHorizontalGridLines = getShowHorizontalGridLines();

        graphics.setColor(getHorizontalGridLineColor());
        graphics.setStroke(getHorizontalGridLineStroke());

        for (var horizontalGridLine : horizontalGridLines) {
            if (showHorizontalGridLines) {
                graphics.draw(horizontalGridLine);
            }

            // TODO Draw label
        }

        var showVerticalGridLines = getShowVerticalGridLines();

        graphics.setColor(getVerticalGridLineColor());
        graphics.setStroke(getVerticalGridLineStroke());

        for (var verticalGridLine : verticalGridLines) {
            if (showVerticalGridLines) {
                graphics.draw(verticalGridLine);
            }

            // TODO Draw label
        }

        paintComponent(graphics, legendPanel);
    }
}
