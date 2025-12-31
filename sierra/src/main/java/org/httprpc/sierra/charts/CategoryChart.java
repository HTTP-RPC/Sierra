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
import java.awt.geom.Line2D;
import java.util.List;
import java.util.SortedSet;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Abstract base class for category charts.
 */
public abstract class CategoryChart<K extends Comparable<? super K>, V> extends Chart<K, V> {
    protected SortedSet<K> keys = sortedSetOf();

    protected List<JLabel> rangeMarkerLabels = listOf();
    protected List<Line2D.Double> rangeMarkerLines = listOf();

    CategoryChart() {
    }

    @Override
    protected int getColumnCount() {
        return keys.isEmpty() ? 1 : keys.size();
    }

    @Override
    protected void populateDomainLabels() {
        var domainLabelTransform = getDomainLabelTransform();
        var domainLabelFont = getDomainLabelFont();

        if (keys.isEmpty()) {
            var textPane = new TextPane("");

            textPane.setFont(domainLabelFont);

            domainLabelTextPanes.add(textPane);
        } else {
            for (var key : keys) {
                var textPane = new TextPane(domainLabelTransform.apply(key));

                textPane.setFont(domainLabelFont);

                domainLabelTextPanes.add(textPane);
            }
        }
    }

    @Override
    protected void validateDomainLabels() {
        var keyCount = keys.size();

        var maximumWidth = 0.0;

        for (var i = 0; i < keyCount; i++) {
            var textPane = domainLabelTextPanes.get(i);

            textPane.setSize(textPane.getPreferredSize());

            var size = textPane.getSize();

            maximumWidth = Math.max(maximumWidth, size.width);
        }

        var showDomainLabels = maximumWidth < columnWidth * 0.85;

        var domainLabelX = chartOffset;
        var domainLabelY = chartHeight + DOMAIN_LABEL_SPACING + horizontalGridLineWidth;

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
    }

    protected void validateMarkers(double rangeScale) {
        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();

        var rangeLabelTransform = getRangeLabelTransform();

        var markerColor = getMarkerColor();
        var markerFont = getMarkerFont();

        for (var rangeMarker : getRangeMarkers()) {
            var value = map(rangeMarker.value(), Number::doubleValue);

            if (value == null) {
                throw new UnsupportedOperationException("Marker value is not defined.");
            }

            var lineY = zeroY - value * rangeScale;

            var text = coalesce(rangeMarker.label(), () -> rangeLabelTransform.apply(value));

            var label = new JLabel(text, rangeMarker.icon(), SwingConstants.LEADING);

            label.setForeground(markerColor);
            label.setFont(markerFont);

            var size = label.getPreferredSize();

            label.setBounds((int)chartOffset + RANGE_LABEL_SPACING, (int)lineY - size.height / 2, size.width, size.height);

            rangeMarkerLabels.add(label);

            var lineX1 = chartOffset + label.getWidth() + RANGE_LABEL_SPACING * 2;
            var lineX2 = width - RANGE_LABEL_SPACING - verticalGridLineWidth / 2;

            if (lineX2 > lineX1) {
                var line = new Line2D.Double(lineX1, lineY, lineX2, lineY);

                rangeMarkerLines.add(line);
            }
        }
    }

    protected void drawMarkers(Graphics2D graphics) {
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
