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
import java.util.List;
import java.util.function.Function;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Abstract base class for XY charts.
 */
public abstract class XYChart<K extends Comparable<? super K>, V extends Number> extends Chart<K, V> {
    Function<K, Number> domainValueTransform;
    Function<Number, K> domainKeyTransform;

    private List<JLabel> domainMarkerLabels = listOf();

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
    @Override
    public Function<K, Number> getDomainValueTransform() {
        return domainValueTransform;
    }

    /**
     * Returns the domain key transform.
     *
     * @return
     * The domain key transform.
     */
    @Override
    public Function<Number, K> getDomainKeyTransform() {
        return domainKeyTransform;
    }

    @Override
    void validateGrid() {
        var dataSets = getDataSets();

        var domainBounds = getDomainBounds();

        K domainMinimum = null;
        K domainMaximum = null;

        var rangeBounds = getRangeBounds();

        var rangeMinimum = Double.POSITIVE_INFINITY;
        var rangeMaximum = Double.NEGATIVE_INFINITY;

        for (var dataSet : dataSets) {
            var dataPoints = dataSet.getDataPoints();

            for (var entry : dataPoints.entrySet()) {
                if (domainBounds == null) {
                    var key = entry.getKey();

                    if (domainMinimum == null || key.compareTo(domainMinimum) < 0) {
                        domainMinimum = key;
                    }

                    if (domainMaximum == null || key.compareTo(domainMaximum) > 0) {
                        domainMaximum = key;
                    }
                }

                if (rangeBounds == null) {
                    var rangeValue = map(entry.getValue(), Number::doubleValue);

                    if (rangeValue != null) {
                        rangeMinimum = Math.min(rangeMinimum, rangeValue);
                        rangeMaximum = Math.max(rangeMaximum, rangeValue);
                    }
                }
            }
        }

        if (domainBounds == null && domainMinimum != null && domainMaximum != null) {
            setDomainBounds(new Bounds<>(domainMinimum, domainMaximum));
        }

        if (rangeBounds == null && rangeMinimum <= rangeMaximum) {
            setRangeBounds(adjustBounds(rangeMinimum, rangeMaximum));
        }

        super.validateGrid();
    }

    @Override
    void validateMarkers() {
        super.validateMarkers();

        domainMarkerLabels.clear();

        var markerColor = getMarkerColor();
        var markerFont = getMarkerFont();

        var gridBounds = getGridBounds();

        var gridY = gridBounds.getY();

        var gridHeight = gridBounds.getHeight();

        var domainScale = getDomainScale();

        var zeroX = getOrigin().getX();

        for (var entry : getDomainMarkers().entrySet()) {
            var key = entry.getKey();
            var marker = entry.getValue();

            var domainValue = domainValueTransform.apply(key).doubleValue();

            var lineX = zeroX + domainValue * domainScale;

            var label = new JLabel(marker.label(), marker.icon(), SwingConstants.CENTER);

            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.BOTTOM);
            label.setIconTextGap(2);

            label.setForeground(markerColor);
            label.setFont(markerFont);

            var size = label.getPreferredSize();

            var labelX = (int)Math.round(lineX - (double)size.width / 2);
            var labelY = gridY + gridHeight - (size.height + SPACING);

            label.setBounds(labelX, (int)labelY, size.width, size.height);

            domainMarkerLabels.add(label);
        }
    }

    @Override
    void drawGrid(Graphics2D graphics) {
        drawZeroLine(graphics, colorWithAlpha(getHorizontalGridLineColor(), 0x80), getHorizontalGridLineStroke());

        super.drawGrid(graphics);
    }

    @Override
    void drawMarkers(Graphics2D graphics) {
        super.drawMarkers(graphics);

        for (var domainMarkerLabel : domainMarkerLabels) {
            paintComponent(graphics, domainMarkerLabel);
        }
    }
}
