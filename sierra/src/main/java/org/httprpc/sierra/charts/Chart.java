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
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.List;
import java.util.SortedMap;
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
    public record Marker(
        String text,
        Icon icon
    ) {
        /**
         * Constructs a new marker instance.
         *
         * @param text
         * The marker text.
         *
         * @param icon
         * The marker icon.
         */
        public Marker {
            if (text == null && icon == null) {
                throw new IllegalArgumentException();
            }
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

    private int width = 320;
    private int height = 240;

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

    private SortedMap<K, Marker> domainMarkers = sortedMapOf();
    private SortedMap<Double, Marker> rangeMarkers = sortedMapOf();

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

    private Bounds<K> domainBounds = null;
    private Bounds<Double> rangeBounds = null;

    private Insets margins = null;

    private boolean valid = false;

    private Rectangle2D.Double gridBounds = null;

    private double domainScale = 0.0;
    private double rangeScale = 0.0;

    private Point2D.Double origin = null;

    private double columnWidth = 0.0;
    private double rowHeight = 0.0;

    private List<TextPane> leftAxisTextPanes = listOf();
    private List<TextPane> bottomAxisTextPanes = listOf();

    private List<Line2D.Double> horizontalGridLines = listOf();
    private List<Line2D.Double> verticalGridLines = listOf();

    private Line2D.Double zeroLine = null;

    private List<JLabel> rangeMarkerLabels = listOf();
    private List<Line2D.Double> rangeMarkerLines = listOf();

    static final int SPACING = 4;

    static final RenderingHints renderingHints = new RenderingHints(mapOf(
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
     * Returns the chart width.
     *
     * @return
     * The chart width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the chart width.
     *
     * @param width
     * The chart width.
     */
    public void setWidth(int width) {
        setSize(width, height);
    }

    /**
     * Returns the chart height.
     *
     * @return
     * The chart height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the chart height.
     *
     * @param height
     * The chart height.
     */
    public void setHeight(int height) {
        setSize(width, height);
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

        if (width != this.width || height != this.height) {
            valid = false;
        }

        this.width = width;
        this.height = height;
    }

    /**
     * Sizes the chart to fit its contents.
     */
    public void sizeToFit() {
        validate();

        // TODO
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

        valid = false;
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

        valid = false;
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

        valid = false;
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

        valid = false;
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

        valid = false;
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

        valid = false;
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

        valid = false;
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

        valid = false;
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
     * Returns the chart's domain markers.
     *
     * @return
     * The chart's domain markers.
     */
    public SortedMap<K, Marker> getDomainMarkers() {
        return domainMarkers;
    }

    /**
     * Sets the chart's domain markers.
     *
     * @param domainMarkers
     * The chart's domain markers.
     */
    public void setDomainMarkers(SortedMap<K, Marker> domainMarkers) {
        if (domainMarkers == null) {
            throw new IllegalArgumentException();
        }

        this.domainMarkers = domainMarkers;

        valid = false;
    }

    /**
     * Returns the chart's range markers.
     *
     * @return
     * The chart's range markers.
     */
    public SortedMap<Double, Marker> getRangeMarkers() {
        return rangeMarkers;
    }

    /**
     * Sets the chart's range markers.
     *
     * @param rangeMarkers
     * The chart's range markers.
     */
    public void setRangeMarkers(SortedMap<Double, Marker> rangeMarkers) {
        if (rangeMarkers == null) {
            throw new IllegalArgumentException();
        }

        this.rangeMarkers = rangeMarkers;

        valid = false;
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

        valid = false;
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

        valid = false;
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

        valid = false;
    }

    /**
     * Draws the chart.
     *
     * @param graphics
     * The graphics context in which the chart will be drawn.
     */
    public void draw(Graphics2D graphics) {
        graphics.setRenderingHints(renderingHints);

        if (!valid) {
            validate();

            valid = true;
        }

        drawChart(graphics);
    }

    /**
     * Validates the chart contents.
     */
    public abstract void validate();

    SortedSet<K> getKeys() {
        return null;
    }

    Function<K, Number> getDomainValueTransform() {
        return null;
    }

    Function<Number, K> getDomainKeyTransform() {
        return null;
    }

    boolean isTransposed() {
        return false;
    }

    void validateGrid() {
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

        populateDomainTextPanes();
        populateRangeTextPanes();

        if (margins == null) {
            margins = getPreferredMargins();
        }

        var horizontalGridLineWidth = (double)horizontalGridLineStroke.getLineWidth();
        var verticalGridLineWidth = (double)verticalGridLineStroke.getLineWidth();

        var gridX = margins.left + verticalGridLineWidth / 2;
        var gridY = margins.top + horizontalGridLineWidth / 2;

        var gridWidth = Math.max(width - (margins.left + margins.right + verticalGridLineWidth), 0.0);
        var gridHeight = Math.max(height - (margins.top + margins.bottom + horizontalGridLineWidth), 0.0);

        gridBounds = new Rectangle2D.Double(gridX, gridY, gridWidth, gridHeight);

        var keys = getKeys();

        int m;
        if (keys == null) {
            m = domainLabelCount - 1;
        } else {
            m = Math.max(keys.size(), 1);
        }

        var n = rangeLabelCount - 1;

        double zeroX;
        double zeroY;
        int columnCount;
        int rowCount;
        if (isTransposed()) {
            domainScale = gridHeight / (domainMaximum - domainMinimum);
            rangeScale = gridWidth / (rangeMaximum - rangeMinimum);

            zeroX = gridX - rangeMinimum * rangeScale;
            zeroY = gridY + domainMaximum * domainScale;

            columnCount = n;
            rowCount = m;
        } else {
            domainScale = gridWidth / (domainMaximum - domainMinimum);
            rangeScale = gridHeight / (rangeMaximum - rangeMinimum);

            zeroX = gridX - domainMinimum * domainScale;
            zeroY = gridY + rangeMaximum * rangeScale;

            columnCount = m;
            rowCount = n;
        }

        origin = new Point2D.Double(zeroX, zeroY);

        columnWidth = gridWidth / columnCount;
        rowHeight = gridHeight / rowCount;

        var gridLineY = gridY;

        for (var i = -1; i < rowCount; i++) {
            horizontalGridLines.add(new Line2D.Double(gridX, gridLineY, gridX + gridWidth, gridLineY));

            gridLineY += rowHeight;
        }

        var gridLineX = gridX;

        for (var i = -1; i < columnCount; i++) {
            verticalGridLines.add(new Line2D.Double(gridLineX, gridY, gridLineX, gridY + gridHeight));

            gridLineX += columnWidth;
        }

        if (isTransposed()) {
            zeroLine = new Line2D.Double(zeroX, gridY, zeroX, gridY + gridHeight);
        } else {
            zeroLine = new Line2D.Double(gridX, zeroY, gridX + gridWidth, zeroY);
        }

        validateVerticalAxisLabels();
        validateHorizontalAxisLabels();
    }

    private Insets getPreferredMargins() {
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

        return new Insets(0, left + SPACING, bottom + SPACING, 0);
    }

    Rectangle2D.Double getGridBounds() {
        return gridBounds;
    }

    double getDomainScale() {
        return domainScale;
    }

    double getRangeScale() {
        return rangeScale;
    }

    Point2D.Double getOrigin() {
        return origin;
    }

    double getColumnWidth() {
        return columnWidth;
    }

    double getRowHeight() {
        return rowHeight;
    }

    private void populateDomainTextPanes() {
        var domainTextPanes = getDomainTextPanes();

        var keys = getKeys();

        if (keys == null) {
            var domainValueTransform = getDomainValueTransform();
            var domainKeyTransform = getDomainKeyTransform();

            var domainMinimum = domainValueTransform.apply(domainBounds.minimum()).doubleValue();
            var domainMaximum = domainValueTransform.apply(domainBounds.maximum()).doubleValue();

            var domainStep = (domainMaximum - domainMinimum) / (domainLabelCount - 1);

            for (var i = 0; i < domainLabelCount; i++) {
                var key = domainKeyTransform.apply(domainMinimum + domainStep * i);

                var textPane = new TextPane(domainLabelTransform.apply(key));

                textPane.setFont(domainLabelFont);

                domainTextPanes.add(textPane);
            }
        } else {
            for (var key : isTransposed() ? keys.reversed() : keys) {
                var textPane = new TextPane(domainLabelTransform.apply(key));

                textPane.setFont(domainLabelFont);

                domainTextPanes.add(textPane);
            }
        }
    }

    private List<TextPane> getDomainTextPanes() {
        if (isTransposed()) {
            return leftAxisTextPanes;
        } else {
            return bottomAxisTextPanes;
        }
    }

    private void populateRangeTextPanes() {
        var rangeTextPanes = getRangeTextPanes();

        var rangeMinimum = rangeBounds.minimum();
        var rangeMaximum = rangeBounds.maximum();

        var rangeStep = Math.abs(rangeMaximum - rangeMinimum) / (rangeLabelCount - 1);

        for (var i = 0; i < rangeLabelCount; i++) {
            var label = rangeLabelTransform.apply(rangeMinimum + rangeStep * i);

            var textPane = new TextPane(label);

            textPane.setFont(rangeLabelFont);

            rangeTextPanes.add(textPane);
        }
    }

    private List<TextPane> getRangeTextPanes() {
        if (isTransposed()) {
            return bottomAxisTextPanes;
        } else {
            return leftAxisTextPanes;
        }
    }

    private void validateVerticalAxisLabels() {
        var n = leftAxisTextPanes.size();

        var baseY = gridBounds.getY() + gridBounds.getHeight();

        for (var i = 0; i < n; i++) {
            var textPane = leftAxisTextPanes.get(i);

            var size = textPane.getPreferredSize();

            int y;
            if (isTransposed()) {
                y = (int)(baseY - rowHeight + (rowHeight - size.height) / 2);
            } else {
                if (i == 0) {
                    y = (int)baseY - size.height;
                } else if (i < n - 1) {
                    y = (int)baseY - size.height / 2;
                } else {
                    y = (int)baseY;
                }
            }

            textPane.setHorizontalAlignment(HorizontalAlignment.TRAILING);

            textPane.setBounds(0, y, margins.left - SPACING, size.height);
            textPane.doLayout();

            baseY -= rowHeight;
        }
    }

    private void validateHorizontalAxisLabels() {
        var n = bottomAxisTextPanes.size();

        var y = gridBounds.getY() + gridBounds.getHeight() + SPACING + horizontalGridLineStroke.getLineWidth();

        var baseX = gridBounds.getX();

        var keys = getKeys();

        if (keys == null || isTransposed()) {
            for (var i = 0; i < n; i++) {
                var textPane = bottomAxisTextPanes.get(i);

                textPane.setSize(textPane.getPreferredSize());

                var size = textPane.getSize();

                int x;
                if (i == 0) {
                    x = (int)baseX;
                } else if (i < n - 1) {
                    x = (int)baseX - size.width / 2;
                } else {
                    x = (int)baseX - size.width;
                }

                textPane.setLocation(x, (int)y);
                textPane.doLayout();

                baseX += columnWidth;
            }
        } else {
            var maximumWidth = 0.0;

            for (var i = 0; i < n; i++) {
                var textPane = bottomAxisTextPanes.get(i);

                textPane.setSize(textPane.getPreferredSize());

                var size = textPane.getSize();

                maximumWidth = Math.max(maximumWidth, size.width);
            }

            var showLabels = maximumWidth < columnWidth * 0.85;

            for (var i = 0; i < n; i++) {
                var textPane = bottomAxisTextPanes.get(i);

                var size = textPane.getSize();

                int x;
                if (showLabels) {
                    x = (int)(baseX + columnWidth / 2) - size.width / 2;
                } else if (i == 0) {
                    x = (int)baseX;
                } else if (i < n - 1) {
                    x = (int)(baseX + columnWidth / 2) - size.width / 2;

                    textPane.setText(null);
                } else {
                    x = (int)(baseX + columnWidth) - size.width;
                }

                textPane.setLocation(x, (int)y);
                textPane.doLayout();

                baseX += columnWidth;
            }
        }
    }

    void validateMarkers() {
        rangeMarkerLabels.clear();
        rangeMarkerLines.clear();

        var gridX = gridBounds.getX();
        var gridY = gridBounds.getY();

        var gridWidth = gridBounds.getWidth();
        var gridHeight = gridBounds.getHeight();

        var zeroX = origin.getX();
        var zeroY = origin.getY();

        for (var entry : rangeMarkers.entrySet()) {
            var rangeValue = entry.getKey();

            var marker = entry.getValue();

            var label = new JLabel(marker.text(), marker.icon(), SwingConstants.CENTER);

            label.setIconTextGap(2);

            label.setFont(markerFont);

            var size = label.getPreferredSize();

            Line2D.Double line;
            if (isTransposed()) {
                var lineX = zeroX + rangeValue * rangeScale;

                int labelX;
                if (lineX < gridX + gridWidth / 2) {
                    labelX = (int)lineX + SPACING;
                } else {
                    labelX = (int)lineX - (size.width + SPACING);
                }

                label.setBounds(labelX, (int)gridY + SPACING, size.width, size.height);

                line = new Line2D.Double(lineX, gridY + SPACING, lineX, gridY + gridHeight - SPACING);
            } else {
                var lineY = zeroY - rangeValue * rangeScale;

                int labelY;
                if (lineY < gridY + gridHeight / 2) {
                    labelY = (int)lineY + SPACING / 2;
                } else {
                    labelY = (int)lineY - (size.height + SPACING / 2);
                }

                label.setBounds((int)gridX + SPACING, labelY, size.width, size.height);

                line = new Line2D.Double(gridX + SPACING, lineY, gridX + gridWidth - SPACING, lineY);
            }

            rangeMarkerLabels.add(label);

            rangeMarkerLines.add(line);
        }
    }

    abstract void drawChart(Graphics2D graphics);

    void drawGrid(Graphics2D graphics) {
        if (showHorizontalGridLines) {
            graphics.setColor(horizontalGridLineColor);
            graphics.setStroke(horizontalGridLineStroke);

            for (var horizontalGridLine : horizontalGridLines) {
                graphics.draw(horizontalGridLine);
            }
        }

        if (showVerticalGridLines) {
            graphics.setColor(verticalGridLineColor);
            graphics.setStroke(verticalGridLineStroke);

            for (var verticalGridLine : verticalGridLines) {
                graphics.draw(verticalGridLine);
            }
        }

        graphics.setColor(domainLabelColor);

        for (var textPane : getDomainTextPanes()) {
            paintComponent(graphics, textPane);
        }

        graphics.setColor(rangeLabelColor);

        for (var textPane : getRangeTextPanes()) {
            paintComponent(graphics, textPane);
        }

        var x = (int)Math.ceil(verticalGridLines.getFirst().getX1());
        var y = (int)Math.ceil(horizontalGridLines.getFirst().getY1());

        var width = (int)Math.floor(verticalGridLines.getLast().getX1()) - x;
        var height = (int)Math.floor(horizontalGridLines.getLast().getY1()) - y;

        graphics.clipRect(x, y, width, height);
    }

    void drawZeroLine(Graphics2D graphics, Color color, BasicStroke stroke) {
        if (!gridBounds.intersectsLine(zeroLine)) {
            return;
        }

        graphics.setColor(color);
        graphics.setStroke(stroke);

        graphics.draw(zeroLine);
    }

    void drawMarkers(Graphics2D graphics) {
        for (var label : rangeMarkerLabels) {
            label.setForeground(markerColor);

            paintComponent(graphics, label);
        }

        graphics.setColor(markerColor);
        graphics.setStroke(markerStroke);

        for (var line : rangeMarkerLines) {
            graphics.draw(line);
        }
    }

    static Bounds<Double> adjustBounds(double minimum, double maximum) {
        var r = maximum - minimum;

        if (r > 0.0) {
            var m = (int)Math.log10(r);

            var n = Math.pow(10, m - 1);

            minimum = Math.floor(minimum / n) * n;
            maximum = Math.ceil(maximum / n) * n;
        }

        return new Bounds<>(minimum, maximum);
    }

    static Color colorWithAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    static void paintComponent(Graphics2D graphics, JComponent component) {
        graphics = (Graphics2D)graphics.create();

        graphics.translate(component.getX(), component.getY());

        component.paint(graphics);

        graphics.dispose();
    }
}
