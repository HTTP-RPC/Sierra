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

import javax.swing.JComponent;
import javax.swing.UIManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.function.Function;

import static org.httprpc.kilo.util.Collections.*;

/**
 * Abstract base class for charts.
 *
 * @param <K>
 * The key type.
 *
 * @param <V>
 * The value type.
 */
public abstract class Chart<K, V> {
    private Font domainAxisFont;
    private Font rangeAxisFont;
    private Font legendFont;
    private Font annotationFont;

    private Function<K, String> domainLabelTransform = null;
    private Function<V, String> rangeLabelTransform = null;

    private boolean showHorizontalGridLines = true;

    private Color horizontalGridLineColor = Color.LIGHT_GRAY;
    private BasicStroke horizontalGridLineStroke = new BasicStroke(1);

    private boolean showVerticalGridLines = true;

    private Color verticalGridLineColor = Color.LIGHT_GRAY;
    private BasicStroke verticalGridLineStroke = new BasicStroke();

    private List<DataSet<K, V>> dataSets = listOf();

    private List<DataPoint<K, Void>> domainMarkers = listOf();
    private List<DataPoint<Void, V>> rangeMarkers = listOf();

    private ComponentOrientation componentOrientation = ComponentOrientation.LEFT_TO_RIGHT;

    private int width = 0;
    private int height = 0;

    private boolean valid = false;

    Chart() {
        var font = UIManager.getFont("Label.font");

        var size = font.getSize2D();

        domainAxisFont = font.deriveFont(size - 2);
        rangeAxisFont = font.deriveFont(size - 1);
        legendFont = font;
        annotationFont = font.deriveFont(size - 3);
    }

    /**
     * Returns the domain axis font.
     *
     * @return
     * The domain axis font.
     */
    public Font getDomainAxisFont() {
        return domainAxisFont;
    }

    /**
     * Sets the domain axis font.
     *
     * @param domainAxisFont
     * The domain axis font.
     */
    public void setDomainAxisFont(Font domainAxisFont) {
        if (domainAxisFont == null) {
            throw new IllegalArgumentException();
        }

        this.domainAxisFont = domainAxisFont;

        valid = false;
    }

    /**
     * Returns the range axis font.
     *
     * @return
     * The range axis font.
     */
    public Font getRangeAxisFont() {
        return rangeAxisFont;
    }

    /**
     * Sets the range axis font.
     *
     * @param rangeAxisFont
     * The range axis font.
     */
    public void setRangeAxisFont(Font rangeAxisFont) {
        if (rangeAxisFont == null) {
            throw new IllegalArgumentException();
        }

        this.rangeAxisFont = rangeAxisFont;

        valid = false;
    }

    /**
     * Returns the legend font.
     *
     * @return
     * The legend font.
     */
    public Font getLegendFont() {
        return legendFont;
    }

    /**
     * Sets the legend font.
     *
     * @param legendFont
     * The legend font.
     */
    public void setLegendFont(Font legendFont) {
        if (legendFont == null) {
            throw new IllegalArgumentException();
        }

        this.legendFont = legendFont;

        valid = false;
    }

    /**
     * Returns the annotation font.
     *
     * @return
     * The annotation font.
     */
    public Font getAnnotationFont() {
        return annotationFont;
    }

    /**
     * Sets the annotation font.
     *
     * @param annotationFont
     * The annotation font.
     */
    public void setAnnotationFont(Font annotationFont) {
        if (annotationFont == null) {
            throw new IllegalArgumentException();
        }

        this.annotationFont = annotationFont;

        valid = false;
    }

    /**
     * Returns the domain label transform.
     *
     * @return
     * The domain label transform, or {@code null} if no transform is set.
     */
    public Function<K, String> getDomainLabelTransform() {
        return domainLabelTransform;
    }

    /**
     * Sets the domain label transform.
     *
     * @param domainLabelTransform
     * The domain label transform, or {@code null} for no transform.
     */
    public void setDomainLabelTransform(Function<K, String> domainLabelTransform) {
        this.domainLabelTransform = domainLabelTransform;

        valid = false;
    }

