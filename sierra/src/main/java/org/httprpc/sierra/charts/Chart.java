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
import javax.swing.JComponent;
import javax.swing.UIManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.List;
import java.util.function.Function;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

/**
 * Abstract base class for charts.
 *
 * @param <K>
 * The key type.
 *
 * @param <V>
 * The value type.
 */
public abstract class Chart<K extends Comparable<? super K>, V> {
    /**
     * Represents a chart marker.
     *
     * @param key
     * The marker key.
     *
     * @param value
     * The marker value.
     *
     * @param label
     * The marker label.
     *
     * @param icon
     * The marker icon.
     */
    public record Marker<K>(
        K key,
        Number value,
        String label,
        Icon icon
    ) {
    }

    private int domainLabelCount = 5;

    private Function<K, String> domainLabelTransform = Object::toString;

    private Color domainLabelColor = Color.GRAY;
    private Font domainLabelFont = defaultDomainLabelFont;

    private int rangeLabelCount = 5;

    private Function<Number, String> rangeLabelTransform = numberFormat::format;

    private Color rangeLabelColor = Color.GRAY;
    private Font rangeLabelFont = defaultRangeLabelFont;

    private Color markerColor = Color.BLACK;
    private BasicStroke markerStroke = defaultMarkerStroke;
    private Font markerFont = defaultMarkerFont;

    private boolean showHorizontalGridLines = true;

    private Color horizontalGridLineColor = Color.LIGHT_GRAY;
    private BasicStroke horizontalGridLineStroke = defaultGridLineStroke;

    private boolean showVerticalGridLines = true;

    private Color verticalGridLineColor = Color.LIGHT_GRAY;
    private BasicStroke verticalGridLineStroke = defaultGridLineStroke;

    private List<DataSet<K, V>> dataSets = listOf();

    private List<Marker<K>> domainMarkers = listOf();
    private List<Marker<K>> rangeMarkers = listOf();

    private int width = 0;
    private int height = 0;

    private static final Font defaultDomainLabelFont;
    private static final Font defaultRangeLabelFont;
    private static final Font defaultMarkerFont;
    static {
        var font = UIManager.getFont("Label.font");

        var size = font.getSize2D();

        defaultDomainLabelFont = font.deriveFont(size - 1);
        defaultRangeLabelFont = font.deriveFont(size - 2);
        defaultMarkerFont = font.deriveFont(size - 3);
    }

    private static final BasicStroke defaultMarkerStroke;
    private static final BasicStroke defaultGridLineStroke;
    static {
        defaultMarkerStroke = new BasicStroke(1.0f);
        defaultGridLineStroke = new BasicStroke(1.0f);
    }

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();

    private static final RenderingHints renderingHints = new RenderingHints(mapOf(
        entry(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON),
        entry(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY),
        entry(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE),
        entry(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT),
        entry(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT)
    ));

    /**
     * Constructs a new chart.
     */
    protected Chart() {
        perform(UIManager.getColor("Label.disabledForeground"), color -> {
            domainLabelColor = color;
            rangeLabelColor = color;
        });

        perform(UIManager.getColor("Label.foreground"), color -> markerColor = colorWithAlpha(color, 0xaa));

        perform(UIManager.getFont("medium.font"), font -> domainLabelFont = font);
        perform(UIManager.getFont("small.font"), font -> rangeLabelFont = font);
        perform(UIManager.getFont("mini.font"), font -> markerFont = font);

        perform(UIManager.getColor("Component.borderColor"), color -> {
            horizontalGridLineColor = color;
            verticalGridLineColor = color;
        });
    }

    /**
     * Returns the domain label count.
     *
     * @return
     * The domain label count.
     */
    public int getDomainLabelCount() {
        return domainLabelCount;
    }

