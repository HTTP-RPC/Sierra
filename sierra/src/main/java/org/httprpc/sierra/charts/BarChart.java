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
import java.util.TreeMap;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Bar chart.
 */
public class BarChart<K extends Comparable<? super K>, V extends Number> extends Chart<K, V> {
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

    private static final int DOMAIN_LABEL_SPACING = 4;
    private static final int RANGE_LABEL_SPACING = 4;

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
     * Returns the bar transparency.
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
     * The bar transparency, as a value from 0.0 to 1.0. The default value is
     * 1.0.
     */
    public void setBarTransparency(double barTransparency) {
        if (barTransparency < 0.0 || barTransparency > 1.0) {
            throw new IllegalArgumentException();
        }

        this.barTransparency = barTransparency;
    }

    @Override
    protected void validate(Graphics2D graphics) {
        zeroLine = null;

        barRectangles.clear();

        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();

        var dataSets = getDataSets();

        var totalValues = new TreeMap<K, Double>();

        var minimum = 0.0;
        var maximum = 0.0;

        for (var dataSet : dataSets) {
            for (var entry : dataSet.getDataPoints().entrySet()) {
                var key = entry.getKey();

                var value = coalesce(map(entry.getValue(), Number::doubleValue), () -> 0.0);

                if (stacked && value < 0.0) {
                    throw new UnsupportedOperationException("Negative value in data set.");
                }

                totalValues.put(key, coalesce(totalValues.get(key), () -> 0.0) + value);

                if (!stacked) {
                    minimum = Math.min(minimum, value);
                    maximum = Math.max(maximum, value);
                }
            }
        }

        var keyCount = totalValues.size();

        if (keyCount == 0) {
            return;
        }

        if (stacked) {
            for (var value : totalValues.values()) {
                minimum = Math.min(minimum, value);
                maximum = Math.max(maximum, value);
            }
        }

        var width = getWidth();
        var height = getHeight();

        var domainLabelTransform = getDomainLabelTransform();
        var domainLabelFont = getDomainLabelFont();
        var domainLabelLineMetrics = domainLabelFont.getLineMetrics("", graphics.getFontRenderContext());
        var domainLabelHeight = (int)Math.ceil(domainLabelLineMetrics.getHeight());

        var maximumDomainLabelWidth = 0.0;

        for (var key : totalValues.keySet()) {
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

            if (minimum < 0.0) {
                minimum -= margin;
            }

            if (maximum > 0.0) {
                maximum += margin;
            }
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

        var n = stacked ? 1 : dataSets.size();

        var spacing = columnWidth * 0.05;

        var barWidth = (columnWidth - spacing * (n + 1)) / n;

        var barX = chartOffset;

        var scale = chartHeight / (maximum - minimum);

        var zeroY = maximum * scale + horizontalGridLineWidth / 2;

        if (maximum > 0.0 && minimum < 0.0) {
            zeroLine = new Line2D.Double(chartOffset, zeroY, chartOffset + chartWidth, zeroY);
        }

        for (var key : totalValues.keySet()) {
            var dataSetBarRectangles = new ArrayList<Rectangle2D.Double>();

            if (stacked) {
                barX += spacing;

                var barY = zeroY;

                for (var dataSet : dataSets) {
                    var value = coalesce(map(dataSet.getDataPoints().get(key), Number::doubleValue), () -> 0.0);

                    var barHeight = value * scale;

                    barY -= barHeight;

                    var barRectangle = new Rectangle2D.Double(barX, barY, barWidth, barHeight);

                    dataSetBarRectangles.add(barRectangle);
                }

                barX += barWidth;
            } else {
                for (var dataSet : dataSets) {
                    barX += spacing;

                    var value = coalesce(map(dataSet.getDataPoints().get(key), Number::doubleValue), () -> 0.0);

                    var barY = zeroY;

                    var barHeight = Math.abs(value) * scale;

                    if (value > 0.0) {
                        barY -= barHeight;
                    }

                    var barRectangle = new Rectangle2D.Double(barX, barY, barWidth, barHeight);

                    dataSetBarRectangles.add(barRectangle);

                    barX += barWidth;
                }
            }

            barRectangles.add(dataSetBarRectangles);

            barX += spacing;
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

            var line = new Line2D.Double(chartOffset + label.getWidth() + RANGE_LABEL_SPACING * 2, lineY, width, lineY);

            rangeMarkerLines.add(line);
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

        var dataSets = getDataSets();

        for (var dataSetBarRectangles : barRectangles) {
            var i = 0;

            for (var barRectangle : dataSetBarRectangles) {
                var dataSet = dataSets.get(i++);

                if (barRectangle.getHeight() > 0.0) {
                    var color = dataSet.getColor();

                    graphics.setColor(colorWithAlpha(color, (int)(barTransparency * 255)));
                    graphics.fill(barRectangle);

                    graphics.setColor(color);
                    graphics.setStroke(barOutlineStroke);

                    graphics.draw(barRectangle);
                }
            }
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
