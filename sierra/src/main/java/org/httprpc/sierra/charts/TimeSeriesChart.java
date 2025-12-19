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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.function.Function;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Time series chart.
 */
public class TimeSeriesChart<K extends Comparable<K>, V extends Number> extends Chart<K, V> {
    private static class LegendIcon implements Icon {
        DataSet<?, ?> dataSet;

        Line2D.Double shape = new Line2D.Double();

        static final int SIZE = 16;

        LegendIcon(DataSet<?, ?> dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            paintIcon((Graphics2D)graphics, x, y);
        }

        void paintIcon(Graphics2D graphics, int x, int y) {
            shape.setLine(x, y + (double)SIZE / 2, SIZE, y + (double)SIZE / 2);

            graphics.setColor(dataSet.getColor());
            graphics.setStroke(dataSet.getStroke());

            graphics.draw(shape);
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

    private Function<K, Number> domainValueTransform;
    private Function<Number, K> domainKeyTransform;

    private List<Line2D.Double> horizontalGridLines = listOf();
    private List<Line2D.Double> verticalGridLines = listOf();

    private List<TextPane> domainLabelTextPanes = listOf();
    private List<TextPane> rangeLabelTextPanes = listOf();

    private Line2D.Double zeroLine = null;

    private List<Path2D.Double> paths = listOf();

    private List<JLabel> domainMarkerLabels = listOf();
    private List<Line2D.Double> domainMarkerLines = listOf();
    private List<Shape> domainMarkerShapes = listOf();

    private List<JLabel> rangeMarkerLabels = listOf();
    private List<Line2D.Double> rangeMarkerLines = listOf();
    private List<Shape> rangeMarkerShapes = listOf();

    private RowPanel legendPanel = new RowPanel();

    private static final int DOMAIN_LABEL_SPACING = 4;
    private static final int RANGE_LABEL_SPACING = 4;

    private static final int LEGEND_SPACING = 16;

    /**
     * Constructs a new time series chart.
     *
     * @param domainValueTransform
     * The domain value transform.
     *
     * @param domainKeyTransform
     * The domain key transform.
     */
    public TimeSeriesChart(Function<K, Number> domainValueTransform, Function<Number, K> domainKeyTransform) {
        if (domainValueTransform == null || domainKeyTransform == null) {
            throw new IllegalArgumentException();
        }

        this.domainValueTransform = domainValueTransform;
        this.domainKeyTransform = domainKeyTransform;
    }

    @Override
    protected void validate() {
        horizontalGridLines.clear();
        verticalGridLines.clear();

        domainLabelTextPanes.clear();
        rangeLabelTextPanes.clear();

        zeroLine = null;

        paths.clear();

        domainMarkerLabels.clear();
        domainMarkerLines.clear();

        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();

        legendPanel.removeAll();

        legendPanel.setSpacing(LEGEND_SPACING);
        legendPanel.setComponentOrientation(getComponentOrientation());

        var dataSets = getDataSets();

        var domainMinimum = Double.POSITIVE_INFINITY;
        var domainMaximum = Double.NEGATIVE_INFINITY;

        var rangeMinimum = Double.POSITIVE_INFINITY;
        var rangeMaximum = Double.NEGATIVE_INFINITY;

        var domainLabelCount = getDomainLabelCount();

        var legendColor = getLegendColor();
        var legendFont = getLegendFont();

        for (var dataSet : dataSets) {
            var dataPoints = dataSet.getDataPoints();

            for (var entry : dataPoints.entrySet()) {
                var domainValue = map(entry.getKey(), domainValueTransform).doubleValue();

                domainMinimum = Math.min(domainMinimum, domainValue);
                domainMaximum = Math.max(domainMaximum, domainValue);

                var rangeValue = entry.getValue().doubleValue();

                rangeMinimum = Math.min(rangeMinimum, rangeValue);
                rangeMaximum = Math.max(rangeMaximum, rangeValue);
            }

            domainLabelCount = Math.min(domainLabelCount, dataPoints.size());

            var legendLabel = new JLabel(dataSet.getLabel(), new LegendIcon(dataSet), SwingConstants.CENTER);

            legendLabel.setForeground(legendColor);
            legendLabel.setFont(legendFont);

            legendPanel.add(legendLabel);
        }

        if (domainMinimum > domainMaximum || domainLabelCount < 2) {
            return;
        }

        if (rangeMinimum == rangeMaximum) {
            rangeMinimum -= 1.0;
            rangeMaximum += 1.0;
        } else {
            var rangeMargin = Math.abs(rangeMaximum - rangeMinimum) * 0.02;

            rangeMinimum -= rangeMargin;
            rangeMaximum += rangeMargin;
        }

        var width = getWidth();
        var height = getHeight();

        var legendSize = legendPanel.getPreferredSize();

        legendPanel.setLocation(width / 2 - legendSize.width / 2, height - legendSize.height);
        legendPanel.setSize(legendSize);

        legendPanel.doLayout();

        var domainStep = (domainMaximum - domainMinimum) / (domainLabelCount - 1);

        var domainLabelTransform = getDomainLabelTransform();
        var domainLabelFont = getDomainLabelFont();

        var domainLabelHeight = 0.0;

        for (var i = 0; i < domainLabelCount; i++) {
            var label = domainLabelTransform.apply(domainKeyTransform.apply(domainMinimum + domainStep * i));

            var textPane = new TextPane(label);

            textPane.setFont(domainLabelFont);
            textPane.setSize(textPane.getPreferredSize());

            domainLabelHeight = Math.max(domainLabelHeight, textPane.getHeight());

            domainLabelTextPanes.add(textPane);
        }

        var rangeLabelCount = getRangeLabelCount();

        var rangeStep = Math.abs(rangeMaximum - rangeMinimum) / (rangeLabelCount - 1);

        var rangeLabelTransform = getRangeLabelTransform();
        var rangeLabelFont = getRangeLabelFont();

        var rangeLabelWidth = 0.0;

        for (var i = 0; i < rangeLabelCount; i++) {
            var label = rangeLabelTransform.apply(rangeMinimum + rangeStep * i);

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

        var verticalGridLineSpacing = (chartWidth - verticalGridLineStrokeWidth) / (domainLabelCount - 1);

        var gridX = rangeLabelOffset + verticalGridLineStrokeWidth / 2.0;

        for (var i = 0; i < domainLabelCount; i++) {
            verticalGridLines.add(new Line2D.Double(gridX, 0.0, gridX, chartHeight));

            gridX += verticalGridLineSpacing;
        }

        var domainLabelX = rangeLabelOffset;
        var domainLabelY = (int)chartHeight + DOMAIN_LABEL_SPACING;

        for (var i = 0; i < domainLabelCount; i++) {
            var textPane = domainLabelTextPanes.get(i);

            var size = textPane.getSize();

            int x;
            if (i == 0) {
                x = (int)domainLabelX;
            } else if (i < domainLabelCount - 1) {
                x = (int)domainLabelX - size.width / 2;
            } else {
                x = (int)domainLabelX - size.width;
            }

            textPane.setBounds(x, domainLabelY, (int)verticalGridLineSpacing, size.height);
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

        var domainScale = chartWidth / (domainMaximum - domainMinimum);
        var rangeScale = chartHeight / (rangeMaximum - rangeMinimum);

        var zeroY = rangeMaximum * rangeScale;

        for (var dataSet : dataSets) {
            var path = new Path2D.Double();

            var i = 0;

            for (var entry : dataSet.getDataPoints().entrySet()) {
                var domainValue = map(entry.getKey(), domainValueTransform).doubleValue();
                var rangeValue = entry.getValue().doubleValue();

                var x = rangeLabelOffset + (domainValue - domainMinimum) * domainScale;
                var y = zeroY - rangeValue * rangeScale;

                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }

                i++;
            }

            paths.add(path);
        }

        if (rangeMaximum > 0.0 && rangeMinimum < 0.0) {
            zeroLine = new Line2D.Double(rangeLabelOffset, zeroY, rangeLabelOffset + chartWidth, zeroY);
        }

        var markerColor = getMarkerColor();
        var markerFont = getMarkerFont();

        for (var domainMarker : getDomainMarkers()) {
            var key = domainMarker.key();

            if (key == null) {
                throw new UnsupportedOperationException("Marker key is not defined.");
            }

            var domainValue = map(key, domainValueTransform).doubleValue() - domainMinimum;

            var lineX = rangeLabelOffset + domainValue * domainScale;

            var text = coalesce(domainMarker.label(), () -> domainLabelTransform.apply(key));

            var label = new JLabel(text, domainMarker.icon(), SwingConstants.CENTER);

            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.BOTTOM);
            label.setIconTextGap(0);

            label.setForeground(markerColor);
            label.setFont(markerFont);

            var size = label.getPreferredSize();

            var labelX = (int)Math.round(lineX - (double)size.width / 2);
            var labelY = (int)Math.ceil(chartHeight - (size.height + DOMAIN_LABEL_SPACING));

            label.setBounds(labelX, labelY, size.width, size.height);

            domainMarkerLabels.add(label);

            var value = map(domainMarker.value(), Number::doubleValue);

            if (value != null) {
                var valueY = zeroY - value * rangeScale;

                var line = new Line2D.Double(lineX, labelY - DOMAIN_LABEL_SPACING, lineX, valueY);

                domainMarkerLines.add(line);

                var diameter = getMarkerStroke().getLineWidth() * 5;
                var shape = new Ellipse2D.Double(lineX - diameter / 2, valueY - diameter / 2, diameter, diameter);

                domainMarkerShapes.add(shape);
            }
        }

        for (var rangeMarker : getRangeMarkers()) {
            var value = map(rangeMarker.value(), Number::doubleValue);

            if (value == null) {
                throw new UnsupportedOperationException("Marker value is not defined.");
            }

            var lineY = zeroY - value * rangeScale;

            var text = coalesce(rangeMarker.label(), () -> rangeLabelTransform.apply(value));

            var label = new JLabel(text, rangeMarker.icon(), SwingConstants.LEADING);

            label.setForeground(markerColor);
            label.setFont(markerFont);

            var size = label.getPreferredSize();

            label.setBounds((int)rangeLabelOffset + RANGE_LABEL_SPACING, (int)lineY - size.height / 2, size.width, size.height);

            rangeMarkerLabels.add(label);

            var key = rangeMarker.key();

            if (key != null) {
                var domainValue = domainValueTransform.apply(key).doubleValue();

                var valueX = rangeLabelOffset + domainValue * domainScale;

                var line = new Line2D.Double(rangeLabelOffset + label.getWidth() + RANGE_LABEL_SPACING * 2, lineY, valueX, lineY);

                rangeMarkerLines.add(line);

                var diameter = getMarkerStroke().getLineWidth() * 5;
                var shape = new Ellipse2D.Double(valueX - diameter / 2, lineY - diameter / 2, diameter, diameter);

                rangeMarkerShapes.add(shape);
            }
        }
    }

    @Override
    protected void draw(Graphics2D graphics) {
        if (horizontalGridLines.isEmpty() || verticalGridLines.isEmpty()) {
            return;
        }

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

        if (zeroLine != null) {
            graphics.setColor(colorWithAlpha(getHorizontalGridLineColor(), 0x40));
            graphics.setStroke(getHorizontalGridLineStroke());

            graphics.draw(zeroLine);
        }

        var i = 0;

        for (var dataSet : getDataSets()) {
            graphics.setColor(dataSet.getColor());
            graphics.setStroke(dataSet.getStroke());

            graphics.draw(paths.get(i));

            i++;
        }

        var markerGraphics = (Graphics2D)graphics.create();

        clipToChartBounds(markerGraphics);

        markerGraphics.setColor(getMarkerColor());
        markerGraphics.setStroke(getMarkerStroke());

        for (var domainMarkerLabel : domainMarkerLabels) {
            paintComponent(markerGraphics, domainMarkerLabel);
        }

        for (var domainMarkerLine : domainMarkerLines) {
            markerGraphics.draw(domainMarkerLine);
        }

        for (var domainMarkerShape : domainMarkerShapes) {
            markerGraphics.fill(domainMarkerShape);
        }

        for (var label : rangeMarkerLabels) {
            paintComponent(markerGraphics, label);
        }

        for (var rangeMarkerLine : rangeMarkerLines) {
            markerGraphics.draw(rangeMarkerLine);
        }

        for (var rangeMarkerShape : rangeMarkerShapes) {
            markerGraphics.fill(rangeMarkerShape);
        }

        markerGraphics.dispose();

        paintComponent(graphics, legendPanel);
    }

    private void clipToChartBounds(Graphics2D graphics) {
        var x = (int)Math.ceil(verticalGridLines.getFirst().getX1());
        var y = (int)Math.ceil(horizontalGridLines.getFirst().getY1());

        var width = (int)Math.floor(verticalGridLines.getLast().getX1()) - x;
        var height = (int)Math.floor(horizontalGridLines.getLast().getY1()) - y;

        graphics.setClip(x, y, width, height);
    }
}
