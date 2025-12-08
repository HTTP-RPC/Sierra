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

package org.httprpc.sierra;

import org.httprpc.sierra.charts.Chart;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Displays a chart.
 */
public class ChartPane extends JComponent {
    private class ChartPaneUI extends ComponentUI {
        @Override
        public Dimension getMinimumSize(JComponent component) {
            return new Dimension(0, 0);
        }

        @Override
        public Dimension getMaximumSize(JComponent component) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Override
        public Dimension getPreferredSize(JComponent component) {
            if (chart == null) {
                return new Dimension(0, 0);
            }

            var insets = getInsets();

            var chartWidth = chart.getWidth();
            var chartHeight = chart.getHeight();

            var preferredWidth = chartWidth + (insets.left + insets.right);
            var preferredHeight = chartHeight + (insets.top + insets.bottom);

            return new Dimension(preferredWidth, preferredHeight);
        }

        @Override
        public int getBaseline(JComponent component, int width, int height) {
            return -1;
        }

        @Override
        public void paint(Graphics graphics, JComponent component) {
            paint((Graphics2D)graphics);
        }

        private void paint(Graphics2D graphics) {
            if (chart == null) {
                return;
            }

            chart.draw(graphics);
        }
    }

    private Chart chart;

    /**
     * Constructs a new chart pane.
     */
    public ChartPane() {
        this(null);
    }

    /**
     * Constructs a new chart pane.
     *
     * @param chart
     * The chart to display, or {@code null} for no chart.
     */
    public ChartPane(Chart chart) {
        this.chart = chart;

        setUI(new ChartPaneUI());
    }

    /**
     * Returns the chart displayed by the component.
     *
     * @return
     * The chart displayed by the component.
     */
    public Chart getImage() {
        return chart;
    }

    /**
     * Sets the chart displayed by the component.
     *
     * @param chart
     * The chart to display, or {@code null} for no chart.
     */
    public void setChart(Chart chart) {
        this.chart = chart;

        revalidate();
    }
}
