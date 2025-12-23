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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

            iconGraphics.setRenderingHints(new RenderingHints(mapOf(
                entry(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON),
                entry(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            )));

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

    private List<Line2D.Double> horizontalGridLines = listOf();
    private List<Line2D.Double> verticalGridLines = listOf();

    private List<TextPane> domainLabelTextPanes = listOf();
    private List<TextPane> rangeLabelTextPanes = listOf();

    private List<List<Rectangle2D.Double>> barRectangles = listOf();

    private Line2D.Double zeroLine = null;

    private List<JLabel> rangeMarkerLabels = listOf();
    private List<Line2D.Double> rangeMarkerLines = listOf();

    private static final int DOMAIN_LABEL_SPACING = 4;
    private static final int RANGE_LABEL_SPACING = 4;

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
        horizontalGridLines.clear();
        verticalGridLines.clear();

        domainLabelTextPanes.clear();
        rangeLabelTextPanes.clear();

        barRectangles.clear();

        zeroLine = null;

        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();

        var dataSets = getDataSets();

        var totals = new TreeMap<K, Double>();

        var minimum = 0.0;
        var maximum = 0.0;

        for (var dataSet : dataSets) {
            for (var entry : dataSet.getDataPoints().entrySet()) {
                var key = entry.getKey();

                var value = coalesce(map(entry.getValue(), Number::doubleValue), () -> 0.0);

                totals.put(key, coalesce(totals.get(key), () -> 0.0) + value);

                if (!stacked) {
                    minimum = Math.min(minimum, value);
                    maximum = Math.max(maximum, value);
                }
            }
        }

        var keyCount = totals.size();

        if (keyCount == 0) {
            return;
        }

        if (stacked) {
            for (var value : totals.values()) {
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

        for (var key : totals.keySet()) {
            var label = domainLabelTransform.apply(key);

            var textPane = new TextPane(label);

            textPane.setFont(domainLabelFont);
            textPane.setHorizontalAlignment(HorizontalAlignment.CENTER);

            domainLabelTextPanes.add(textPane);
        }

        var chartHeight = Math.max(height - (domainLabelHeight + DOMAIN_LABEL_SPACING), 0);

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

        var rangeLabelOffset = rangeLabelWidth + RANGE_LABEL_SPACING;

        var chartWidth = (double)width - rangeLabelOffset;

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
            textPane.setBounds((int)domainLabelX, chartHeight + DOMAIN_LABEL_SPACING, (int)verticalGridLineSpacing, domainLabelHeight);
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

        var n = stacked ? 1 : dataSets.size();

        var barSpacing = verticalGridLineSpacing * 0.05;

        var barWidth = (verticalGridLineSpacing - (verticalGridLineStrokeWidth + barSpacing * (n + 1))) / n;

        var barX = rangeLabelOffset + verticalGridLineStrokeWidth;

        var scale = chartHeight / (maximum - minimum);

        var zeroY = maximum * scale;

        for (var key : totals.keySet()) {
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

        zeroLine = new Line2D.Double(rangeLabelOffset, zeroY, rangeLabelOffset + chartWidth, zeroY);

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

            label.setBounds((int)rangeLabelOffset + RANGE_LABEL_SPACING, (int)lineY - size.height / 2, size.width, size.height);

            rangeMarkerLabels.add(label);

            var line = new Line2D.Double(rangeLabelOffset + label.getWidth() + RANGE_LABEL_SPACING * 2, lineY, width, lineY);

            rangeMarkerLines.add(line);
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

        clipToGrid(graphics);

        var dataSets = getDataSets();

        for (var dataSetBarRectangles : barRectangles) {
            var i = 0;

            for (var barShape : dataSetBarRectangles) {
                var dataSet = dataSets.get(i++);

                var color = dataSet.getColor();

                graphics.setColor(colorWithAlpha(color, (int)(barTransparency * 255)));
                graphics.fill(barShape);

                graphics.setColor(color);
                graphics.setStroke(getHorizontalGridLineStroke());

                graphics.draw(barShape);
            }
        }

        graphics.setColor(colorWithAlpha(getHorizontalGridLineColor(), 0x80));
        graphics.setStroke(getHorizontalGridLineStroke());

        graphics.draw(zeroLine);

        graphics.setColor(getMarkerColor());
        graphics.setStroke(getMarkerStroke());

        for (var label : rangeMarkerLabels) {
            paintComponent(graphics, label);
        }

        for (var rangeMarkerLine : rangeMarkerLines) {
            graphics.draw(rangeMarkerLine);
        }
    }

    private void clipToGrid(Graphics2D graphics) {
        var x = (int)Math.ceil(verticalGridLines.getFirst().getX1());
        var y = (int)Math.ceil(horizontalGridLines.getFirst().getY1());

        var width = (int)Math.floor(verticalGridLines.getLast().getX1()) - x;
        var height = (int)Math.floor(horizontalGridLines.getLast().getY1()) - y;

        graphics.setClip(x, y, width, height);
    }
}
