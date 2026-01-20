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

import org.httprpc.sierra.TextPane;

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

    protected List<JLabel> domainMarkerLabels = listOf();
    protected List<Line2D.Double> domainMarkerLines = listOf();
    protected List<Shape> domainMarkerShapes = listOf();

    protected List<JLabel> rangeMarkerLabels = listOf();
    protected List<Line2D.Double> rangeMarkerLines = listOf();
    protected List<Shape> rangeMarkerShapes = listOf();

    private static final int MARKER_SCALE = 5;

    XYChart(Function<K, Number> domainValueTransform, Function<Number, K> domainKeyTransform) {
        this.domainValueTransform = domainValueTransform;
        this.domainKeyTransform = domainKeyTransform;
    }

    /**
     * Returns the domain value transform.
     *
     * @return
     * The domain value transform.
     */
    public Function<K, Number> getDomainValueTransform() {
        return domainValueTransform;
    }

    /**
     * Returns the domain key transform.
     *
     * @return
     * The domain key transform.
     */
    public Function<Number, K> getDomainKeyTransform() {
        return domainKeyTransform;
    }

    @Override
    public K getDomainMinimum() {
        return domainKeyTransform.apply(domainMinimum);
    }

    @Override
    public K getDomainMaximum() {
        return domainKeyTransform.apply(domainMaximum);
    }

    @Override
    public void setDomainBounds(K domainMinimum, K domainMaximum) {
        if (domainMinimum != null) {
            this.domainMinimum = domainValueTransform.apply(domainMinimum).doubleValue();
        } else  {
            this.domainMinimum = Double.NaN;
        }

        if (domainMaximum != null) {
            this.domainMaximum = domainValueTransform.apply(domainMaximum).doubleValue();
        } else  {
            this.domainMaximum = Double.NaN;
        }
    }

    @Override
    protected void validateGrid() {
        var dataSets = getDataSets();

        var domainMinimum = Double.POSITIVE_INFINITY;
        var domainMaximum = Double.NEGATIVE_INFINITY;

        var rangeMinimum = Double.POSITIVE_INFINITY;
        var rangeMaximum = Double.NEGATIVE_INFINITY;

        for (var dataSet : dataSets) {
            var dataPoints = dataSet.getDataPoints();

            for (var entry : dataPoints.entrySet()) {
                var domainValue = map(entry.getKey(), domainValueTransform).doubleValue();

                domainMinimum = Math.min(domainMinimum, domainValue);
                domainMaximum = Math.max(domainMaximum, domainValue);

                var rangeValue = map(entry.getValue(), Number::doubleValue);

                if (rangeValue != null) {
                    rangeMinimum = Math.min(rangeMinimum, rangeValue);
                    rangeMaximum = Math.max(rangeMaximum, rangeValue);
                }
            }
        }

        if (domainMinimum > domainMaximum) {
            domainMinimum = 0.0;
            domainMaximum = 0.0;
        }

        if (Double.isNaN(this.domainMinimum)) {
            this.domainMinimum = domainMinimum;
        }

        if (Double.isNaN(this.domainMaximum)) {
            this.domainMaximum = domainMaximum;
        }

        if (rangeMinimum > rangeMaximum) {
            rangeMinimum = 0.0;
            rangeMaximum = 0.0;
        }

        if (Double.isNaN(this.rangeMinimum)) {
            this.rangeMinimum = rangeMinimum;
        }

        if (Double.isNaN(this.rangeMaximum)) {
            this.rangeMaximum = rangeMaximum;
        }

        super.validateGrid();
    }

    @Override
    protected int getColumnCount() {
        return getDomainLabelCount() - 1;
    }

    @Override
    protected void populateDomainLabels() {
        var domainLabelCount = getDomainLabelCount();

        var domainLabelTransform = getDomainLabelTransform();
        var domainLabelFont = getDomainLabelFont();

        var domainStep = (domainMaximum - domainMinimum) / (domainLabelCount - 1);

        for (var i = 0; i < domainLabelCount; i++) {
            var key = domainKeyTransform.apply(domainMinimum + domainStep * i);

            var textPane = new TextPane(domainLabelTransform.apply(key));

            textPane.setFont(domainLabelFont);

            domainLabelTextPanes.add(textPane);
        }
    }

    @Override
    protected void validateDomainLabels() {
        var domainLabelCount = getDomainLabelCount();

        var domainLabelX = chartOffset;
        var domainLabelY = chartHeight + DOMAIN_LABEL_SPACING + horizontalGridLineWidth;

        for (var i = 0; i < domainLabelCount; i++) {
            var textPane = domainLabelTextPanes.get(i);

            textPane.setSize(textPane.getPreferredSize());

            var size = textPane.getSize();

            int x;
            if (i == 0) {
                x = (int)domainLabelX;
            } else if (i < domainLabelCount - 1) {
                x = (int)domainLabelX - size.width / 2;
            } else {
                x = (int)domainLabelX - size.width;
            }

            textPane.setLocation(x, (int)domainLabelY);
            textPane.doLayout();

            domainLabelX += columnWidth;
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

        for (var domainMarker : getDomainMarkers()) {
            var key = domainMarker.key();

            if (key == null) {
                throw new UnsupportedOperationException("Marker key is not defined.");
            }

            var domainValue = map(key, domainValueTransform).doubleValue() - domainMinimum;

            var lineX = chartOffset + domainValue * domainScale;

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
            var labelY = chartHeight + horizontalGridLineWidth / 2 - (size.height + DOMAIN_LABEL_SPACING);

            label.setBounds(labelX, (int)labelY, size.width, size.height);

            domainMarkerLabels.add(label);

            var value = domainMarker.value();

            if (value != null) {
                var rangeValue = value.doubleValue();

                var valueY = zeroY - rangeValue * rangeScale;

                var diameter = getMarkerStroke().getLineWidth() * MARKER_SCALE;

                if (valueY < label.getY() - diameter) {
                    var line = new Line2D.Double(lineX, labelY - DOMAIN_LABEL_SPACING, lineX, valueY);

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

            label.setBounds((int)chartOffset + RANGE_LABEL_SPACING, (int)lineY - size.height / 2, size.width, size.height);

            rangeMarkerLabels.add(label);

            var key = rangeMarker.key();

            if (key != null) {
                var domainValue = domainValueTransform.apply(key).doubleValue() - domainMinimum;

                var valueX = chartOffset + domainValue * domainScale;

                var diameter = getMarkerStroke().getLineWidth() * MARKER_SCALE;

                if (valueX > label.getX() + label.getWidth() + diameter) {
                    var line = new Line2D.Double(chartOffset + label.getWidth() + RANGE_LABEL_SPACING * 2, lineY, valueX, lineY);

                    rangeMarkerLines.add(line);

                    var shape = new Ellipse2D.Double(valueX - diameter / 2, lineY - diameter / 2, diameter, diameter);

                    rangeMarkerShapes.add(shape);
                }
            }
        }
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
