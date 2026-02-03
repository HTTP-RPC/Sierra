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
import java.awt.geom.Line2D;
import java.util.List;
import java.util.SortedSet;

import static org.httprpc.kilo.util.Collections.*;

/**
 * Abstract base class for category charts.
 */
public abstract class CategoryChart<K extends Comparable<? super K>, V> extends Chart<K, V> {
    SortedSet<K> keys = sortedSetOf();

    private List<JLabel> rangeMarkerLabels = listOf();
    private List<Line2D.Double> rangeMarkerLines = listOf();

    CategoryChart() {
    }

    @Override
    SortedSet<K> getKeys() {
        return keys;
    }

    void validateMarkers() {
        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();

        var markerColor = getMarkerColor();
        var markerFont = getMarkerFont();

        var gridBounds = getGridBounds();

        var gridX = gridBounds.getX();
        var gridY = gridBounds.getY();

        var gridWidth = gridBounds.getWidth();
        var gridHeight = gridBounds.getHeight();

        var rangeScale = getRangeScale();

        var origin = getOrigin();

        var zeroX = origin.getX();
        var zeroY = origin.getY();

        for (var entry : getRangeMarkers().entrySet()) {
            var key = entry.getKey();
            var marker = entry.getValue();

            if (isTransposed()) {
                var lineX = zeroX + key * rangeScale;

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

                rangeMarkerLabels.add(label);

                var left = (int)gridX + SPACING;

                if (labelX < left) {
                    label.setLocation(left, (int)labelY);
                } else {
                    var right = (int)(gridX + gridWidth) - SPACING;

                    if (labelX + size.width > right) {
                        label.setLocation(right - size.width, (int)labelY);
                    } else {
                        var lineY1 = labelY - SPACING;
                        var lineY2 = gridY + SPACING;

                        if (lineY2 < lineY1) {
                            rangeMarkerLines.add(new Line2D.Double(lineX, lineY1, lineX, lineY2));
                        }
                    }
                }
            } else {
                var lineY = zeroY - key * rangeScale;

                var label = new JLabel(marker.label(), marker.icon(), SwingConstants.LEADING);

                label.setIconTextGap(2);

                label.setForeground(markerColor);
                label.setFont(markerFont);

                var size = label.getPreferredSize();

                var labelX = (int)gridX + SPACING;
                var labelY = (int)lineY - size.height / 2;

                label.setBounds(labelX, labelY, size.width, size.height);

                rangeMarkerLabels.add(label);

                var top = (int)gridY + SPACING;

                if (labelY < top) {
                    label.setLocation(labelX, top);
                } else {
                    var bottom = (int)(gridY + gridHeight) - SPACING;

                    if (labelY + size.height > bottom) {
                        label.setLocation(labelX, bottom - size.height);
                    } else {
                        var lineX1 = gridX + label.getWidth() + SPACING * 2;
                        var lineX2 = gridX + gridWidth - SPACING;

                        if (lineX2 > lineX1) {
                            rangeMarkerLines.add(new Line2D.Double(lineX1, lineY, lineX2, lineY));
                        }
                    }
                }
            }
        }
    }

    void drawMarkers(Graphics2D graphics) {
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