    /**
     * Returns the range label transform.
     *
     * @return
     * The range label transform, or {@code null} if no transform is set.
     */
    public Function<V, String> getRangeLabelTransform() {
        return rangeLabelTransform;
    }

    /**
     * Sets the range label transform.
     *
     * @param rangeLabelTransform
     * The range label transform, or {@code null} for no transform.
     */
    public void setRangeLabelTransform(Function<V, String> rangeLabelTransform) {
        this.rangeLabelTransform = rangeLabelTransform;

        valid = false;
    }

    /**
     * Indicates that horizontal grid lines will be shown.
     *
     * @return
     * {@code true} if horizontal grid lines are enabled; {@code false},
     * otherwise.
     */
    public boolean getShowHorizontalGridLines() {
        return showHorizontalGridLines;
    }

    /**
     * Toggles horizontal grid line visibility.
     *
     * @param showHorizontalGridLines
     * {@code true} to show horizontal grid lines; {@code false} to hide them.
     */
    public void setShowHorizontalGridLines(boolean showHorizontalGridLines) {
        this.showHorizontalGridLines = showHorizontalGridLines;
    }

    /**
     * Returns the horizontal grid line color.
     *
     * @return
     * The horizontal grid line color.
     */
    public Color getHorizontalGridLineColor() {
        return horizontalGridLineColor;
    }

    /**
     * Sets the horizontal grid line color.
     *
     * @param horizontalGridLineColor
     * The horizontal grid line color.
     */
    public void setHorizontalGridLineColor(Color horizontalGridLineColor) {
        if (horizontalGridLineColor == null) {
            throw new IllegalArgumentException();
        }

        this.horizontalGridLineColor = horizontalGridLineColor;
    }

    /**
     * Returns the horizontal grid line stroke.
     *
     * @return
     * The horizontal grid line stroke.
     */
    public BasicStroke getHorizontalGridLineStroke() {
        return horizontalGridLineStroke;
    }

    /**
     * Sets the horizontal grid line stroke.
     *
     * @param horizontalGridLineStroke
     * The horizontal grid line stroke.
     */
    public void setHorizontalGridLineStroke(BasicStroke horizontalGridLineStroke) {
        if (horizontalGridLineStroke == null) {
            throw new IllegalArgumentException();
        }

        this.horizontalGridLineStroke = horizontalGridLineStroke;

        valid = false;
    }

    /**
     * Indicates that vertical grid lines will be shown.
     *
     * @return
     * {@code true} if vertical grid lines are enabled; {@code false},
     * otherwise.
     */
    public boolean getShowVerticalGridLines() {
        return showVerticalGridLines;
    }

    /**
     * Toggles vertical grid line visibility.
     *
     * @param showVerticalGridLines
     * {@code true} to show vertical grid lines; {@code false} to hide them.
     */
    public void setShowVerticalGridLines(boolean showVerticalGridLines) {
        this.showVerticalGridLines = showVerticalGridLines;
    }

    /**
     * Returns the vertical grid line color.
     *
     * @return
     * The vertical grid line color.
     */
    public Color getVerticalGridLineColor() {
        return verticalGridLineColor;
    }

    /**
     * Sets the vertical grid line color.
     *
     * @param verticalGridLineColor
     * The vertical grid line color.
     */
    public void setVerticalGridLineColor(Color verticalGridLineColor) {
        if (verticalGridLineColor == null) {
            throw new IllegalArgumentException();
        }

        this.verticalGridLineColor = verticalGridLineColor;
    }

    /**
     * Returns the vertical grid line stroke.
     *
     * @return
     * The vertical grid line stroke.
     */
    public BasicStroke getVerticalGridLineStroke() {
        return verticalGridLineStroke;
    }

