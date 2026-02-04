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
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
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

    private boolean horizontal;
    private boolean stacked;

    private double barTransparency = 1.0;

    private SortedSet<K> keys = sortedSetOf();

    private List<List<Rectangle2D.Double>> barRectangles = listOf();

    private static final BasicStroke outlineStroke;
    static {
        outlineStroke = new BasicStroke(1.0f);
    }

    /**
     * Constructs a new bar chart.
     */
    public BarChart() {
        this(false, false);
    }

    /**
     * Constructs a new bar chart.
     *
     * @param horizontal
     * {@code true} for a horizontal bar chart; {@code false}, otherwise.
     *
     * @param stacked
     * {@code true} for a stacked bar chart; {@code false}, otherwise.
     */
    public BarChart(boolean horizontal, boolean stacked) {
        this.horizontal = horizontal;
        this.stacked = stacked;
    }

    /**
     * Indicates that the chart is horizontal.
     *
     * @return
     * {@code true} if the chart is stacked; {@code false}, otherwise.
     */
    public boolean isHorizontal() {
        return horizontal;
    }

    /**
     * Indicates that the chart is stacked.
     *
     * @return
     * {@code true} if the chart is stacked; {@code false}, otherwise.
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
    SortedSet<K> getKeys() {
        return keys;
    }

    @Override
    boolean isTransposed() {
        return horizontal;
    }

    @Override
    public void validate() {
        keys.clear();

        barRectangles.clear();

        var dataSets = getDataSets();

        var positiveTotals = new TreeMap<K, Double>();
        var negativeTotals = new TreeMap<K, Double>();

        var rangeBounds = getRangeBounds();

        var rangeMinimum = 0.0;
        var rangeMaximum = 0.0;

        for (var dataSet : dataSets) {
            for (var entry : dataSet.getDataPoints().entrySet()) {
                var key = entry.getKey();

                keys.add(key);

                if (rangeBounds == null) {
                    var value = coalesce(map(entry.getValue(), Number::doubleValue), () -> 0.0);

                    if (stacked) {
                        var totals = (value > 0.0) ? positiveTotals : negativeTotals;

                        totals.put(key, coalesce(totals.get(key), () -> 0.0) + value);
                    } else {
                        rangeMinimum = Math.min(rangeMinimum, value);
                        rangeMaximum = Math.max(rangeMaximum, value);
                    }
                }
            }
        }

        if (rangeBounds == null) {
            if (stacked) {
                for (var value : positiveTotals.values()) {
                    rangeMaximum = Math.max(rangeMaximum, value);
                }

                for (var value : negativeTotals.values()) {
                    rangeMinimum = Math.min(rangeMinimum, value);
                }
            }

            setRangeBounds(adjustBounds(rangeMinimum, rangeMaximum));
        }

        validateGrid();

        var keyCount = keys.size();

        if (keyCount == 0) {
            return;
        }

        positiveTotals.clear();
        negativeTotals.clear();

        var gridBounds = getGridBounds();

        var gridX = gridBounds.getX();
        var gridY = gridBounds.getY();

        var rangeScale = getRangeScale();

        var origin = getOrigin();

        var zeroX = origin.getX();
        var zeroY = origin.getY();

        var n = stacked ? 1 : dataSets.size();

        if (isTransposed()) {
            var rowHeight = getRowHeight();

            var barHeight = (rowHeight / n) * 0.75;
            var barSpacing = (rowHeight - (barHeight * n)) / (n + 1);

            var i = 0;

            for (var dataSet : dataSets) {
                var dataSetBarRectangles = new ArrayList<Rectangle2D.Double>(keyCount);

                var dataPoints = dataSet.getDataPoints();

                var j = 0;

                for (var key : keys) {
                    var value = coalesce(map(dataPoints.get(key), Number::doubleValue), () -> 0.0);

                    var barWidth = Math.abs(value) * rangeScale;

                    Rectangle2D.Double barRectangle;
                    if (stacked) {
                        var barY = gridY + rowHeight * j + barSpacing;

                        double barX;
                        if (value < 0.0) {
                            var totalWidth = coalesce(negativeTotals.get(key), () -> 0.0) + barWidth;

                            barX = zeroX - totalWidth;

                            negativeTotals.put(key, totalWidth);
                        } else {
                            var totalWidth = coalesce(positiveTotals.get(key), () -> 0.0);

                            barX = zeroX + totalWidth;

                            positiveTotals.put(key, totalWidth + barWidth);
                        }

                        barRectangle = new Rectangle2D.Double(barX, barY, barWidth, barHeight);
                    } else {
                        var barY = gridY + rowHeight * j + barSpacing * (i + 1) + barHeight * i;

                        double barX;
                        if (value < 0.0) {
                            barX = zeroX - barWidth;
                        } else {
                            barX = zeroX;
                        }

                        barRectangle = new Rectangle2D.Double(barX, barY, barWidth, barHeight);
                    }

                    dataSetBarRectangles.add(barRectangle);

                    j++;
                }

                barRectangles.add(dataSetBarRectangles);

                i++;
            }
        } else {
            var columnWidth = getColumnWidth();

            var barWidth = (columnWidth / n) * 0.75;
            var barSpacing = (columnWidth - (barWidth * n)) / (n + 1);

            var i = 0;

            for (var dataSet : dataSets) {
                var dataSetBarRectangles = new ArrayList<Rectangle2D.Double>(keyCount);

                var dataPoints = dataSet.getDataPoints();

                var j = 0;

                for (var key : keys) {
                    var value = coalesce(map(dataPoints.get(key), Number::doubleValue), () -> 0.0);

                    var barHeight = Math.abs(value) * rangeScale;

                    Rectangle2D.Double barRectangle;
                    if (stacked) {
                        var barX = gridX + columnWidth * j + barSpacing;

                        double barY;
                        if (value > 0.0) {
                            var totalHeight = coalesce(positiveTotals.get(key), () -> 0.0) + barHeight;

                            barY = zeroY - totalHeight;

                            positiveTotals.put(key, totalHeight);
                        } else {
                            var totalHeight = coalesce(negativeTotals.get(key), () -> 0.0);

                            barY = zeroY + totalHeight;

                            negativeTotals.put(key, totalHeight + barHeight);
                        }

                        barRectangle = new Rectangle2D.Double(barX, barY, barWidth, barHeight);
                    } else {
                        var barX = gridX + columnWidth * j + barSpacing * (i + 1) + barWidth * i;

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
        }

        validateMarkers();
    }

    @Override
    void drawChart(Graphics2D graphics) {
        drawGrid(graphics);

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
                    graphics.setStroke(outlineStroke);

                    graphics.draw(barRectangle);
                }
            }

            i++;
        }

        drawZeroLine(graphics, getHorizontalGridLineColor(), outlineStroke);

        drawMarkers(graphics);
    }
}
