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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.List;

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
    /**
     * Orientation options.
     */
    public enum Orientation {
        /**
         * Horizontal orientation.
         */
        HORIZONTAL,

        /**
         * Vertical orientation.
         */
        VERTICAL
    }

    /**
     * Range axis options.
     */
    public enum RangeAxis {
        /**
         * Leading axis.
         */
        LEADING,

        /**
         * Trailing axis.
         */
        TRAILING
    }

    private int width = 0;
    private int height = 0;

    private String heading = null;

    private Color headingColor = null;
    private Font headingFont = null;

    private Color horizontalGridColor = null;
    private Stroke horizontalGridStroke = null;

    private Color verticalGridColor = null;
    private Stroke verticalGridStroke = null;

    private Font domainAxisFont = null;
    private Font rangeAxisFont = null;
    private Font legendFont = null;
    private Font annotationFont = null;

    private boolean showDomainLabels = true;

    private List<DataSet<K, V>> dataSets = listOf();

    /**
     * Returns the chart's width.
     *
     * @return
     * The chart width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the chart's height.
     *
     * @return
     * The chart height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the chart's size.
     *
     * @param width
     * The chart width.
     *
     * @param height
     * The chart height.
     */
    public void setSize(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }

        this.width = width;
        this.height = height;
    }

    /**
     * Returns the chart heading.
     *
     * @return
     * The chart heading.
     */
    public String getHeading() {
        return heading;
    }

    /**
     * Sets the chart heading.
     *
     * @param heading
     * The chart heading.
     */
    public void setHeading(String heading) {
        this.heading = heading;
    }

    /**
     * Returns the heading color.
     *
     * @return
     * The heading color.
     */
    public Color getHeadingColor() {
        return headingColor;
    }

    /**
     * Sets the heading color.
     *
     * @param headingColor
     * The heading color.
     */
    public void setHeadingColor(Color headingColor) {
        this.headingColor = headingColor;
    }

    /**
     * Returns the heading font.
     *
     * @return
     * The heading font.
     */
    public Font getHeadingFont() {
        return headingFont;
    }

    /**
     * Sets the heading font.
     *
     * @param headingFont
     * The heading font.
     */
    public void setHeadingFont(Font headingFont) {
        this.headingFont = headingFont;
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
        this.horizontalGridStroke = horizontalGridStroke;
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
        this.verticalGridStroke = verticalGridStroke;
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
        this.domainAxisFont = domainAxisFont;
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
        this.rangeAxisFont = rangeAxisFont;
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
        this.legendFont = legendFont;
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
        this.annotationFont = annotationFont;
    }

    /**
     * Indicates that domain labels will be shown.
     *
     * @return
     * {@code true} if domain labels will be shown; {@code false}, otherwise.
     */
    public boolean getShowDomainLabels() {
        return showDomainLabels;
    }

    /**
     * Toggles domain label visibility.
     *
     * @param showDomainLabels
     * {@code true} to show domain labels; {@code false} to hide them.
     */
    public void setShowDomainLabels(boolean showDomainLabels) {
        this.showDomainLabels = showDomainLabels;
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
     * Generates an image from the chart.
     *
     * @return
     * The generated image.
     */
    public Image toImage() {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        var graphics = image.createGraphics();

        draw(graphics);

        graphics.dispose();

        return image;
    }

    /**
     * Draws the chart.
     *
     * @param graphics
     * The graphics context in which the chart will be drawn.
     */
    public abstract void draw(Graphics2D graphics);
}
