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

import org.httprpc.sierra.HorizontalAlignment;
import org.httprpc.sierra.TextPane;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.text.NumberFormat;
import java.util.List;
import java.util.SortedSet;
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
     */
    public record Marker<K>(
        K key,
        Number value,
        String label,
        Icon icon
    ) {
        /**
         * Constructs a new marker instance.
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
        public Marker {
        }
    }

    /**
     * Represents chart bounds.
     *
     * @param <T>
     * The value type.
     */
    public record Bounds<T extends Comparable<? super T>>(
        T minimum,
        T maximum
    ) {
        /**
         * Constructs a new bounds instance.
         *
         * @param minimum
         * The minimum value.
         *
         * @param maximum
         * The maximum value.
         */
        public Bounds {
            if (minimum == null || maximum == null) {
                throw new IllegalArgumentException();
            }

            if (minimum.compareTo(maximum) > 0) {
                throw new IllegalArgumentException();
            }
        }
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

    protected Bounds<K> domainBounds = null;
    protected Bounds<Double> rangeBounds = null;

    protected Insets margins = null;

    protected int width = 0;
    protected int height = 0;

    protected double horizontalGridLineWidth = 0.0;
    protected double verticalGridLineWidth = 0.0;

    protected double chartX = 0.0;
    protected double chartY = 0.0;

    protected double chartWidth = 0.0;
    protected double chartHeight = 0.0;

    protected double domainScale = 0.0;
    protected double rangeScale = 0.0;

    protected double columnWidth = 0.0;
    protected double rowHeight = 0.0;

    protected double zeroX = 0.0;
    protected double zeroY = 0.0;

    protected List<TextPane> leftAxisTextPanes = listOf();
    protected List<TextPane> bottomAxisTextPanes = listOf();

    protected List<Line2D.Double> horizontalGridLines = listOf();
    protected List<Line2D.Double> verticalGridLines = listOf();

    protected Line2D.Double zeroLine = null;

    protected static final int SPACING = 4;

    protected static final RenderingHints renderingHints = new RenderingHints(mapOf(
        entry(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON),
        entry(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY),
        entry(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE),
        entry(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT),
        entry(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT)
    ));

    Chart() {
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
     * Returns the domain value transform.
     *
     * @return
     * The domain value transform.
     */
    protected Function<K, Number> getDomainValueTransform() {
        return null;
    }

    /**
     * Returns the domain key transform.
     *
     * @return
     * The domain key transform.
     */
    protected Function<Number, K> getDomainKeyTransform() {
        return null;
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
     * Returns the domain bounds.
     *
     * @return
     * The domain bounds.
     */
    public Bounds<K> getDomainBounds() {
        return domainBounds;
    }

    /**
     * Sets the domain bounds.
     *
     * @param domainBounds
     * The domain bounds, or {@code null} for the default bounds.
     */
    public void setDomainBounds(Bounds<K> domainBounds) {
        this.domainBounds = domainBounds;
    }

    /**
     * Returns the range bounds.
     *
     * @return
     * The range bounds.
     */
    public Bounds<Double> getRangeBounds() {
        return rangeBounds;
    }

    /**
     * Sets the range bounds.
     *
     * @param rangeBounds
     * The range bounds, or {@code null} for the default bounds.
     */
    public void setRangeBounds(Bounds<Double> rangeBounds) {
        this.rangeBounds = rangeBounds;
    }

    /**
     * Returns the chart margins.
     *
     * @return
     * The chart margins.
     */
    public Insets getMargins() {
        return margins;
    }

    /**
     * Sets the chart margins.
     *
     * @param margins
     * The chart margins, or {@code null} for the default margins.
     */
    public void setMargins(Insets margins) {
        this.margins = margins;
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
            validate();
        }

        draw(graphics);
    }

    /**
     * Validates the chart contents.
     */
    public abstract void validate();

    /**
     * Validates the grid.
     */
    protected void validateGrid() {
        var keys = getKeys();

        var domainValueTransform = getDomainValueTransform();

        var domainMinimum = 0.0;
        var domainMaximum = 0.0;

        if (domainValueTransform != null) {
            if (domainBounds != null) {
                domainMinimum = domainValueTransform.apply(domainBounds.minimum()).doubleValue();
                domainMaximum = domainValueTransform.apply(domainBounds.maximum()).doubleValue();
            }

            if (domainMinimum == domainMaximum) {
                domainMinimum -= 1.0;
                domainMaximum += 1.0;

                var domainKeyTransform = getDomainKeyTransform();

                domainBounds = new Bounds<>(domainKeyTransform.apply(domainMinimum), domainKeyTransform.apply(domainMaximum));
            }
        }

        var rangeMinimum = 0.0;
        var rangeMaximum = 0.0;

        if (rangeBounds != null) {
            rangeMinimum = rangeBounds.minimum();
            rangeMaximum = rangeBounds.maximum();
        }

        if (rangeMinimum == rangeMaximum) {
            rangeMinimum -= 1.0;
            rangeMaximum += 1.0;

            rangeBounds = new Bounds<>(rangeMinimum, rangeMaximum);
        }

        leftAxisTextPanes.clear();
        bottomAxisTextPanes.clear();

        horizontalGridLines.clear();
        verticalGridLines.clear();

        zeroLine = null;

        populateDomainLabels();
        populateRangeLabels();

        if (margins == null) {
            var left = 0;

            for (var textPane : leftAxisTextPanes) {
                var preferredSize = textPane.getPreferredSize();

                left = Math.max(left, preferredSize.width);
            }

            var bottom = 0;

            for (var textPane : bottomAxisTextPanes) {
                var preferredSize = textPane.getPreferredSize();

                bottom = Math.max(bottom, preferredSize.height);
            }

            margins = new Insets(0, left + SPACING, bottom + SPACING, 0);
        }

        horizontalGridLineWidth = getHorizontalGridLineStroke().getLineWidth();
        verticalGridLineWidth = getVerticalGridLineStroke().getLineWidth();

        chartX = margins.left + verticalGridLineWidth / 2;
        chartY = margins.top + horizontalGridLineWidth / 2;

        chartWidth = Math.max(width - (margins.left + margins.right + verticalGridLineWidth), 0.0);
        chartHeight = Math.max(height - (margins.top + margins.bottom + horizontalGridLineWidth), 0.0);

        domainScale = chartWidth / (domainMaximum - domainMinimum);
        rangeScale = chartHeight / (rangeMaximum - rangeMinimum);

        int n;
        if (keys != null) {
            n = keys.isEmpty() ? 1 : keys.size();
        } else {
            n = getDomainLabelCount() - 1;
        }

        columnWidth = chartWidth / n;
        rowHeight = chartHeight / (rangeLabelCount - 1);

        zeroY = rangeMaximum * rangeScale + horizontalGridLineWidth / 2;

        var gridY = horizontalGridLineWidth / 2;

        for (var i = 0; i < rangeLabelCount; i++) {
            horizontalGridLines.add(new Line2D.Double(chartX, gridY, chartX + chartWidth, gridY));

            gridY += rowHeight;
        }

        var verticalGridLineCount = n + 1;

        var gridX = chartX;

        for (var i = 0; i < verticalGridLineCount; i++) {
            verticalGridLines.add(new Line2D.Double(gridX, verticalGridLineWidth / 2, gridX, chartHeight));

            gridX += columnWidth;
        }

        if (rangeMaximum > 0.0 && rangeMinimum < 0.0) {
            zeroLine = new Line2D.Double(chartX, zeroY, chartX + chartWidth, zeroY);
        }

        validateVerticalAxisLabels();
        validateHorizontalAxisLabels();
    }

    /**
     * Returns the category keys.
     *
     * @return
     * The category keys.
     */
    protected SortedSet<K> getKeys() {
        return null;
    }

    /**
     * Indicates that the chart is transposed.
     *
     * @return
     * {@code true} if the chart is transposed; {@code false}, otherwise.
     */
    protected boolean isTransposed() {
        return false;
    }

    private void populateDomainLabels() {
        var domainLabelTransform = getDomainLabelTransform();
        var domainLabelFont = getDomainLabelFont();

        var keys = getKeys();

        if (keys != null) {
            if (keys.isEmpty()) {
                var textPane = new TextPane("");

                textPane.setFont(domainLabelFont);

                bottomAxisTextPanes.add(textPane);
            } else {
                for (var key : keys) {
                    var textPane = new TextPane(domainLabelTransform.apply(key));

                    textPane.setFont(domainLabelFont);

                    bottomAxisTextPanes.add(textPane);
                }
            }
        } else {
            var domainValueTransform = getDomainValueTransform();
            var domainKeyTransform = getDomainKeyTransform();

            var domainMinimum = domainValueTransform.apply(domainBounds.minimum()).doubleValue();
            var domainMaximum = domainValueTransform.apply(domainBounds.maximum()).doubleValue();

            var domainLabelCount = getDomainLabelCount();

            var domainStep = (domainMaximum - domainMinimum) / (domainLabelCount - 1);

            for (var i = 0; i < domainLabelCount; i++) {
                var key = domainKeyTransform.apply(domainMinimum + domainStep * i);

                var textPane = new TextPane(domainLabelTransform.apply(key));

                textPane.setFont(domainLabelFont);

                bottomAxisTextPanes.add(textPane);
            }
        }
    }

    private void populateRangeLabels() {
        var rangeMinimum = rangeBounds.minimum();
        var rangeMaximum = rangeBounds.maximum();

        var rangeStep = Math.abs(rangeMaximum - rangeMinimum) / (rangeLabelCount - 1);

        for (var i = 0; i < rangeLabelCount; i++) {
            var label = rangeLabelTransform.apply(rangeMinimum + rangeStep * i);

            var textPane = new TextPane(label);

            textPane.setFont(rangeLabelFont);
            textPane.setHorizontalAlignment(HorizontalAlignment.TRAILING);

            leftAxisTextPanes.add(textPane);
        }
    }

    private void validateVerticalAxisLabels() {
        var rangeLabelY = chartY + chartHeight;

        for (var i = 0; i < rangeLabelCount; i++) {
            var textPane = leftAxisTextPanes.get(i);

            var size = textPane.getPreferredSize();

            int y;
            if (i == 0) {
                y = (int)rangeLabelY - size.height;
            } else if (i < rangeLabelCount - 1) {
                y = (int)rangeLabelY - size.height / 2;
            } else {
                y = (int)rangeLabelY;
            }

            textPane.setBounds(0, y, margins.left - SPACING, size.height);
            textPane.doLayout();

            rangeLabelY -= rowHeight;
        }
    }

    private void validateHorizontalAxisLabels() {
        var keys = getKeys();

        if (keys != null) {
            var keyCount = keys.size();

            var maximumWidth = 0.0;

            for (var i = 0; i < keyCount; i++) {
                var textPane = bottomAxisTextPanes.get(i);

                textPane.setSize(textPane.getPreferredSize());

                var size = textPane.getSize();

                maximumWidth = Math.max(maximumWidth, size.width);
            }

            var showDomainLabels = maximumWidth < columnWidth * 0.85;

            var domainLabelX = chartX;
            var domainLabelY = chartY + chartHeight + SPACING + horizontalGridLineWidth;

            for (var i = 0; i < keyCount; i++) {
                var textPane = bottomAxisTextPanes.get(i);

                var size = textPane.getSize();

                int x;
                if (showDomainLabels) {
                    x = (int)(domainLabelX + columnWidth / 2) - size.width / 2;
                } else if (i == 0) {
                    x = (int)domainLabelX;
                } else if (i < keyCount - 1) {
                    x = (int)(domainLabelX + columnWidth / 2) - size.width / 2;

                    textPane.setText(null);
                } else {
                    x = (int)(domainLabelX + columnWidth) - size.width;
                }

                textPane.setLocation(x, (int)domainLabelY);
                textPane.doLayout();

                domainLabelX += columnWidth;
            }
        } else {
            var domainLabelCount = getDomainLabelCount();

            var domainLabelX = chartX;
            var domainLabelY = chartY + chartHeight + SPACING + horizontalGridLineWidth;

            for (var i = 0; i < domainLabelCount; i++) {
                var textPane = bottomAxisTextPanes.get(i);

                textPane.setSize(textPane.getPreferredSize());

                var size = textPane.getSize();

                int x;
                if (i == 0) {
                    x = (int)domainLabelX;
                } else if (i < domainLabelCount - 1) {
                    x = (int)domainLabelX - size.width / 2;
                } else {
                    x = (int)domainLabelX - size.width;
                }

                textPane.setLocation(x, (int)domainLabelY);
                textPane.doLayout();

                domainLabelX += columnWidth;
            }
        }
    }

    /**
     * Draws the chart.
     *
     * @param graphics
     * The graphics context in which the chart will be drawn.
     */
    protected abstract void draw(Graphics2D graphics);

    /**
     * Draws the grid.
     *
     * @param graphics
     * The graphics context in which the grid will be drawn.
     */
    protected void drawGrid(Graphics2D graphics) {
        if (getShowHorizontalGridLines()) {
            graphics.setColor(getHorizontalGridLineColor());
            graphics.setStroke(getHorizontalGridLineStroke());

            for (var horizontalGridLine : horizontalGridLines) {
                graphics.draw(horizontalGridLine);
            }
        }

        if (getShowVerticalGridLines()) {
            graphics.setColor(getVerticalGridLineColor());
            graphics.setStroke(getVerticalGridLineStroke());

            for (var verticalGridLine : verticalGridLines) {
                graphics.draw(verticalGridLine);
            }
        }

        graphics.setColor(getDomainLabelColor());

        for (var textPane : bottomAxisTextPanes) {
            paintComponent(graphics, textPane);
        }

        graphics.setColor(getRangeLabelColor());

        for (var textPane : leftAxisTextPanes) {
            paintComponent(graphics, textPane);
        }

        var x = (int)Math.ceil(verticalGridLines.getFirst().getX1());
        var y = (int)Math.ceil(horizontalGridLines.getFirst().getY1());

        var width = (int)Math.floor(verticalGridLines.getLast().getX1()) - x;
        var height = (int)Math.floor(horizontalGridLines.getLast().getY1()) - y;

        graphics.setClip(x, y, width, height);
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
        graphics = (Graphics2D)graphics.create();

        graphics.translate(component.getX(), component.getY());
        graphics.clipRect(0, 0, component.getWidth(), component.getHeight());

        component.paint(graphics);

        graphics.dispose();
    }
}
