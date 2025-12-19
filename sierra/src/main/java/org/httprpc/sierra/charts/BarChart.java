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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
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

    private List<List<Rectangle2D.Double>> barRectangles = listOf();

    private Line2D.Double zeroLine = null;

    private List<JLabel> rangeMarkerLabels = listOf();
    private List<Line2D.Double> rangeMarkerLines = listOf();

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

        barRectangles.clear();

        zeroLine = null;

        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();

        legendPanel.removeAll();

        legendPanel.setSpacing(LEGEND_SPACING);
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

        if (keyCount == 0) {
            return;
        }

        if (minimum == maximum) {
            minimum -= 1.0;
            maximum += 1.0;
        } else {
            var margin = Math.abs(maximum - minimum) * 0.02;

            if (minimum < 0.0) {
                minimum -= margin;
            }

            if (maximum > 0.0) {
                maximum += margin;
            }
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
            var dataSetBarRectangles = new ArrayList<Rectangle2D.Double>();

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

                var barRectangle = new Rectangle2D.Double(barX, barY, barWidth, barHeight);

                dataSetBarRectangles.add(barRectangle);

                barX += barWidth;
            }

            barRectangles.add(dataSetBarRectangles);

            barX += barSpacing + verticalGridLineStrokeWidth;
        }

        if (maximum > 0.0 && minimum < 0.0) {
            zeroLine = new Line2D.Double(rangeLabelOffset, zeroY, rangeLabelOffset + chartWidth, zeroY);
        }

        var markerFont = getMarkerFont();

        for (var rangeMarker : getRangeMarkers()) {
            var value = map(rangeMarker.value(), Number::doubleValue);

            if (value == null) {
                throw new UnsupportedOperationException("Marker value is not defined.");
            }

            var y = zeroY - value * scale;

            var label = new JLabel(rangeMarker.label(), rangeMarker.icon(), SwingConstants.LEADING);

            label.setFont(markerFont);

            var size = label.getPreferredSize();

            label.setBounds((int)rangeLabelOffset + RANGE_LABEL_SPACING, (int)y - size.height / 2, size.width, size.height);

            rangeMarkerLabels.add(label);

            var x = rangeLabelOffset + label.getWidth() + RANGE_LABEL_SPACING * 2;

            rangeMarkerLines.add(new Line2D.Double(x, y, width, y));
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

        for (var dataSetBarShapes : barRectangles) {
            var i = 0;

            for (var barShape : dataSetBarShapes) {
                var dataSet = dataSets.get(i++);

                graphics.setColor(dataSet.getColor());
                graphics.setStroke(dataSet.getStroke());

                graphics.fill(barShape);
            }
        }

        if (zeroLine != null) {
            graphics.setColor(getHorizontalGridLineColor());
            graphics.setStroke(getHorizontalGridLineStroke());

            graphics.draw(zeroLine);
        }

        graphics.setColor(getMarkerColor());
        graphics.setStroke(getMarkerStroke());

        for (var label : rangeMarkerLabels) {
            paintComponent(graphics, label);
        }

        for (var rangeMarkerLine : rangeMarkerLines) {
            graphics.draw(rangeMarkerLine);
        }

        paintComponent(graphics, legendPanel);
    }
}
