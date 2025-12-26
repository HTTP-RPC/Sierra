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
import org.httprpc.sierra.TextPane;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Candlestick chart.
 */
public class CandlestickChart<K extends Comparable<? super K>> extends Chart<K, OHLC> {
    /**
     * Candlestick chart legend icon.
     */
    public static class LegendIcon implements Icon {
        private DataSet<?, ?> dataSet;

        private Line2D.Double shape = new Line2D.Double();

        private static final int SIZE = 12;

        /**
         * Constructs a new candlestick chart legend icon.
         *
         * @param dataSet
         * The data set the icon is associated with.
         */
        public LegendIcon(DataSet<?, ?> dataSet) {
            if (dataSet == null) {
                throw new IllegalArgumentException();
            }

            this.dataSet = dataSet;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            var iconGraphics = (Graphics2D)graphics.create();

            iconGraphics.setRenderingHints(renderingHints);

            paintIcon(iconGraphics, x, y);

            iconGraphics.dispose();
        }

        private void paintIcon(Graphics2D graphics, int x, int y) {
            shape.setLine(x, y + (double)SIZE / 2, SIZE, y + (double)SIZE / 2);

            graphics.setColor(dataSet.getColor());
            graphics.setStroke(bodyOutlineStroke);

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

    private double bodyTransparency = 1.0;

    private List<List<Rectangle2D.Double>> bodyRectangles = listOf();

    private List<List<Line2D.Double>> highWickLines = listOf();
    private List<List<Line2D.Double>> lowWickLines = listOf();

    private List<JLabel> rangeMarkerLabels = listOf();
    private List<Line2D.Double> rangeMarkerLines = listOf();

    private static final BasicStroke bodyOutlineStroke;
    private static final BasicStroke wickStroke;
    static {
        bodyOutlineStroke = new BasicStroke(1.0f);
        wickStroke = new BasicStroke(1.0f);
    }

    /**
     * Returns the body transparency.
     *
     * @return
     * The body transparency.
     */
    public double getBodyTransparency() {
        return bodyTransparency;
    }

    /**
     * Sets the body transparency.
     *
     * @param bodyTransparency
     * The body transparency, as a value from 0.0 to 1.0. The default value is
     * 1.0.
     */
    public void setBodyTransparency(double bodyTransparency) {
        if (bodyTransparency < 0.0 || bodyTransparency > 1.0) {
            throw new IllegalArgumentException();
        }

        this.bodyTransparency = bodyTransparency;
    }

    @Override
    protected void validate(Graphics2D graphics) {
        bodyRectangles.clear();

        highWickLines.clear();
        lowWickLines.clear();

        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();

        var dataSets = getDataSets();

        var keys = new TreeSet<K>();

        var minimum = Double.POSITIVE_INFINITY;
        var maximum = Double.NEGATIVE_INFINITY;

        for (var dataSet : dataSets) {
            for (var entry : dataSet.getDataPoints().entrySet()) {
                keys.add(entry.getKey());

                var value = entry.getValue();

                minimum = Math.min(minimum, value.low());
                maximum = Math.max(maximum, value.high());
            }
        }

        var keyCount = keys.size();

        if (keyCount == 0) {
            return;
        }

        var width = getWidth();
        var height = getHeight();

        var domainLabelTransform = getDomainLabelTransform();
        var domainLabelFont = getDomainLabelFont();
        var domainLabelLineMetrics = domainLabelFont.getLineMetrics("", graphics.getFontRenderContext());
        var domainLabelHeight = (int)Math.ceil(domainLabelLineMetrics.getHeight());

        var maximumDomainLabelWidth = 0.0;

        for (var key : keys) {
            var label = domainLabelTransform.apply(key);

            var textPane = new TextPane(label);

            textPane.setFont(domainLabelFont);

            textPane.setSize(textPane.getPreferredSize());

            maximumDomainLabelWidth = Math.max(maximumDomainLabelWidth, textPane.getWidth());

            domainLabelTextPanes.add(textPane);
        }

        var horizontalGridLineWidth = getHorizontalGridLineStroke().getLineWidth();
        var verticalGridLineWidth = getVerticalGridLineStroke().getLineWidth();

        var chartHeight = Math.max(height - (domainLabelHeight + DOMAIN_LABEL_SPACING + horizontalGridLineWidth), 0);

        var markerFont = getMarkerFont();

        if (minimum == maximum) {
            minimum -= 1.0;
            maximum += 1.0;
        } else {
            var markerLineMetrics = markerFont.getLineMetrics("", graphics.getFontRenderContext());

            var marginRatio = (markerLineMetrics.getHeight() / 2 + RANGE_LABEL_SPACING) / chartHeight;
            var margin = Math.abs(maximum - minimum) * marginRatio;

            minimum -= margin;
            maximum += margin;
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

        var chartOffset = rangeLabelWidth + RANGE_LABEL_SPACING + verticalGridLineWidth / 2;

        var chartWidth = (double)width - (chartOffset + verticalGridLineWidth / 2);

        var rowHeight = chartHeight / (rangeLabelCount - 1);

        var gridY = horizontalGridLineWidth / 2;

        for (var i = 0; i < rangeLabelCount; i++) {
            horizontalGridLines.add(new Line2D.Double(chartOffset, gridY, chartOffset + chartWidth, gridY));

            gridY += rowHeight;
        }

        var columnWidth = chartWidth / keyCount;

        var verticalGridLineCount = keyCount + 1;

        var gridX = chartOffset;

        for (var i = 0; i < verticalGridLineCount; i++) {
            verticalGridLines.add(new Line2D.Double(gridX, verticalGridLineWidth / 2, gridX, chartHeight));

            gridX += columnWidth;
        }

        var domainLabelX = chartOffset;
        var domainLabelY = chartHeight + DOMAIN_LABEL_SPACING + horizontalGridLineWidth;

        var showDomainLabels = maximumDomainLabelWidth < columnWidth * 0.85;

        for (var i = 0; i < keyCount; i++) {
            var textPane = domainLabelTextPanes.get(i);

            var size = textPane.getSize();

            int x;
            if (showDomainLabels) {
                x = (int)(domainLabelX + columnWidth / 2) - size.width / 2;
            } else if (i == 0) {
                x = (int)domainLabelX;
            } else if (i < keyCount - 1) {
                x = (int)(domainLabelX + columnWidth / 2) - size.width / 2;

                textPane.setText(null);
            } else {
                x = (int)(domainLabelX + columnWidth) - size.width;
            }

            textPane.setLocation(x, (int)domainLabelY);
            textPane.doLayout();

            domainLabelX += columnWidth;
        }

        var rangeLabelY = chartHeight + horizontalGridLineWidth / 2;

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

            rangeLabelY -= rowHeight;
        }

        var bodyWidth = columnWidth * 0.25;

        var scale = chartHeight / (maximum - minimum);

        var zeroY = maximum * scale + horizontalGridLineWidth / 2;

        for (var dataSet : dataSets) {
            var dataSetBodyRectangles = new ArrayList<Rectangle2D.Double>(keyCount);

            var dataSetHighWickLines = new ArrayList<Line2D.Double>(keyCount);
            var dataSetLowWickLines = new ArrayList<Line2D.Double>(keyCount);

            var i = 0;

            for (var value : dataSet.getDataPoints().values()) {
                if (value == null) {
                    throw new UnsupportedOperationException("Value is required.");
                }

                var open = value.open();
                var high = value.high();
                var low = value.low();
                var close = value.close();

                var lineX = chartOffset + (columnWidth * i + columnWidth / 2);

                double top;
                double bottom;
                if (open > close) {
                    top = zeroY - open * scale;
                    bottom = zeroY - close * scale;
                } else {
                    top = zeroY - close * scale;
                    bottom = zeroY - open * scale;
                }

                var bodyRectangle = new Rectangle2D.Double(lineX - bodyWidth / 2, top, bodyWidth, bottom - top);

                dataSetBodyRectangles.add(bodyRectangle);

                var highWickLine = new Line2D.Double(lineX, zeroY - high * scale, lineX, top);
                var lowWickLine = new Line2D.Double(lineX, bottom, lineX, zeroY - low * scale);

                dataSetHighWickLines.add(highWickLine);
                dataSetLowWickLines.add(lowWickLine);

                i++;
            }

            bodyRectangles.add(dataSetBodyRectangles);

            highWickLines.add(dataSetHighWickLines);
            lowWickLines.add(dataSetLowWickLines);
        }

        var markerColor = getMarkerColor();

        for (var rangeMarker : getRangeMarkers()) {
            var value = map(rangeMarker.value(), Number::doubleValue);

            if (value == null) {
                throw new UnsupportedOperationException("Marker value is not defined.");
            }

            var lineY = zeroY - value * scale;

            var text = coalesce(rangeMarker.label(), () -> rangeLabelTransform.apply(value));

            var label = new JLabel(text, rangeMarker.icon(), SwingConstants.LEADING);

            label.setForeground(markerColor);
            label.setFont(markerFont);

            var size = label.getPreferredSize();

            label.setBounds((int)chartOffset + RANGE_LABEL_SPACING, (int)lineY - size.height / 2, size.width, size.height);

            rangeMarkerLabels.add(label);

            var line = new Line2D.Double(chartOffset + label.getWidth() + RANGE_LABEL_SPACING * 2,
                lineY,
                width - RANGE_LABEL_SPACING - verticalGridLineWidth / 2,
                lineY);

            rangeMarkerLines.add(line);
        }
    }

    @Override
    protected void draw(Graphics2D graphics) {
        drawGrid(graphics);

        if (bodyRectangles.isEmpty()) {
            return;
        }

        var i = 0;

        for (var dataSet : getDataSets()) {
            var dataSetBodyRectangles = bodyRectangles.get(i);

            var dataSetHighWickLines = highWickLines.get(i);
            var dataSetLowWickLines = lowWickLines.get(i);

            var j = 0;

            var color = dataSet.getColor();

            var fillColor = colorWithAlpha(color, (int)(bodyTransparency * 255));

            for (var value : dataSet.getDataPoints().values()) {
                var bodyRectangle = dataSetBodyRectangles.get(j);

                if (value.close() < value.open()) {
                    graphics.setColor(fillColor);

                    graphics.fill(bodyRectangle);
                }

                graphics.setColor(color);

                graphics.setStroke(bodyOutlineStroke);

                graphics.draw(bodyRectangle);

                graphics.setStroke(wickStroke);

                graphics.draw(dataSetHighWickLines.get(j));
                graphics.draw(dataSetLowWickLines.get(j));

                j++;
            }

            i++;
        }

        graphics.setColor(getMarkerColor());
        graphics.setStroke(getMarkerStroke());

        for (var label : rangeMarkerLabels) {
            paintComponent(graphics, label);
        }

        for (var rangeMarkerLine : rangeMarkerLines) {
            graphics.draw(rangeMarkerLine);
        }
    }
}