    /**
     * Sets the domain label count.
     *
     * @param domainLabelCount
     * The domain label count.
     */
    public void setDomainLabelCount(int domainLabelCount) {
        if (domainLabelCount < 2) {
            throw new IllegalArgumentException();
        }

        this.domainLabelCount = domainLabelCount;
    }

    /**
     * Returns the domain label transform.
     *
     * @return
     * The domain label transform.
     */
    public Function<K, String> getDomainLabelTransform() {
        return domainLabelTransform;
    }

    /**
     * Sets the domain label transform.
     *
     * @param domainLabelTransform
     * The domain label transform.
     */
    public void setDomainLabelTransform(Function<K, String> domainLabelTransform) {
        if (domainLabelTransform == null) {
            throw new IllegalArgumentException();
        }

        this.domainLabelTransform = domainLabelTransform;
    }

    /**
     * Returns the domain label color.
     *
     * @return
     * The domain label color.
     */
    public Color getDomainLabelColor() {
        return domainLabelColor;
    }

    /**
     * Sets the domain label color.
     *
     * @param domainLabelColor
     * The domain label color.
     */
    public void setDomainLabelColor(Color domainLabelColor) {
        if (domainLabelColor == null) {
            throw new IllegalArgumentException();
        }

        this.domainLabelColor = domainLabelColor;
    }

    /**
     * Returns the domain label font.
     *
     * @return
     * The domain label font.
     */
    public Font getDomainLabelFont() {
        return domainLabelFont;
    }

    /**
     * Sets the domain label font.
     *
     * @param domainLabelFont
     * The domain label font.
     */
    public void setDomainLabelFont(Font domainLabelFont) {
        if (domainLabelFont == null) {
            throw new IllegalArgumentException();
        }

        this.domainLabelFont = domainLabelFont;
    }

    /**
     * Returns the range label count.
     *
     * @return
     * The range label count.
     */
    public int getRangeLabelCount() {
        return rangeLabelCount;
    }

    /**
     * Sets the range label count.
     *
     * @param rangeLabelCount
     * The range label count.
     */
    public void setRangeLabelCount(int rangeLabelCount) {
        if (rangeLabelCount < 2) {
            throw new IllegalArgumentException();
        }

        this.rangeLabelCount = rangeLabelCount;
    }

    /**
     * Returns the range label transform.
     *
     * @return
     * The range label transform.
     */
    public Function<Number, String> getRangeLabelTransform() {
        return rangeLabelTransform;
    }

    /**
     * Sets the range label transform.
     *
     * @param rangeLabelTransform
     * The range label transform.
     */
    public void setRangeLabelTransform(Function<Number, String> rangeLabelTransform) {
        if (rangeLabelTransform == null) {
            throw new IllegalArgumentException();
        }

        this.rangeLabelTransform = rangeLabelTransform;
    }

    /**
     * Returns the range label color.
     *
     * @return
     * The range label color.
     */
    public Color getRangeLabelColor() {
        return rangeLabelColor;
    }

    /**
     * Sets the range label color.
     *
     * @param rangeLabelColor
     * The range label color.
     */
    public void setRangeLabelColor(Color rangeLabelColor) {
        if (rangeLabelColor == null) {
            throw new IllegalArgumentException();
        }

        this.rangeLabelColor = rangeLabelColor;
    }

    /**
     * Returns the range label font.
     *
     * @return
     * The range label font.
     */
    public Font getRangeLabelFont() {
        return rangeLabelFont;
    }

    /**
     * Sets the range label font.
     *
     * @param rangeLabelFont
     * The range label font.
     */
    public void setRangeLabelFont(Font rangeLabelFont) {
        if (rangeLabelFont == null) {
            throw new IllegalArgumentException();
        }

        this.rangeLabelFont = rangeLabelFont;
    }

    /**
     * Returns the marker color.
     *
     * @return
     * The marker color.
     */
    public Color getMarkerColor() {
        return markerColor;
    }

