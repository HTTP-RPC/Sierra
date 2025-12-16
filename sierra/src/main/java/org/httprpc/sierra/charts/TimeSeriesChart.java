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
import java.awt.geom.Line2D;
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

    private Function<K, Number> valueTransform;
    private Function<Number, K> keyTransform;

    private List<Line2D.Double> horizontalGridLines = listOf();
    private List<Line2D.Double> verticalGridLines = listOf();

    private List<TextPane> domainLabelTextPanes = listOf();
    private List<TextPane> rangeLabelTextPanes = listOf();

    private RowPanel legendPanel = new RowPanel();

    private static final int DOMAIN_LABEL_SPACING = 4;
    private static final int RANGE_LABEL_SPACING = 4;

    private static final int LEGEND_SPACING = 16;

    /**
     * Constructs a new time series chart.
     *
     * @param valueTransform
     * The value transform.
     *
     * @param keyTransform
     * The key transform.
     */
    public TimeSeriesChart(Function<K, Number> valueTransform, Function<Number, K> keyTransform) {
        if (valueTransform == null || keyTransform == null) {
            throw new IllegalArgumentException();
        }

        this.valueTransform = valueTransform;
        this.keyTransform = keyTransform;
    }

    @Override
    protected void validate() {
        horizontalGridLines.clear();
        verticalGridLines.clear();

        domainLabelTextPanes.clear();
        rangeLabelTextPanes.clear();

        // TODO

        legendPanel.removeAll();

        legendPanel.setSpacing(16);
        legendPanel.setComponentOrientation(getComponentOrientation());

        var dataSets = getDataSets();

        var domainMinimum = 0.0;
        var domainMaximum = 0.0;

        var rangeMinimum = 0.0;
        var rangeMaximum = 0.0;

        var legendColor = getLegendColor();
        var legendFont = getLegendFont();

        for (var dataSet : dataSets) {
            for (var entry : dataSet.getDataPoints().entrySet()) {
                var domainValue = map(entry.getKey(), valueTransform).doubleValue();

                domainMinimum = Math.min(domainMinimum, domainValue);
                domainMaximum = Math.max(domainMaximum, domainValue);

                var rangeValue = map(entry.getValue(), Number::doubleValue);

                if (rangeValue == null) {
                    continue;
                }

                rangeMinimum = Math.min(rangeMinimum, rangeValue);
                rangeMaximum = Math.max(rangeMaximum, rangeValue);
            }

            var legendLabel = new JLabel(dataSet.getLabel(), new LegendIcon(dataSet), SwingConstants.CENTER);

            legendLabel.setForeground(legendColor);
            legendLabel.setFont(legendFont);

            legendPanel.add(legendLabel);
        }

        var width = getWidth();
        var height = getHeight();

        var legendSize = legendPanel.getPreferredSize();

        legendPanel.setLocation(width / 2 - legendSize.width / 2, height - legendSize.height);
        legendPanel.setSize(legendSize);

        legendPanel.doLayout();

        var domainLabelCount = getDomainLabelCount();

        var domainStep = (domainMaximum - domainMinimum) / (domainLabelCount - 1);

        var domainLabelTransform = getDomainLabelTransform();
        var domainLabelFont = getDomainLabelFont();

        var domainLabelHeight = 0.0;

        for (var i = 0; i < domainLabelCount; i++) {
            var label = domainLabelTransform.apply(keyTransform.apply(domainMinimum + domainStep * i));

            var textPane = new TextPane(label);

            textPane.setFont(domainLabelFont);

            // TODO Set alignment based on position (LEADING, CENTER, TRAILING)

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

        // TODO
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

        // TODO

        paintComponent(graphics, legendPanel);
    }
}
