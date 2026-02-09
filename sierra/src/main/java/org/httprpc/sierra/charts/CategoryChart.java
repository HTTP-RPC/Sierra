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

import static org.httprpc.kilo.util.Collections.*;

/**
 * Abstract base class for category charts.
 */
public abstract class CategoryChart<K extends Comparable<? super K>, V> extends Chart<K, V> {
    private List<JLabel> domainMarkerLabels = listOf();

    CategoryChart() {
    }

    @Override
    void validateMarkers() {
        super.validateMarkers();

        domainMarkerLabels.clear();

        var markerFont = getMarkerFont();

        var gridBounds = getGridBounds();

        var gridX = gridBounds.getX();
        var gridY = gridBounds.getY();

        var gridHeight = gridBounds.getHeight();

        var columnWidth = getColumnWidth();
        var rowHeight = getRowHeight();

        var j = 0;

        var domainMarkers = getDomainMarkers();

        for (var key : getKeys()) {
            var marker = domainMarkers.get(key);

            if (marker != null) {
                var label = new JLabel(marker.text(), marker.icon(), SwingConstants.CENTER);

                label.setHorizontalTextPosition(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setVerticalTextPosition(SwingConstants.BOTTOM);
                label.setIconTextGap(2);

                label.setFont(markerFont);

                var size = label.getPreferredSize();

                double labelX;
                double labelY;
                if (isTransposed()) {
                    labelX = gridX + SPACING;
                    labelY = gridY + rowHeight * j + (rowHeight - (double)size.height) / 2;
                } else {
                    labelX = gridX + columnWidth * j + (columnWidth - (double)size.width) / 2;
                    labelY = gridY + gridHeight - (size.height + SPACING);
                }

                label.setBounds((int)Math.round(labelX), (int)Math.round(labelY), size.width, size.height);

                domainMarkerLabels.add(label);
            }

            j++;
        }
    }

    @Override
    void drawMarkers(Graphics2D graphics) {
        super.drawMarkers(graphics);

        var markerColor = getMarkerColor();

        for (var label : domainMarkerLabels) {
            label.setForeground(markerColor);

            paintComponent(graphics, label);
        }
    }
}