    /**
     * Sets the marker color.
     *
     * @param markerColor
     * The marker color.
     */
    public void setMarkerColor(Color markerColor) {
        if (markerColor == null) {
            throw new IllegalArgumentException();
        }

        this.markerColor = markerColor;
    }

    /**
     * Returns the marker stroke.
     *
     * @return
     * The marker stroke.
     */
    public BasicStroke getMarkerStroke() {
        return markerStroke;
    }

    /**
     * Sets the marker stroke.
     *
     * @param markerStroke
     * The marker stroke.
     */
    public void setMarkerStroke(BasicStroke markerStroke) {
        if (markerStroke == null) {
            throw new IllegalArgumentException();
        }

        this.markerStroke = markerStroke;
    }

    /**
     * Returns the marker font.
     *
     * @return
     * The marker font.
     */
    public Font getMarkerFont() {
        return markerFont;
    }

    /**
     * Sets the marker font.
     *
     * @param markerFont
     * The marker font.
     */
    public void setMarkerFont(Font markerFont) {
        if (markerFont == null) {
            throw new IllegalArgumentException();
        }

        this.markerFont = markerFont;
    }

    /**
     * Indicates that horizontal grid lines will be shown. The default value is
     * {@code true}.
     *
     * @return
     * {@code true} if horizontal grid lines will be shown; {@code false},
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
    }

    /**
     * Indicates that vertical grid lines will be shown. The default value is
     * {@code true}.
     *
     * @return
     * {@code true} if vertical grid lines will be shown; {@code false},
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
     * Returns the chart's domain markers.
     *
     * @return
     * The chart's domain markers.
     */
    public List<Marker<K>> getDomainMarkers() {
        return domainMarkers;
    }

    /**
     * Sets the chart's domain markers.
     *
     * @param domainMarkers
     * The chart's domain markers.
     */
    public void setDomainMarkers(List<Marker<K>> domainMarkers) {
        if (domainMarkers == null) {
            throw new IllegalArgumentException();
        }

        this.domainMarkers = domainMarkers;
    }

    /**
     * Returns the chart's range markers.
     *
     * @return
     * The chart's range markers.
     */
    public List<Marker<K>> getRangeMarkers() {
        return rangeMarkers;
    }

    /**
     * Sets the chart's range markers.
     *
     * @param rangeMarkers
     * The chart's range markers.
     */
    public void setRangeMarkers(List<Marker<K>> rangeMarkers) {
        if (rangeMarkers == null) {
            throw new IllegalArgumentException();
        }

        this.rangeMarkers = rangeMarkers;
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

        graphics.setRenderingHints(renderingHints);

        var valid = (width == this.width && height == this.height);

        this.width = width;
        this.height = height;

        if (!valid) {
            validate(graphics);
        }

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
    protected abstract void validate(Graphics2D graphics);

    /**
     * Draws the chart.
     *
     * @param graphics
     * The graphics context in which the chart will be drawn.
     */
    protected abstract void draw(Graphics2D graphics);

    /**
     * Returns the rendering hints.
     *
     * @return
     * The rendering hints.
     */
    protected static RenderingHints getRenderingHints() {
        return renderingHints;
    }

    /**
     * Applies an alpha component to a color.
     *
     * @param color
     * The color.
     *
     * @param alpha
     * The alpha component.
     *
     * @return
     * The color with the alpha component applied.
     */
    protected static Color colorWithAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    /**
     * Paints a component.
     *
     * @param graphics
     * The graphics context in which the component will be painted.
     *
     * @param component
     * The component to paint.
     */
    protected static void paintComponent(Graphics2D graphics, JComponent component) {
        if (graphics == null || component == null) {
            throw new IllegalArgumentException();
        }

        graphics = (Graphics2D)graphics.create();

        graphics.translate(component.getX(), component.getY());
        graphics.clipRect(0, 0, component.getWidth(), component.getHeight());

        component.paint(graphics);

        graphics.dispose();
    }
}
