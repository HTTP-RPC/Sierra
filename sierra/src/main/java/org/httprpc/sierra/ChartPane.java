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
            if (chart == null || sizeToFit) {
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

            var insets = getInsets();

            var width = Math.max(getWidth() - (insets.left + insets.right), 0);
            var height = Math.max(getHeight() - (insets.top + insets.bottom), 0);

            var chartWidth = (double)chart.getWidth();
            var chartHeight = (double)chart.getHeight();

            var x = switch (horizontalAlignment.getLocalizedValue(ChartPane.this)) {
                case LEFT -> 0;
                case RIGHT -> width - chartWidth;
                case CENTER -> (width - chartWidth) / 2;
                default -> throw new UnsupportedOperationException();
            };

            var y = switch (verticalAlignment) {
                case TOP -> 0;
                case BOTTOM -> height - chartHeight;
                case CENTER -> (height - chartHeight) / 2;
            };

            graphics = (Graphics2D)graphics.create();

            graphics.translate(x + insets.left, y + insets.top);

            chart.draw(graphics);

            graphics.dispose();
        }
    }

    private Chart chart;

    private boolean sizeToFit = false;

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

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
    public Chart getChart() {
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

    /**
     * Indicates that the chart will be sized to fit the available space.
     *
     * @return
     * {@code true} if the chart will be sized to fit; {@code false},
     * otherwise.
     */
    public boolean isSizeToFit() {
        return sizeToFit;
    }

    /**
     * Toggles chart resizing.
     *
     * @param sizeToFit
     * {@code true} to enable chart resizing; {@code false} to disable it.
     */
    public void setSizeToFit(boolean sizeToFit) {
        this.sizeToFit = sizeToFit;

        revalidate();
    }

    /**
     * Returns the horizontal alignment. The default value is
     * {@link HorizontalAlignment#CENTER}.
     *
     * @return
     * The horizontal alignment.
     */
    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Sets the horizontal alignment.
     *
     * @param horizontalAlignment
     * The horizontal alignment.
     */
    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        if (horizontalAlignment == null) {
            throw new IllegalArgumentException();
        }

        this.horizontalAlignment = horizontalAlignment;

        repaint();
    }

    /**
     * Returns the vertical alignment. The default value is
     * {@link VerticalAlignment#CENTER}.
     *
     * @return
     * The vertical alignment.
     */
    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the vertical alignment.
     *
     * @param verticalAlignment
     * The vertical alignment.
     */
    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        if (verticalAlignment == null) {
            throw new IllegalArgumentException();
        }

        this.verticalAlignment = verticalAlignment;

        repaint();
    }
}
