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

import org.httprpc.sierra.HorizontalAlignment;
import org.httprpc.sierra.RowPanel;
import org.httprpc.sierra.TextPane;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
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

    private List<TextPane> domainLabelTextPanes = listOf();
    private List<TextPane> rangeLabelTextPanes = listOf();

    private List<List<Shape>> barShapes = listOf();

    private List<Line2D.Double> rangeMarkerLines = listOf();
    private List<TextPane> rangeMarkerTextPanes = listOf();

    private RowPanel legendPanel = new RowPanel();

    private static final int DOMAIN_LABEL_SPACING = 4;
    private static final int RANGE_LABEL_SPACING = 4;

    private static final int LEGEND_SPACING = 16;

    @Override
    protected void validate() {
        horizontalGridLines.clear();
        verticalGridLines.clear();

        domainLabelTextPanes.clear();
        rangeLabelTextPanes.clear();

        barShapes.clear();

        rangeMarkerLines.clear();
        rangeMarkerTextPanes.clear();

        legendPanel.removeAll();

        legendPanel.setSpacing(16);
        legendPanel.setComponentOrientation(getComponentOrientation());

        var dataSets = getDataSets();

        var keys = new TreeSet<K>();

        var minimum = 0.0;
        var maximum = 0.0;

        var legendColor = getLegendColor();
        var legendFont = getLegendFont();

        for (var dataSet : dataSets) {
            for (var entry : dataSet.getDataPoints().entrySet()) {
                var key = entry.getKey();

                keys.add(key);

                var value = coalesce(map(entry.getValue(), Number::doubleValue), () -> 0.0);

                minimum = Math.min(minimum, value);
                maximum = Math.max(maximum, value);
            }

            var legendLabel = new JLabel(dataSet.getLabel(), new LegendIcon(dataSet), SwingConstants.CENTER);

            legendLabel.setForeground(legendColor);
            legendLabel.setFont(legendFont);

            legendPanel.add(legendLabel);
        }

        var keyCount = keys.size();

        if (keyCount == 0 || minimum == 0.0 && maximum == 0.0) {
            return;
        }

        var width = getWidth();
        var height = getHeight();

        var legendSize = legendPanel.getPreferredSize();

        legendPanel.setLocation(width / 2 - legendSize.width / 2, height - legendSize.height);
        legendPanel.setSize(legendSize);

        legendPanel.doLayout();

        var domainLabelTransform = getDomainLabelTransform();
        var domainLabelFont = getDomainLabelFont();

        var domainLabelHeight = 0.0;

        for (var key : keys) {
            var label = domainLabelTransform.apply(key);

            var textPane = new TextPane(label);

            textPane.setFont(domainLabelFont);
            textPane.setHorizontalAlignment(HorizontalAlignment.CENTER);
            textPane.setSize(textPane.getPreferredSize());

            domainLabelHeight = Math.max(domainLabelHeight, textPane.getHeight());

            domainLabelTextPanes.add(textPane);
        }

        var rangeLabelCount = getRangeLabelCount();

        var rangeStep = Math.abs(maximum - minimum) / (rangeLabelCount - 1);

        var rangeLabelTransform = getRangeLabelTransform();
        var rangeLabelFont = getRangeLabelFont();

        var rangeLabelWidth = 0.0;

        for (var i = 0; i < rangeLabelCount; i++) {
            var label = rangeLabelTransform.apply(minimum + rangeStep * i);

            var textPane = new TextPane(label);

            textPane.setFont(rangeLabelFont);
            textPane.setHorizontalAlignment(HorizontalAlignment.TRAILING);
            textPane.setSize(textPane.getPreferredSize());

            rangeLabelWidth = Math.max(rangeLabelWidth, textPane.getWidth());

            rangeLabelTextPanes.add(textPane);
        }

        var rangeLabelOffset = rangeLabelWidth + RANGE_LABEL_SPACING;

        var chartWidth = (double)width - rangeLabelOffset;
        var chartHeight = Math.max(height - (domainLabelHeight + DOMAIN_LABEL_SPACING + legendSize.height + LEGEND_SPACING), 0);

        var horizontalGridStrokeWidth = getHorizontalGridLineStroke().getLineWidth();

        var horizontalGridLineSpacing = (chartHeight - horizontalGridStrokeWidth) / (rangeLabelCount - 1);

        var gridY = horizontalGridStrokeWidth / 2.0;

        for (var i = 0; i < rangeLabelCount; i++) {
            horizontalGridLines.add(new Line2D.Double(rangeLabelOffset, gridY, rangeLabelOffset + chartWidth, gridY));

            gridY += horizontalGridLineSpacing;
        }

        var verticalGridLineStrokeWidth = getVerticalGridLineStroke().getLineWidth();

        var verticalGridLineSpacing = (chartWidth - verticalGridLineStrokeWidth) / keyCount;
        var verticalGridLineCount = keyCount + 1;

        var gridX = rangeLabelOffset + verticalGridLineStrokeWidth / 2.0;

        for (var i = 0; i < verticalGridLineCount; i++) {
            verticalGridLines.add(new Line2D.Double(gridX, 0.0, gridX, chartHeight));

            gridX += verticalGridLineSpacing;
        }

        var domainLabelX = rangeLabelOffset;

        for (var textPane : domainLabelTextPanes) {
            var size = textPane.getSize();

            textPane.setBounds((int)domainLabelX, (int)chartHeight + DOMAIN_LABEL_SPACING, (int)verticalGridLineSpacing, size.height);
            textPane.doLayout();

            domainLabelX += verticalGridLineSpacing;
        }

        var rangeLabelY = chartHeight - horizontalGridStrokeWidth / 2.0;

        for (var i = 0; i < rangeLabelCount; i++) {
            var textPane = rangeLabelTextPanes.get(i);

            var size = textPane.getSize();

            int y;
            if (i == 0) {
                y = (int)rangeLabelY - size.height;
            } else if (i < rangeLabelCount - 1) {
                y = (int)rangeLabelY - size.height / 2;
            } else {
                y = (int)rangeLabelY;
            }

            textPane.setBounds(0, y, (int)rangeLabelWidth, size.height);
            textPane.doLayout();

            rangeLabelY -= horizontalGridLineSpacing;
        }

        var n = dataSets.size();

        var barSpacing = verticalGridLineSpacing * 0.04;
        var barWidth = (verticalGridLineSpacing - (verticalGridLineStrokeWidth + barSpacing * (n + 1))) / n;

        var barX = rangeLabelOffset + verticalGridLineStrokeWidth;

        var scale = chartHeight / (maximum - minimum);

        var zeroY = maximum * scale;

        for (var key : keys) {
            var dataSetBarShapes = new ArrayList<Shape>();

            for (var dataSet : dataSets) {
                barX += barSpacing;

                var value = coalesce(map(dataSet.getDataPoints().get(key), Number::doubleValue), () -> 0.0);

                var barY = zeroY;
                var barHeight = Math.abs(value) * scale - horizontalGridStrokeWidth;

                if (value > 0.0) {
                    barY -= barHeight + horizontalGridStrokeWidth / 2;
                } else {
                    barY += horizontalGridStrokeWidth / 2;
                }

                var arc = barWidth * 0.3;

                var barRectangle = new RoundRectangle2D.Double(barX, barY, barWidth, barHeight, arc, arc);

                var baseHeight = arc / 2;
                var baseRectangle = new Rectangle2D.Double(barX, barY + barHeight - baseHeight, barWidth, baseHeight);

                var barArea = new Area();

                barArea.add(new Area(barRectangle));
                barArea.add(new Area(baseRectangle));

                dataSetBarShapes.add(barArea);

                barX += barWidth;
            }

            barShapes.add(dataSetBarShapes);

            barX += barSpacing + verticalGridLineStrokeWidth;
        }

        var markerFont = getMarkerFont();

        for (var rangeMarker : getRangeMarkers()) {
            var value = map(rangeMarker.value(), Number::doubleValue);

            if (value == null) {
                throw new UnsupportedOperationException("Marker value is not defined.");
            }

            var y = zeroY - value * scale;

            rangeMarkerLines.add(new Line2D.Double(rangeLabelOffset, y, rangeLabelOffset + chartWidth, y));

            var textPane = new TextPane(rangeLabelTransform.apply(value));

            textPane.setFont(markerFont);
            textPane.setHorizontalAlignment(HorizontalAlignment.TRAILING);

            var size = textPane.getPreferredSize();

            textPane.setBounds(0, (int)y - size.height / 2, (int)rangeLabelWidth, size.height);
            textPane.doLayout();

            rangeMarkerTextPanes.add(textPane);
        }
    }

    @Override
    protected void draw(Graphics2D graphics) {
        if (getShowHorizontalGridLines()) {
            graphics.setColor(getHorizontalGridLineColor());
            graphics.setStroke(getHorizontalGridLineStroke());

            for (var horizontalGridLine : horizontalGridLines) {
                graphics.draw(horizontalGridLine);
            }
        }

        if (getShowVerticalGridLines()) {
            graphics.setColor(getVerticalGridLineColor());
            graphics.setStroke(getVerticalGridLineStroke());

            for (var verticalGridLine : verticalGridLines) {
                graphics.draw(verticalGridLine);
            }
        }

        graphics.setColor(getDomainLabelColor());

        for (var textPane : domainLabelTextPanes) {
            paintComponent(graphics, textPane);
        }

        graphics.setColor(getRangeLabelColor());

        for (var textPane : rangeLabelTextPanes) {
            paintComponent(graphics, textPane);
        }

        var dataSets = getDataSets();

        for (var dataSetBarShapes : barShapes) {
            var i = 0;

            for (var barShape : dataSetBarShapes) {
                var dataSet = dataSets.get(i++);

                graphics.setColor(dataSet.getColor());
                graphics.setStroke(dataSet.getStroke());

                graphics.fill(barShape);
            }
        }

        graphics.setColor(getMarkerColor());
        graphics.setStroke(getMarkerStroke());

        for (var rangeMarkerLine : rangeMarkerLines) {
            graphics.draw(rangeMarkerLine);
        }

        for (var textPane : rangeMarkerTextPanes) {
            paintComponent(graphics, textPane);
        }

        paintComponent(graphics, legendPanel);
    }
}
