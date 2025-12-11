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

import javax.swing.UIManager;
import java.awt.BasicStroke;
import java.awt.Color;
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

    private int horizontalGridSpacing = 120;

    private Color horizontalGridColor = Color.GRAY;
    private BasicStroke horizontalGridStroke = new BasicStroke();

    private boolean showVerticalGridLines = true;

    private int verticalGridSpacing = 80;

    private Color verticalGridColor = Color.LIGHT_GRAY;
    private BasicStroke verticalGridStroke = new BasicStroke();

    private List<DataSet<K, V>> dataSets = listOf();

    private List<DataPoint<K, V>> markers = listOf();

    private int width = 0;
    private int height = 0;

    private boolean leftToRight = true;

    private boolean valid = false;

    /**
     * Constructs a new chart.
     */
    protected Chart() {
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
     * Returns the horizontal grid spacing.
     *
     * @return
     * The horizontal grid spacing.
     */
    public int getHorizontalGridSpacing() {
        return horizontalGridSpacing;
    }

    /**
     * Sets the horizontal grid spacing.
     *
     * @param horizontalGridSpacing
     * The horizontal grid spacing.
     */
    public void setHorizontalGridSpacing(int horizontalGridSpacing) {
        if (horizontalGridSpacing < 0) {
            throw new IllegalArgumentException();
        }

        this.horizontalGridSpacing = horizontalGridSpacing;

        valid = false;
    }

    /**
     * Returns the horizontal grid color.
     *
     * @return
     * The horizontal grid color.
     */
    public Color getHorizontalGridColor() {
        return horizontalGridColor;
    }

    /**
     * Sets the horizontal grid color.
     *
     * @param horizontalGridColor
     * The horizontal grid color.
     */
    public void setHorizontalGridColor(Color horizontalGridColor) {
        if (horizontalGridColor == null) {
            throw new IllegalArgumentException();
        }

        this.horizontalGridColor = horizontalGridColor;
    }

    /**
     * Returns the horizontal grid stroke.
     *
     * @return
     * The horizontal grid stroke.
     */
    public BasicStroke getHorizontalGridStroke() {
        return horizontalGridStroke;
    }

    /**
     * Sets the horizontal grid stroke.
     *
     * @param horizontalGridStroke
     * The horizontal grid stroke.
     */
    public void setHorizontalGridStroke(BasicStroke horizontalGridStroke) {
        if (horizontalGridStroke == null) {
            throw new IllegalArgumentException();
        }

        this.horizontalGridStroke = horizontalGridStroke;

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
     * Returns the vertical grid spacing.
     *
     * @return
     * The vertical grid spacing.
     */
    public int getVerticalGridSpacing() {
        return verticalGridSpacing;
    }

    /**
     * Sets the vertical grid spacing.
     *
     * @param verticalGridSpacing
     * The vertical grid spacing.
     */
    public void setVerticalGridSpacing(int verticalGridSpacing) {
        if (verticalGridSpacing < 0) {
            throw new IllegalArgumentException();
        }

        this.verticalGridSpacing = verticalGridSpacing;

        valid = false;
    }

    /**
     * Returns the vertical grid color.
     *
     * @return
     * The vertical grid color.
     */
    public Color getVerticalGridColor() {
        return verticalGridColor;
    }

    /**
     * Sets the vertical grid color.
     *
     * @param verticalGridColor
     * The vertical grid color.
     */
    public void setVerticalGridColor(Color verticalGridColor) {
        if (verticalGridColor == null) {
            throw new IllegalArgumentException();
        }

        this.verticalGridColor = verticalGridColor;
    }

    /**
     * Returns the vertical grid stroke.
     *
     * @return
     * The vertical grid stroke.
     */
    public BasicStroke getVerticalGridStroke() {
        return verticalGridStroke;
    }

    /**
     * Sets the vertical grid stroke.
     *
     * @param verticalGridStroke
     * The vertical grid stroke.
     */
    public void setVerticalGridStroke(BasicStroke verticalGridStroke) {
        if (verticalGridStroke == null) {
            throw new IllegalArgumentException();
        }

        this.verticalGridStroke = verticalGridStroke;

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
     * Returns the chart markers.
     *
     * @return
     * The chart markers.
     */
    public List<DataPoint<K, V>> getMarkers() {
        return markers;
    }

    /**
     * Sets the chart markers.
     *
     * @param markers
     * The chart markers.
     */
    public void setMarkers(List<DataPoint<K, V>> markers) {
        if (markers == null) {
            throw new IllegalArgumentException();
        }

        this.markers = markers;

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
     *
     * @param leftToRight
     * {@code true} if chart elements should be laid out in left-to-right
     * order; {@code false} for right-to-left order.
     */
    public void draw(Graphics2D graphics, int width, int height, boolean leftToRight) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }

        valid |= (width == this.width && height == this.height && leftToRight == this.leftToRight);

        this.width = width;
        this.height = height;

        this.leftToRight = leftToRight;

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
     * Indicates that chart elements should be laid out in left-to-right order.
     *
     * @return
     * {@code true} if chart elements should be laid out in left-to-right
     * order; {@code false} for right-to-left order.
     */
    protected boolean isLeftToRight() {
        return leftToRight;
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
}
