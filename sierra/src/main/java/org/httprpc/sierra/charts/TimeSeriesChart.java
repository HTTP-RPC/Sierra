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

import org.httprpc.sierra.RowPanel;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

/**
 * Time series chart.
 */
public class TimeSeriesChart<K extends Comparable<K>, V extends Number> extends Chart<K, V> {
    private static class LegendIcon implements Icon {
        DataSet<?, ?> dataSet;

        Line2D.Double shape = new Line2D.Double();

        static final int SIZE = 16;

        LegendIcon(DataSet<?, ?> dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            paintIcon((Graphics2D)graphics, x, y);
        }

        void paintIcon(Graphics2D graphics, int x, int y) {
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

    private RowPanel legendPanel = new RowPanel();

    @Override
    protected void validate() {
        legendPanel.removeAll();

        var dataSets = getDataSets();

        var n = dataSets.size();

        var legendFont = getLegendFont();

        for (var i = 0; i < n; i++) {
            var dataSet = dataSets.get(i);

            // TODO

            var legendLabel = new JLabel(dataSet.getLabel(), new LegendIcon(dataSet), SwingConstants.CENTER);

            legendLabel.setIconTextGap(8);
            legendLabel.setFont(legendFont);

            legendPanel.add(legendLabel);
        }

        var width = getWidth();
        var height = getHeight();

        var legendSize = legendPanel.getPreferredSize();

        legendPanel.setLocation(width / 2 - legendSize.width / 2, height - legendSize.height);
        legendPanel.setSize(legendSize);

        legendPanel.setSpacing(16);

        legendPanel.setComponentOrientation(getComponentOrientation());

        legendPanel.doLayout();
    }

    @Override
    protected void draw(Graphics2D graphics) {
        // TODO

        paintComponent(graphics, legendPanel);
    }
}
