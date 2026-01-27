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

import org.httprpc.sierra.Orientation;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
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

    private Orientation orientation;
    private boolean stacked;

    private double barTransparency = 1.0;

    private List<List<Rectangle2D.Double>> barRectangles = listOf();

    private static final BasicStroke outlineStroke;
    static {
        outlineStroke = new BasicStroke(1.0f);
    }

    /**
     * Constructs a new bar chart.
     */
    public BarChart() {
        this(Orientation.VERTICAL, false);
    }

    /**
     * Constructs a new bar chart.
     *
     * @param orientation
     * The chart's orientation.
     *
     * @param stacked
     * {@code true} for a stacked bar chart; {@code false}, otherwise.
     */
    public BarChart(Orientation orientation, boolean stacked) {
        if (orientation == null) {
            throw new IllegalArgumentException();
        }

        this.orientation = orientation;
        this.stacked = stacked;
    }

    /**
     * Returns the chart's orientation.
     *
     * @return
     * The chart's orientation.
     */
    public Orientation getOrientation() {
        return orientation;
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
    public void validate() {
        keys.clear();

        barRectangles.clear();

        var dataSets = getDataSets();

        var positiveTotals = new TreeMap<K, Double>();
        var negativeTotals = new TreeMap<K, Double>();

        var rangeMinimum = 0.0;
        var rangeMaximum = 0.0;

        for (var dataSet : dataSets) {
            for (var entry : dataSet.getDataPoints().entrySet()) {
                var key = entry.getKey();

                keys.add(key);

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

        if (stacked) {
            for (var value : positiveTotals.values()) {
                rangeMaximum = Math.max(rangeMaximum, value);
            }

            for (var value : negativeTotals.values()) {
                rangeMinimum = Math.min(rangeMinimum, value);
            }
        }

        if (getRangeBounds() == null) {
            setRangeBounds(new Bounds<>(rangeMinimum, rangeMaximum));
        }

        validateGrid();

        var keyCount = keys.size();

        if (keyCount == 0) {
            return;
        }

        positiveTotals.clear();
        negativeTotals.clear();

        var rangeScale = getRangeScale();

        var zeroY = getOrigin().getY();

        var n = stacked ? 1 : dataSets.size();

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

        validateMarkers();
    }

    @Override
    boolean isTransposed() {
        return orientation == Orientation.HORIZONTAL;
    }

    @Override
    protected void draw(Graphics2D graphics) {
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
