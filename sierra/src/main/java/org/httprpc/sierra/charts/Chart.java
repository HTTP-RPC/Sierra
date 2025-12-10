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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
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
    private String domainHeading = null;
    private Font domainHeadingFont = null;

    private Function<K, String> domainLabelTransform = null;
    private Font domainAxisFont = null;

    private String rangeHeading = null;
    private Font rangeHeadingFont = null;

    private Function<V, String> rangeLabelTransform = null;
    private Font rangeAxisFont = null;

    private Font legendFont = null;
    private Font annotationFont = null;

    private boolean showHorizontalGridLines = true;

    private int horizontalGridSpacing = 120;

    private Color horizontalGridColor = Color.GRAY;
    private Stroke horizontalGridStroke = new BasicStroke();

    private boolean showVerticalGridLines = true;

    private int verticalGridSpacing = 80;

    private Color verticalGridColor = Color.LIGHT_GRAY;
    private Stroke verticalGridStroke = new BasicStroke();

    private List<DataSet<K, V>> dataSets = listOf();

    private List<DataPoint<K, V>> markers = listOf();

    /**
     * Returns the domain heading.
     *
     * @return
     * The domain heading, or {@code null} if no heading is set.
     */
    public String getDomainHeading() {
        return domainHeading;
    }

    /**
     * Sets the domain heading.
     *
     * @param domainHeading
     * The domain heading, or {@code null} for no heading.
     */
    public void setDomainHeading(String domainHeading) {
        this.domainHeading = domainHeading;
    }

    /**
     * Returns the domain heading font.
     *
     * @return
     * The domain heading font, or {@code null} if no font is set.
     */
    public Font getDomainHeadingFont() {
        return domainHeadingFont;
    }

    /**
     * Sets the domain heading font.
     *
     * @param domainHeadingFont
     * The domain heading font, or {@code null} for the default font.
     */
    public void setDomainHeadingFont(Font domainHeadingFont) {
        this.domainHeadingFont = domainHeadingFont;
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
    }

    /**
     * Returns the domain axis font.
     *
     * @return
     * The domain axis font, or {@code null} if no font is set.
     */
    public Font getDomainAxisFont() {
        return domainAxisFont;
    }

    /**
     * Sets the domain axis font.
     *
     * @param domainAxisFont
     * The domain axis font, or {@code null} for the default font.
     */
    public void setDomainAxisFont(Font domainAxisFont) {
        this.domainAxisFont = domainAxisFont;
    }

    /**
     * Returns the range heading.
     *
     * @return
     * The range heading, or {@code null} if no heading is set.
     */
    public String getRangeHeading() {
        return rangeHeading;
    }

    /**
     * Sets the range heading.
     *
     * @param rangeHeading
     * The range heading, or {@code null} for no heading.
     */
    public void setRangeHeading(String rangeHeading) {
        this.rangeHeading = rangeHeading;
    }

    /**
     * Returns the range heading font.
     *
     * @return
     * The range heading font, or {@code null} if no font is set.
     */
    public Font getRangeHeadingFont() {
        return rangeHeadingFont;
    }

    /**
     * Sets the range heading font.
     *
     * @param rangeHeadingFont
     * The range heading font, or {@code null} for the default font.
     */
    public void setRangeHeadingFont(Font rangeHeadingFont) {
        this.rangeHeadingFont = rangeHeadingFont;
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
    }

    /**
     * Returns the range axis font.
     *
     * @return
     * The range axis font, or {@code null} if no font is set.
     */
    public Font getRangeAxisFont() {
        return rangeAxisFont;
    }

    /**
     * Sets the range axis font.
     *
     * @param rangeAxisFont
     * The range axis font, or {@code null} for the default font.
     */
    public void setRangeAxisFont(Font rangeAxisFont) {
        this.rangeAxisFont = rangeAxisFont;
    }

    /**
     * Returns the legend font.
     *
     * @return
     * The legend font, or {@code null} if no font is set.
     */
    public Font getLegendFont() {
        return legendFont;
    }

    /**
     * Sets the legend font.
     *
     * @param legendFont
     * The legend font, or {@code null} for the default font.
     */
    public void setLegendFont(Font legendFont) {
        this.legendFont = legendFont;
    }

    /**
     * Returns the annotation font.
     *
     * @return
     * The annotation font, or {@code null} if no font is set.
     */
    public Font getAnnotationFont() {
        return annotationFont;
    }

    /**
     * Sets the annotation font.
     *
     * @param annotationFont
     * The annotation font, or {@code null} for the default font.
     */
    public void setAnnotationFont(Font annotationFont) {
        this.annotationFont = annotationFont;
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
    public Stroke getHorizontalGridStroke() {
        return horizontalGridStroke;
    }

    /**
     * Sets the horizontal grid stroke.
     *
     * @param horizontalGridStroke
     * The horizontal grid stroke.
     */
    public void setHorizontalGridStroke(Stroke horizontalGridStroke) {
        if (horizontalGridStroke == null) {
            throw new IllegalArgumentException();
        }

        this.horizontalGridStroke = horizontalGridStroke;
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
    public Stroke getVerticalGridStroke() {
        return verticalGridStroke;
    }

    /**
     * Sets the vertical grid stroke.
     *
     * @param verticalGridStroke
     * The vertical grid stroke.
     */
    public void setVerticalGridStroke(Stroke verticalGridStroke) {
        if (verticalGridStroke == null) {
            throw new IllegalArgumentException();
        }

        this.verticalGridStroke = verticalGridStroke;
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
    public abstract void draw(Graphics2D graphics, int width, int height);
}
