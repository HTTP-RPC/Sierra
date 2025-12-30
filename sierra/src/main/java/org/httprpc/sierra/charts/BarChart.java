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
import java.util.SortedMap;
import java.util.TreeMap;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Bar chart.
 */
public class BarChart<K extends Comparable<? super K>, V extends Number> extends CategoryChart<K, V> {
    /**
     * Bar chart legend icon.
     */
    public static class LegendIcon implements Icon {
        private DataSet<?, ?> dataSet;

        private Rectangle2D.Double shape = new Rectangle2D.Double();

        private static final int SIZE = 12;

        /**
         * Constructs a new bar chart legend icon.
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

    private boolean stacked;

    private double barTransparency = 1.0;

    private Line2D.Double zeroLine = null;

    private List<List<Rectangle2D.Double>> barRectangles = listOf();

    private List<JLabel> rangeMarkerLabels = listOf();
    private List<Line2D.Double> rangeMarkerLines = listOf();

    private static final BasicStroke barOutlineStroke;
    static {
        barOutlineStroke = new BasicStroke(1.0f);
    }

    /**
     * Constructs a new bar chart.
     */
    public BarChart() {
        this(false);
    }

    /**
     * Constructs a new bar chart.
     *
     * @param stacked
     * {@code true} for a stacked bar chart; {@code false}, otherwise.
     */
    public BarChart(boolean stacked) {
        this.stacked = stacked;
    }

    /**
     * Indicates that the chart is a stacked bar chart.
     *
     * @return
     * {@code true} if the chart is a stacked bar chart; {@code false},
     * otherwise.
     */
    public boolean isStacked() {
        return stacked;
    }

    /**
     * Returns the bar transparency. The default value is 1.0.
     *
     * @return
     * The bar transparency.
     */
    public double getBarTransparency() {
        return barTransparency;
    }

    /**
     * Sets the bar transparency.
     *
     * @param barTransparency
     * The bar transparency, as a value from 0.0 to 1.0.
     */
    public void setBarTransparency(double barTransparency) {
        if (barTransparency < 0.0 || barTransparency > 1.0) {
            throw new IllegalArgumentException();
        }

        this.barTransparency = barTransparency;
    }

    @Override
    public void validate() {
        keys.clear();

        zeroLine = null;

        barRectangles.clear();

        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();

        var dataSets = getDataSets();

        SortedMap<K, Double> totalValues;
        if (stacked) {
            totalValues = new TreeMap<>();
        } else {
            totalValues = null;
        }

        var rangeMinimum = 0.0;
        var rangeMaximum = 0.0;

        for (var dataSet : dataSets) {
            for (var entry : dataSet.getDataPoints().entrySet()) {
                var key = entry.getKey();

                keys.add(key);

                var value = coalesce(map(entry.getValue(), Number::doubleValue), () -> 0.0);

                if (stacked) {
                    if (value < 0.0) {
                        throw new UnsupportedOperationException("Negative value in data set.");
                    }

                    totalValues.put(key, coalesce(totalValues.get(key), () -> 0.0) + value);
                } else {
                    rangeMinimum = Math.min(rangeMinimum, value);
                    rangeMaximum = Math.max(rangeMaximum, value);
                }
            }
        }

        if (stacked) {
            for (var value : totalValues.values()) {
                rangeMinimum = Math.min(rangeMinimum, value);
                rangeMaximum = Math.max(rangeMaximum, value);
            }
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

        validateGrid();

        var keyCount = keys.size();

        if (keyCount == 0) {
            return;
        }

        var n = stacked ? 1 : dataSets.size();

        var barWidth = (columnWidth / n) * 0.75;
        var barSpacing = (columnWidth - (barWidth * n)) / (n + 1);

        var rangeScale = chartHeight / (rangeMaximum - rangeMinimum);

        var zeroY = rangeMaximum * rangeScale + horizontalGridLineWidth / 2;

        if (rangeMaximum > 0.0 && rangeMinimum < 0.0) {
            zeroLine = new Line2D.Double(chartOffset, zeroY, chartOffset + chartWidth, zeroY);
        }

        var i = 0;

        for (var dataSet : dataSets) {
            var dataSetBarRectangles = new ArrayList<Rectangle2D.Double>(keyCount);

            var dataPoints = dataSet.getDataPoints();

            var j = 0;

            for (var key : keys) {
                var value = coalesce(map(dataPoints.get(key), Number::doubleValue), () -> 0.0);

                Rectangle2D.Double barRectangle;
                if (stacked) {
                    var barX = chartOffset + columnWidth * j + barSpacing;

                    var barHeight = value * rangeScale;

                    double barY;
                    if (i == 0) {
                        barY = zeroY - barHeight;
                    } else {
                        barY = barRectangles.get(i - 1).get(j).getY() - barHeight;
                    }

                    barRectangle = new Rectangle2D.Double(barX, barY, barWidth, barHeight);
                } else {
                    var barX = chartOffset + columnWidth * j + barSpacing * (i + 1) + barWidth * i;

                    var barHeight = Math.abs(value) * rangeScale;

                    double barY;
                    if (value > 0.0) {
                        barY = zeroY - barHeight;
                    } else {
                        barY = zeroY;
                    }

                    barRectangle = new Rectangle2D.Double(barX, barY, barWidth, barHeight);
                }

                dataSetBarRectangles.add(barRectangle);

                j++;
            }

            barRectangles.add(dataSetBarRectangles);

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

        if (zeroLine != null) {
            graphics.setColor(colorWithAlpha(getHorizontalGridLineColor(), 0x80));
            graphics.setStroke(getHorizontalGridLineStroke());

            graphics.draw(zeroLine);
        }

        if (barRectangles.isEmpty()) {
            return;
        }

        var i = 0;

        for (var dataSet : getDataSets()) {
            var color = dataSet.getColor();

            var fillColor = colorWithAlpha(color, (int)(barTransparency * 255));

            for (var barRectangle : barRectangles.get(i)) {
                graphics.setColor(fillColor);

                graphics.fill(barRectangle);

                if (barRectangle.getHeight() > 0.0) {
                    graphics.setColor(color);
                    graphics.setStroke(barOutlineStroke);

                    graphics.draw(barRectangle);
                }
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