    /**
     * Sets the vertical grid line stroke.
     *
     * @param verticalGridLineStroke
     * The vertical grid line stroke.
     */
    public void setVerticalGridLineStroke(BasicStroke verticalGridLineStroke) {
        if (verticalGridLineStroke == null) {
            throw new IllegalArgumentException();
        }

        this.verticalGridLineStroke = verticalGridLineStroke;

        valid = false;
    }

    /**
     * Returns the chart's data sets.
     *
     * @return
     * The chart's data sets.
     */
    public List<DataSet<K, V>> getDataSets() {
        return dataSets;
    }

    /**
     * Sets the chart's data sets.
     *
     * @param dataSets
     * The chart's data sets.
     */
    public void setDataSets(List<DataSet<K, V>> dataSets) {
        if (dataSets == null) {
            throw new IllegalArgumentException();
        }

        this.dataSets = dataSets;

        valid = false;
    }

    /**
     * Returns the domain markers.
     *
     * @return
     * The domain markers.
     */
    public List<DataPoint<K, Void>> getDomainMarkers() {
        return domainMarkers;
    }

    /**
     * Sets the domain markers.
     *
     * @param domainMarkers
     * The domain markers.
     */
    public void setDomainMarkers(List<DataPoint<K, Void>> domainMarkers) {
        if (domainMarkers == null) {
            throw new IllegalArgumentException();
        }

        this.domainMarkers = domainMarkers;

        valid = false;
    }

    /**
     * Returns the range markers.
     *
     * @return
     * The range markers.
     */
    public List<DataPoint<Void, V>> getRangeMarkers() {
        return rangeMarkers;
    }

    /**
     * Sets the range markers.
     *
     * @param rangeMarkers
     * The range markers.
     */
    public void setRangeMarkers(List<DataPoint<Void, V>> rangeMarkers) {
        if (rangeMarkers == null) {
            throw new IllegalArgumentException();
        }

        this.rangeMarkers = rangeMarkers;

        valid = false;
    }

    /**
     * Returns the chart's component orientation.
     *
     * @return
     * The chart's component orientation.
     */
    public ComponentOrientation getComponentOrientation() {
        return componentOrientation;
    }

    /**
     * Sets the chart's component orientation.
     *
     * @param componentOrientation
     * The chart's component orientation.
     */
    public void setComponentOrientation(ComponentOrientation componentOrientation) {
        if (componentOrientation == null) {
            throw new IllegalArgumentException();
        }

        this.componentOrientation = componentOrientation;

        valid = false;
    }

    /**
     * Draws the chart.
     *
     * @param graphics
     * The graphics context in which the chart will be drawn.
     *
     * @param width
     * The chart width.
     *
     * @param height
     * The chart height.
     */
    public void draw(Graphics2D graphics, int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }

        valid &= (width == this.width && height == this.height);

        this.width = width;
        this.height = height;

        if (!valid) {
            validate();
        }

        valid = true;

        graphics.setRenderingHints(new RenderingHints(mapOf(
            entry(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON),
            entry(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT),
            entry(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT)
        )));

        draw(graphics);
    }

    /**
     * Returns the chart width.
     *
     * @return
     * The chart width.
     */
    protected int getWidth() {
        return width;
    }

    /**
     * Returns the chart height.
     *
     * @return
     * The chart height.
     */
    protected int getHeight() {
        return height;
    }

    /**
     * Validates the chart contents.
     */
    protected abstract void validate();

    /**
     * Draws the chart.
     *
     * @param graphics
     * The graphics context in which the chart will be drawn.
     */
    protected abstract void draw(Graphics2D graphics);

    /**
     * Paints a component.
     *
     * @param graphics
     * The graphics context in which the component will be painted.
     *
     * @param component
     * The component to paint.
     */
    protected void paintComponent(Graphics2D graphics, JComponent component) {
        if (graphics == null || component == null) {
            throw new IllegalArgumentException();
        }

        graphics = (Graphics2D)graphics.create();

        graphics.translate(component.getX(), component.getY());

        component.paint(graphics);

        graphics.dispose();
    }
}
