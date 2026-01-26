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

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.function.Function;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Abstract base class for XY charts.
 */
public abstract class XYChart<K extends Comparable<? super K>, V extends Number> extends Chart<K, V> {
    protected Function<K, Number> domainValueTransform;
    protected Function<Number, K> domainKeyTransform;

    private Line2D.Double zeroLine = null;

    private List<JLabel> domainMarkerLabels = listOf();
    private List<Line2D.Double> domainMarkerLines = listOf();
    private List<Shape> domainMarkerShapes = listOf();

    private List<JLabel> rangeMarkerLabels = listOf();
    private List<Line2D.Double> rangeMarkerLines = listOf();
    private List<Shape> rangeMarkerShapes = listOf();

    private static final int MARKER_SCALE = 5;

    XYChart(Function<K, Number> domainValueTransform, Function<Number, K> domainKeyTransform) {
        this.domainValueTransform = domainValueTransform;
        this.domainKeyTransform = domainKeyTransform;
    }

    @Override
    public Function<K, Number> getDomainValueTransform() {
        return domainValueTransform;
    }

    @Override
    public Function<Number, K> getDomainKeyTransform() {
        return domainKeyTransform;
    }

    @Override
    protected void validateGrid() {
        zeroLine = null;

        var dataSets = getDataSets();

        K domainMinimum = null;
        K domainMaximum = null;

        var rangeMinimum = Double.POSITIVE_INFINITY;
        var rangeMaximum = Double.NEGATIVE_INFINITY;

        for (var dataSet : dataSets) {
            var dataPoints = dataSet.getDataPoints();

            for (var entry : dataPoints.entrySet()) {
                var key = entry.getKey();

                if (domainMinimum == null || key.compareTo(domainMinimum) < 0) {
                    domainMinimum = key;
                }

                if (domainMaximum == null || key.compareTo(domainMaximum) > 0) {
                    domainMaximum = key;
                }

                var rangeValue = map(entry.getValue(), Number::doubleValue);

                if (rangeValue != null) {
                    rangeMinimum = Math.min(rangeMinimum, rangeValue);
                    rangeMaximum = Math.max(rangeMaximum, rangeValue);
                }
            }
        }

        if (getDomainBounds() == null && domainMinimum != null && domainMaximum != null) {
            setDomainBounds(new Bounds<>(domainMinimum, domainMaximum));
        }

        if (getRangeBounds() == null && rangeMinimum <= rangeMaximum) {
            setRangeBounds(new Bounds<>(rangeMinimum, rangeMaximum));
        }

        super.validateGrid();

        if (zeroY > chartY && zeroY < chartY + chartHeight) {
            zeroLine = new Line2D.Double(chartX, zeroY, chartX + chartWidth, zeroY);
        }
    }

    /**
     * Validates the chart markers.
     */
    protected void validateMarkers() {
        domainMarkerLabels.clear();
        domainMarkerLines.clear();
        domainMarkerShapes.clear();

        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();
        rangeMarkerShapes.clear();

        var domainLabelTransform = getDomainLabelTransform();
        var rangeLabelTransform = getRangeLabelTransform();

        var markerColor = getMarkerColor();
        var markerFont = getMarkerFont();

        var domainMinimum = domainValueTransform.apply(getDomainBounds().minimum()).doubleValue();

        for (var domainMarker : getDomainMarkers()) {
            var key = domainMarker.key();

            if (key == null) {
                throw new UnsupportedOperationException("Marker key is not defined.");
            }

            var domainValue = map(key, domainValueTransform).doubleValue() - domainMinimum;

            var lineX = chartX + domainValue * domainScale;

            var text = coalesce(domainMarker.label(), () -> domainLabelTransform.apply(key));

            var label = new JLabel(text, domainMarker.icon(), SwingConstants.CENTER);

            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.BOTTOM);
            label.setIconTextGap(2);

            label.setForeground(markerColor);
            label.setFont(markerFont);

            var size = label.getPreferredSize();

            var labelX = (int)Math.round(lineX - (double)size.width / 2);
            var labelY = chartY + chartHeight - (size.height + SPACING);

            label.setBounds(labelX, (int)labelY, size.width, size.height);

            domainMarkerLabels.add(label);

            var value = domainMarker.value();

            if (value != null) {
                var rangeValue = value.doubleValue();

                var valueY = zeroY - rangeValue * rangeScale;

                var diameter = getMarkerStroke().getLineWidth() * MARKER_SCALE;

                if (valueY < label.getY() - diameter) {
                    var line = new Line2D.Double(lineX, labelY - SPACING, lineX, valueY);

                    domainMarkerLines.add(line);

                    var shape = new Ellipse2D.Double(lineX - diameter / 2, valueY - diameter / 2, diameter, diameter);

                    domainMarkerShapes.add(shape);
                }
            }
        }

        for (var rangeMarker : getRangeMarkers()) {
            var value = rangeMarker.value();

            if (value == null) {
                throw new UnsupportedOperationException("Marker value is not defined.");
            }

            var rangeValue = value.doubleValue();

            var lineY = zeroY - rangeValue * rangeScale;

            var text = coalesce(rangeMarker.label(), () -> rangeLabelTransform.apply(rangeValue));

            var label = new JLabel(text, rangeMarker.icon(), SwingConstants.LEADING);

            label.setForeground(markerColor);
            label.setFont(markerFont);
            label.setIconTextGap(2);

            var size = label.getPreferredSize();

            label.setBounds((int)chartX + SPACING, (int)lineY - size.height / 2, size.width, size.height);

            rangeMarkerLabels.add(label);

            var key = rangeMarker.key();

            if (key != null) {
                var domainValue = domainValueTransform.apply(key).doubleValue() - domainMinimum;

                var valueX = chartX + domainValue * domainScale;

                var diameter = getMarkerStroke().getLineWidth() * MARKER_SCALE;

                if (valueX > label.getX() + label.getWidth() + diameter) {
                    var line = new Line2D.Double(chartX + label.getWidth() + SPACING * 2, lineY, valueX, lineY);

                    rangeMarkerLines.add(line);

                    var shape = new Ellipse2D.Double(valueX - diameter / 2, lineY - diameter / 2, diameter, diameter);

                    rangeMarkerShapes.add(shape);
                }
            }
        }
    }

    @Override
    protected void drawGrid(Graphics2D graphics) {
        if (zeroLine != null) {
            graphics.setColor(colorWithAlpha(getHorizontalGridLineColor(), 0x80));
            graphics.setStroke(getHorizontalGridLineStroke());

            graphics.draw(zeroLine);
        }

        super.drawGrid(graphics);
    }

    /**
     * Draws the chart markers.
     */
    protected void drawMarkers(Graphics2D graphics) {
        graphics.setColor(getMarkerColor());
        graphics.setStroke(getMarkerStroke());

        for (var domainMarkerLabel : domainMarkerLabels) {
            paintComponent(graphics, domainMarkerLabel);
        }

        for (var domainMarkerLine : domainMarkerLines) {
            graphics.draw(domainMarkerLine);
        }

        for (var domainMarkerShape : domainMarkerShapes) {
            graphics.fill(domainMarkerShape);
        }

        for (var label : rangeMarkerLabels) {
            paintComponent(graphics, label);
        }

        for (var rangeMarkerLine : rangeMarkerLines) {
            graphics.draw(rangeMarkerLine);
        }

        for (var rangeMarkerShape : rangeMarkerShapes) {
            graphics.fill(rangeMarkerShape);
        }
    }
}
