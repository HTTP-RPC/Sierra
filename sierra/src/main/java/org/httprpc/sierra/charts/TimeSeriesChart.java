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
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Time series chart.
 */
public class TimeSeriesChart<K extends Comparable<? super K>, V extends Number> extends XYChart<K, V> {
    /**
     * Time series chart legend icon.
     */
    public static class LegendIcon implements Icon {
        private DataSet<?, ?> dataSet;

        private Line2D.Double shape = new Line2D.Double();

        private static final int SIZE = 16;

        /**
         * Constructs a new time series chart legend icon.
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

    private boolean showValueMarkers = false;

    private List<Path2D.Double> paths = listOf();
    private List<List<Shape>> valueMarkerShapes = listOf();

    private static final int VALUE_MARKER_SCALE = 5;

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
        super(domainValueTransform, domainKeyTransform);
    }

    /**
     * Indicates that value markers will be shown. The default value is
     * {@code false}.
     *
     * @return
     * {@code true} if value markers will be shown; {@code false}, otherwise.
     */
    public boolean getShowValueMarkers() {
        return showValueMarkers;
    }

    /**
     * Toggles value marker visibility.
     *
     * @param showValueMarkers
     * {@code true} to show value markers; {@code false} to hide them.
     */
    public void setShowValueMarkers(boolean showValueMarkers) {
        this.showValueMarkers = showValueMarkers;
    }

    @Override
    public void validate() {
        paths.clear();
        valueMarkerShapes.clear();

        validateGrid();

        var gridX = getGridBounds().getX();

        var domainScale = getDomainScale();
        var rangeScale = getRangeScale();

        var zeroY = getOrigin().getY();

        var domainMinimum = domainValueTransform.apply(getDomainBounds().minimum()).doubleValue();

        for (var dataSet : getDataSets()) {
            var path = new Path2D.Double();
            var dataSetValueMarkerShapes = new ArrayList<Shape>(dataSet.getDataPoints().size());

            var i = 0;

            for (var entry : dataSet.getDataPoints().entrySet()) {
                var domainValue = map(entry.getKey(), domainValueTransform).doubleValue();

                var rangeValue = map(entry.getValue(), Number::doubleValue);

                if (rangeValue != null) {
                    var x = gridX + (domainValue - domainMinimum) * domainScale;
                    var y = zeroY - rangeValue * rangeScale;

                    if (i == 0) {
                        path.moveTo(x, y);
                    } else {
                        path.lineTo(x, y);
                    }

                    if (showValueMarkers) {
                        var diameter = dataSet.getStroke().getLineWidth() * VALUE_MARKER_SCALE;

                        var shape = new Ellipse2D.Double(x - diameter / 2, y - diameter / 2, diameter, diameter);

                        dataSetValueMarkerShapes.add(shape);
                    }
                }

                i++;
            }

            paths.add(path);
            valueMarkerShapes.add(dataSetValueMarkerShapes);
        }

        validateMarkers();
    }

    @Override
    void draw(Graphics2D graphics) {
        drawGrid(graphics);

        if (paths.isEmpty()) {
            return;
        }

        var i = 0;

        for (var dataSet : getDataSets()) {
            graphics.setColor(dataSet.getColor());
            graphics.setStroke(dataSet.getStroke());

            graphics.draw(paths.get(i));

            if (showValueMarkers) {
                for (var valueMarkerShape : valueMarkerShapes.get(i)) {
                    graphics.fill(valueMarkerShape);
                }
            }

            i++;
        }

        drawMarkers(graphics);
    }
}
