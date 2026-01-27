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
import javax.swing.UIManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Pie chart.
 */
public class PieChart<K extends Comparable<? super K>, V extends Number> extends Chart<K, V> {
    /**
     * Pie chart legend icon.
     */
    public static class LegendIcon implements Icon {
        private DataSet<?, ?> dataSet;

        private Ellipse2D.Double shape = new Ellipse2D.Double();

        private static final int SIZE = 12;

        /**
         * Constructs a new pie chart legend icon.
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
            shape.setFrame(x, y, SIZE, SIZE);

            graphics.setColor(dataSet.getColor());
            graphics.fill(shape);
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

    private boolean doughnut;

    private Color outlineColor = Color.WHITE;
    private BasicStroke outlineStroke = defaultOutlineStroke;

    private List<Arc2D.Double> sliceArcs = listOf();

    private Ellipse2D.Double cutoutShape = null;

    private static final BasicStroke defaultOutlineStroke;
    static {
        defaultOutlineStroke = new BasicStroke(1.25f);
    }

    /**
     * Constructs a new pie chart.
     */
    public PieChart() {
        this(false);
    }

    /**
     * Constructs a new pie chart.
     *
     * @param doughnut
     * {@code true} for a doughnut chart; {@code false}, otherwise.
     */
    public PieChart(boolean doughnut) {
        this.doughnut = doughnut;

        perform(UIManager.getColor("TextArea.background"), color -> outlineColor = color);
    }

    /**
     * Indicates that the chart is a doughnut.
     *
     * @return
     * {@code true} if the chart is a doughnut; {@code false}, otherwise.
     */
    public boolean isDoughnut() {
        return doughnut;
    }

    /**
     * Returns the outline color.
     *
     * @return
     * The outline color.
     */
    public Color getOutlineColor() {
        return outlineColor;
    }

    /**
     * Sets the outline color.
     *
     * @param outlineColor
     * The outline color.
     */
    public void setOutlineColor(Color outlineColor) {
        if (outlineColor == null) {
            throw new IllegalArgumentException();
        }

        this.outlineColor = outlineColor;
    }

    /**
     * Returns the outline stroke.
     *
     * @return
     * The outline stroke.
     */
    public BasicStroke getOutlineStroke() {
        return outlineStroke;
    }

    /**
     * Sets the outline stroke.
     *
     * @param outlineStroke
     * The outline stroke.
     */
    public void setOutlineStroke(BasicStroke outlineStroke) {
        if (outlineStroke == null) {
            throw new IllegalArgumentException();
        }

        this.outlineStroke = outlineStroke;
    }

    @Override
    public void validate() {
        sliceArcs.clear();

        cutoutShape = null;

        var dataSets = getDataSets();

        var n = dataSets.size();

        var dataSetValues = new ArrayList<Double>(n);

        var total = 0.0;

        for (var i = 0; i < n; i++) {
            var dataSet = dataSets.get(i);

            dataSetValues.add(0.0);

            for (var entry : dataSet.getDataPoints().entrySet()) {
                var value = coalesce(map(entry.getValue(), Number::doubleValue), () -> 0.0);

                if (value < 0.0) {
                    throw new UnsupportedOperationException("Negative value in data set.");
                }

                dataSetValues.set(i, dataSetValues.get(i) + value);

                total += value;
            }
        }

        if (total == 0.0) {
            return;
        }

        var size = getSize();

        var outlineWidth = outlineStroke.getLineWidth();

        var height = size.height - outlineWidth;

        var pieBounds = new Rectangle2D.Double((double)size.width / 2 - height / 2, outlineWidth / 2, height, height);

        var start = 90.0;

        for (var i = 0; i < n; i++) {
            var extent = -360.0 * (dataSetValues.get(i) / total);

            sliceArcs.add(new Arc2D.Double(pieBounds, start, extent, Arc2D.PIE));

            start += extent;
        }

        if (doughnut) {
            var cutoutSize = (double)height / 2;

            var x = pieBounds.getX() + (pieBounds.getWidth() / 2 - cutoutSize / 2);
            var y = pieBounds.y + (double)height / 2 - cutoutSize / 2;

            cutoutShape = new Ellipse2D.Double(x, y, cutoutSize, cutoutSize);
        }
    }

    @Override
    protected void draw(Graphics2D graphics) {
        var i = 0;

        for (var dataSet : getDataSets()) {
            var sliceArc = sliceArcs.get(i);

            graphics.setColor(dataSet.getColor());
            graphics.fill(sliceArc);

            graphics.setColor(outlineColor);
            graphics.setStroke(outlineStroke);
            graphics.draw(sliceArc);

            i++;
        }

        if (cutoutShape != null) {
            graphics.setColor(outlineColor);

            graphics.fill(cutoutShape);
        }
    }
}
