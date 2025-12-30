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
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.httprpc.kilo.util.Collections.*;

/**
 * Scatter chart.
 */
public class ScatterChart<K extends Comparable<? super K>, V extends Number> extends XYChart<K, Collection<V>> {
    /**
     * Scatter chart legend icon.
     */
    public static class LegendIcon implements Icon {
        private DataSet<?, ?> dataSet;

        private Ellipse2D.Double shape = new Ellipse2D.Double();

        private static final int SIZE = 12;

        /**
         * Constructs a new scatter chart legend icon.
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
            var lineWidth = outlineStroke.getLineWidth();

            shape.setFrame(x + lineWidth / 2, y + lineWidth / 2, SIZE - lineWidth, SIZE - lineWidth);

            graphics.setColor(dataSet.getColor());
            graphics.setStroke(outlineStroke);

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

    private double valueMarkerTransparency = 1.0;

    private Line2D.Double zeroLine = null;

    private List<List<Shape>> valueMarkerShapes = listOf();

    private static int VALUE_MARKER_SIZE = 10;

    private static final BasicStroke outlineStroke;
    static {
        outlineStroke = new BasicStroke(1.0f);
    }

    /**
     * Constructs a new scatter chart.
     *
     * @param domainValueTransform
     * The domain value transform.
     *
     * @param domainKeyTransform
     * The domain key transform.
     */
    public ScatterChart(Function<K, Number> domainValueTransform, Function<Number, K> domainKeyTransform) {
        super(domainValueTransform, domainKeyTransform);
    }

    /**
     * Returns the value marker transparency. The default value is 1.0.
     *
     * @return
     * The value marker transparency.
     */
    public double getValueMarkerTransparency() {
        return valueMarkerTransparency;
    }

    /**
     * Sets the value marker transparency.
     *
     * @param valueMarkerTransparency
     * The value marker transparency, as a value from 0.0 to 1.0.
     */
    public void setValueMarkerTransparency(double valueMarkerTransparency) {
        if (valueMarkerTransparency < 0.0 || valueMarkerTransparency > 1.0) {
            throw new IllegalArgumentException();
        }

        this.valueMarkerTransparency = valueMarkerTransparency;
    }

    @Override
    public void validate() {
        // TODO
    }

    @Override
    protected void draw(Graphics2D graphics) {
        // TODO
    }
}
