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

        private Rectangle2D.Double shape = new Rectangle2D.Double();

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
            var lineWidth = bodyOutlineStroke.getLineWidth();

            shape.setFrame(x + lineWidth / 2, y + lineWidth / 2, SIZE - lineWidth, SIZE - lineWidth);

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
     * Constructs a new candlestick chart.
     */
    public CandlestickChart() {
        super(null, null);
    }

    /**
     * Returns the body transparency. The default value is 1.0.
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
     * The body transparency, as a value from 0.0 to 1.0.
     */
    public void setBodyTransparency(double bodyTransparency) {
        if (bodyTransparency < 0.0 || bodyTransparency > 1.0) {
            throw new IllegalArgumentException();
        }

        this.bodyTransparency = bodyTransparency;
    }

    @Override
    protected void validate() {
        bodyRectangles.clear();

        highWickLines.clear();
        lowWickLines.clear();

        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();

        var dataSets = getDataSets();

        var keys = new TreeSet<K>();

        var rangeMinimum = Double.POSITIVE_INFINITY;
        var rangeMaximum = Double.NEGATIVE_INFINITY;

        for (var dataSet : dataSets) {
            for (var entry : dataSet.getDataPoints().entrySet()) {
                keys.add(entry.getKey());

                var value = entry.getValue();

                rangeMinimum = Math.min(rangeMinimum, value.low());
                rangeMaximum = Math.max(rangeMaximum, value.high());
            }
        }

        if (rangeMinimum > rangeMaximum) {
            rangeMinimum = 0.0;
            rangeMaximum = 0.0;
        }

        if (rangeMinimum == rangeMaximum) {
            rangeMinimum -= 1.0;
            rangeMaximum += 1.0;
        }

        if (Double.isNaN(this.rangeMinimum)) {
            this.rangeMinimum = rangeMinimum;
        } else {
            rangeMinimum = this.rangeMinimum;
        }

        if (Double.isNaN(this.rangeMaximum)) {
            this.rangeMaximum = rangeMaximum;
        } else {
            rangeMaximum = this.rangeMaximum;
        }

        // TODO Remove
        this.keys.addAll(keys);

        validateGrid();

        var keyCount = keys.size();

        if (keyCount == 0) {
            return;
        }

        var n = dataSets.size();

        var bodyWidth = columnWidth * 0.25 / Math.sqrt(n);
        var bodySpacing = (columnWidth - (bodyWidth * n)) / (n + 1);

        var rangeScale = chartHeight / (rangeMaximum - rangeMinimum);

        var zeroY = rangeMaximum * rangeScale + horizontalGridLineWidth / 2;

        var i = 0;

        for (var dataSet : dataSets) {
            var dataSetBodyRectangles = new ArrayList<Rectangle2D.Double>(keyCount);

            var dataSetHighWickLines = new ArrayList<Line2D.Double>(keyCount);
            var dataSetLowWickLines = new ArrayList<Line2D.Double>(keyCount);

            var dataPoints = dataSet.getDataPoints();

            var j = 0;

            for (var key : keys) {
                var value = dataPoints.get(key);

                if (value != null) {
                    var open = value.open();
                    var high = value.high();
                    var low = value.low();
                    var close = value.close();

                    var lineX = chartOffset + columnWidth * j + bodySpacing * (i + 1) + bodyWidth * i + bodyWidth / 2;

                    double top;
                    double bottom;
                    if (open > close) {
                        top = zeroY - open * rangeScale;
                        bottom = zeroY - close * rangeScale;
                    } else {
                        top = zeroY - close * rangeScale;
                        bottom = zeroY - open * rangeScale;
                    }

                    var bodyRectangle = new Rectangle2D.Double(lineX - bodyWidth / 2, top, bodyWidth, bottom - top);

                    dataSetBodyRectangles.add(bodyRectangle);

                    var highWickLine = new Line2D.Double(lineX, zeroY - high * rangeScale, lineX, top);
                    var lowWickLine = new Line2D.Double(lineX, bottom, lineX, zeroY - low * rangeScale);

                    dataSetHighWickLines.add(highWickLine);
                    dataSetLowWickLines.add(lowWickLine);
                }

                j++;
            }

            bodyRectangles.add(dataSetBodyRectangles);

            highWickLines.add(dataSetHighWickLines);
            lowWickLines.add(dataSetLowWickLines);

            i++;
        }

        var rangeLabelTransform = getRangeLabelTransform();

        var markerColor = getMarkerColor();
        var markerFont = getMarkerFont();

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

            label.setBounds((int)chartOffset + RANGE_LABEL_SPACING, (int)lineY - size.height / 2, size.width, size.height);

            rangeMarkerLabels.add(label);

            var lineX1 = chartOffset + label.getWidth() + RANGE_LABEL_SPACING * 2;
            var lineX2 = width - RANGE_LABEL_SPACING - verticalGridLineWidth / 2;

            if (lineX2 > lineX1) {
                var line = new Line2D.Double(lineX1, lineY, lineX2, lineY);

                rangeMarkerLines.add(line);
            }
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
