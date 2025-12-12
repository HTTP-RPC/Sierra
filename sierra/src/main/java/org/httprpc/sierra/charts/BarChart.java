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
import java.util.List;
import java.util.TreeSet;

import static org.httprpc.kilo.util.Collections.*;

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

    // TODO
    private List<Rectangle2D.Double> barRectangles = null;

    private RowPanel legendPanel = new RowPanel();

    @Override
    protected void validate() {
        horizontalGridLines.clear();
        verticalGridLines.clear();

        legendPanel.removeAll();

        legendPanel.setSpacing(16);
        legendPanel.setComponentOrientation(getComponentOrientation());

        var dataSets = getDataSets();

        var n = dataSets.size();

        var keys = new TreeSet<K>();

        var legendFont = getLegendFont();

        for (var i = 0; i < n; i++) {
            var dataSet = dataSets.get(i);

            for (var dataPoint : dataSet.getDataPoints()) {
                keys.add(dataPoint.getKey());
            }

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

        var rangeLabelCount = 5; // TODO

        var horizontalGridStrokeWidth = getHorizontalGridLineStroke().getLineWidth();

        var horizontalGridLineSpacing = (chartHeight - horizontalGridStrokeWidth) / (rangeLabelCount - 1);

        var y = horizontalGridStrokeWidth / 2.0;

        for (var i = 0; i < rangeLabelCount; i++) {
            horizontalGridLines.add(new Line2D.Double(0.0, y, chartWidth, y));

            y += horizontalGridLineSpacing;
        }

        var verticalGridLineStrokeWidth = getVerticalGridLineStroke().getLineWidth();

        var verticalGridLineSpacing = (chartWidth - verticalGridLineStrokeWidth) / keyCount;
        var verticalGridLineCount = keyCount + 1;

        var x = verticalGridLineStrokeWidth / 2.0;

        for (var i = 0; i < verticalGridLineCount; i++) {
            verticalGridLines.add(new Line2D.Double(x, 0.0, x, chartHeight));

            x += verticalGridLineSpacing;
        }

        // TODO Lay out bars
    }

    @Override
    protected void draw(Graphics2D graphics) {
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
