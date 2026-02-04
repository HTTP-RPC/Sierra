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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Scatter chart.
 */
public class ScatterChart<K extends Comparable<? super K>, V extends Number> extends XYChart<K, V> {
    /**
     * Scatter chart legend icon.
     */
    public static class LegendIcon implements Icon {
        private DataSet<?, ?> dataSet;

        private Ellipse2D.Double shape = new Ellipse2D.Double();

        private static final int SIZE = 12;

        /**
         * Constructs a new scatter chart legend icon.
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
            var stroke = dataSet.getStroke();

            var lineWidth = stroke.getLineWidth();

            shape.setFrame(x + lineWidth / 2, y + lineWidth / 2, SIZE - lineWidth, SIZE - lineWidth);

            graphics.setColor(dataSet.getColor());
            graphics.setStroke(stroke);

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

    private boolean showTrendLines = false;

    private double valueMarkerTransparency = 1.0;

    private List<List<Shape>> valueMarkerShapes = listOf();

    private List<Line2D.Double> trendLines = listOf();

    private static final int VALUE_MARKER_SIZE = 10;

    /**
     * Constructs a new scatter chart.
     *
     * @param domainValueTransform
     * The domain value transform.
     *
     * @param domainKeyTransform
     * The domain key transform.
     */
    public ScatterChart(Function<K, Number> domainValueTransform, Function<Number, K> domainKeyTransform) {
        super(domainValueTransform, domainKeyTransform);
    }

    /**
     * Indicates that trend lines will be shown. The default value is
     * {@code false}.
     *
     * @return
     * {@code true} if trend lines will be shown; {@code false}, otherwise.
     */
    public boolean getShowTrendLines() {
        return showTrendLines;
    }

    /**
     * Toggles trend line visibility.
     *
     * @param showTrendLines
     * {@code true} to show trend lines; {@code false} to hide them.
     */
    public void setShowTrendLines(boolean showTrendLines) {
        this.showTrendLines = showTrendLines;
    }

    /**
     * Returns the value marker transparency. The default value is 1.0.
     *
     * @return
     * The value marker transparency.
     */
    public double getValueMarkerTransparency() {
        return valueMarkerTransparency;
    }

    /**
     * Sets the value marker transparency.
     *
     * @param valueMarkerTransparency
     * The value marker transparency, as a value from 0.0 to 1.0.
     */
    public void setValueMarkerTransparency(double valueMarkerTransparency) {
        if (valueMarkerTransparency < 0.0 || valueMarkerTransparency > 1.0) {
            throw new IllegalArgumentException();
        }

        this.valueMarkerTransparency = valueMarkerTransparency;
    }

    @Override
    public void validate() {
        valueMarkerShapes.clear();

        trendLines.clear();

        validateGrid();

        var domainBounds = getDomainBounds();

        var gridBounds = getGridBounds();

        var gridX = gridBounds.getX();

        var domainScale = getDomainScale();
        var rangeScale = getRangeScale();

        var zeroY = getOrigin().getY();

        var domainValueTransform = getDomainValueTransform();

        var domainMinimum = domainValueTransform.apply(domainBounds.minimum()).doubleValue();

        for (var dataSet : getDataSets()) {
            var dataSetValueMarkerShapes = new LinkedList<Shape>();

            var dataPoints = dataSet.getDataPoints();

            var n = dataPoints.size();

            var totalXY = 0.0;

            var totalX = 0.0;
            var totalY = 0.0;

            var totalXSquared = 0.0;

            for (var entry : dataPoints.entrySet()) {
                var domainValue = domainValueTransform.apply(entry.getKey()).doubleValue();

                var rangeValue = map(entry.getValue(), Number::doubleValue);

                if (rangeValue != null) {
                    var x = gridX + (domainValue - domainMinimum) * domainScale - (double)VALUE_MARKER_SIZE / 2;
                    var y = zeroY - rangeValue * rangeScale - (double)VALUE_MARKER_SIZE / 2;

                    if (showTrendLines) {
                        totalXY += domainValue * rangeValue;

                        totalX += domainValue;
                        totalY += rangeValue;

                        totalXSquared += Math.pow(domainValue, 2);
                    }

                    var shape = new Ellipse2D.Double(x, y, VALUE_MARKER_SIZE, VALUE_MARKER_SIZE);

                    dataSetValueMarkerShapes.add(shape);
                }
            }

            valueMarkerShapes.add(dataSetValueMarkerShapes);

            if (showTrendLines) {
                var domainMaximum = domainValueTransform.apply(domainBounds.maximum()).doubleValue();

                var m = (totalXY - totalX * totalY) / (totalXSquared - Math.pow(totalX, 2));

                Line2D.Double trendLine;
                if (!Double.isNaN(m)) {
                    var b = (totalY - m * totalX) / n;

                    var y1 = zeroY - (m * domainMinimum + b) * rangeScale;
                    var y2 = zeroY - (m * domainMaximum + b) * rangeScale;

                    trendLine = new Line2D.Double(gridX, y1, gridX + gridBounds.getWidth(), y2);
                } else {
                    trendLine = new Line2D.Double();
                }

                trendLines.add(trendLine);
            }
        }

        validateMarkers();
    }

    @Override
    void drawChart(Graphics2D graphics) {
        drawGrid(graphics);

        if (valueMarkerShapes.isEmpty()) {
            return;
        }

        var i = 0;

        for (var dataSet : getDataSets()) {
            var color = dataSet.getColor();
            var stroke = dataSet.getStroke();

            var fillColor = colorWithAlpha(color, (int)(valueMarkerTransparency * 255));

            for (var valueMarkerShape : valueMarkerShapes.get(i)) {
                graphics.setColor(fillColor);

                graphics.fill(valueMarkerShape);

                graphics.setColor(color);
                graphics.setStroke(stroke);

                graphics.draw(valueMarkerShape);
            }

            if (showTrendLines) {
                graphics.setStroke(dataSet.getStroke());

                graphics.draw(trendLines.get(i));
            }

            i++;
        }

        drawMarkers(graphics);
    }
}
