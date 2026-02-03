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

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Insets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static org.httprpc.kilo.util.Collections.*;

public class TimeSeriesChartTest extends ChartTest {
    @Test
    public void testPositiveValues() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Positive Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, 10.0),
            entry(2.0, 20.0),
            entry(3.0, 30.0),
            entry(4.0, 40.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("time-series-chart-positive-values.svg", chart);
    }

    @Test
    public void testNegativeValues() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Negative Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, -10.0),
            entry(2.0, -20.0),
            entry(3.0, -30.0),
            entry(4.0, -40.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("time-series-chart-negative-values.svg", chart);
    }

    @Test
    public void testMixedValues() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet1 = new DataSet<Double, Double>("Mixed Values 1", Color.RED);

        dataSet1.setDataPoints(sortedMapOf(
            entry(-4.0, -40.0),
            entry(-3.0, -30.0),
            entry(-2.0, -20.0),
            entry(-1.0, -10.0),
            entry(0.0, 0.0),
            entry(1.0, 10.0),
            entry(2.0, 20.0),
            entry(3.0, 30.0),
            entry(4.0, 40.0)
        ));

        var dataSet2 = new DataSet<Double, Double>("Mixed Values 2", Color.GREEN);

        dataSet2.setDataPoints(sortedMapOf(
            entry(-2.0, 20.0),
            entry(-1.0, 10.0),
            entry(0.0, 0.0),
            entry(1.0, -10.0),
            entry(2.0, -20.0)
        ));

        chart.setDataSets(listOf(dataSet1, dataSet2));

        compare("time-series-chart-mixed-values.svg", chart);
    }

    @Test
    public void testZeroValues() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Zero Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, 0.0),
            entry(2.0, 0.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("time-series-chart-zero-values.svg", chart);
    }

    @Test
    public void testNoValues() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("No Values", Color.RED);

        chart.setDataSets(listOf(dataSet));

        compare("time-series-chart-no-values.svg", chart);
    }

    @Test
    public void testOneValue() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("One Value", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("time-series-chart-one-value.svg", chart);
    }

    @Test
    public void testDomainMarkers() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, 10.0),
            entry(2.0, 20.0),
            entry(3.0, 30.0),
            entry(4.0, 40.0),
            entry(5.0, 50.0),
            entry(6.0, 60.0)
        ));

        chart.setDataSets(listOf(dataSet));

        var icon = new FlatSVGIcon(getClass().getResource("icons/flag_24dp.svg"));

        icon = icon.derive(18, 18);

        chart.setDomainMarkers(sortedMapOf(
            entry(1.5, new Chart.Marker(null, icon)),
            entry(4.5, new Chart.Marker(null, icon))
        ));

        compare("time-series-chart-domain-markers.svg", chart);
    }

    @Test
    public void testRangeMarkers() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, 10.0),
            entry(2.0, 20.0),
            entry(3.0, 30.0),
            entry(4.0, 40.0),
            entry(5.0, 50.0),
            entry(6.0, 60.0)
        ));

        chart.setDataSets(listOf(dataSet));

        var icon = new FlatSVGIcon(getClass().getResource("icons/flag_24dp.svg"));

        icon = icon.derive(18, 18);

        chart.setRangeMarkers(sortedMapOf(
            entry(0.1, new Chart.Marker("Bottom", icon)),
            entry(30.0, new Chart.Marker("Center", icon)),
            entry(59.9, new Chart.Marker("Top", icon))
        ));

        compare("time-series-chart-range-markers.svg", chart);
    }

    @Test
    public void testValueMarkers() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        chart.setShowValueMarkers(true);

        var dataSet = new DataSet<Double, Double>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, 10.0),
            entry(1.5, 15.0),
            entry(2.0, 20.0),
            entry(3.0, 30.0),
            entry(4.0, 40.0),
            entry(4.5, 45.0),
            entry(5.0, 50.0),
            entry(6.0, 60.0)
        ));

        chart.setDomainLabelCount(9);

        var domainLabelFormat = NumberFormat.getNumberInstance();

        domainLabelFormat.setMaximumFractionDigits(2);

        chart.setDomainLabelTransform(domainLabelFormat::format);

        chart.setDataSets(listOf(dataSet));

        compare("time-series-chart-value-markers.svg", chart);
    }

    @Test
    public void testLocalDates() throws Exception {
        var dataPoints = sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), 10.0),
            entry(LocalDate.of(2025, 12, 20), 25.0),
            entry(LocalDate.of(2025, 12, 21), null),
            entry(LocalDate.of(2025, 12, 22), 35.0),
            entry(LocalDate.of(2025, 12, 23), null),
            entry(LocalDate.of(2025, 12, 24), 45.0)
        );

        var first = dataPoints.firstKey();
        var last = dataPoints.lastKey();

        Function<LocalDate, Number> domainValueTransform = localDate -> ChronoUnit.DAYS.between(first, localDate);
        Function<Number, LocalDate> domainKeyTransform = value -> first.plusDays(value.intValue());

        var chart = new TimeSeriesChart<LocalDate, Double>(domainValueTransform, domainKeyTransform);

        chart.setShowValueMarkers(true);

        var dataSet = new DataSet<LocalDate, Double>("Values", Color.RED);

        dataSet.setDataPoints(dataPoints);

        chart.setDomainLabelCount((int)ChronoUnit.DAYS.between(first, last) + 1);

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet));

        compare("time-series-chart-local-dates.svg", chart);
    }

    @Test
    public void testAdjustedBounds() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, -0.15),
            entry(1.0, 0.0),
            entry(2.0, 0.15)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("time-series-chart-adjusted-bounds.svg", chart);
    }

    @Test
    public void testCustomBounds() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, 10.0),
            entry(2.0, 20.0),
            entry(3.0, 30.0),
            entry(4.0, 40.0)
        ));

        chart.setDataSets(listOf(dataSet));

        chart.setDomainBounds(new Chart.Bounds<>(-1.0, 5.0));
        chart.setRangeBounds(new Chart.Bounds<>(-10.0, 50.0));

        compare("time-series-chart-custom-bounds.svg", chart);
    }

    @Test
    public void testCustomMargins() throws Exception {
        var chart = new TimeSeriesChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, 10.0),
            entry(2.0, 20.0),
            entry(3.0, 30.0),
            entry(4.0, 40.0)
        ));

        chart.setDataSets(listOf(dataSet));

        chart.validate();

        var margins = chart.getMargins();

        chart.setMargins(new Insets(20, margins.left * 4, margins.bottom * 4, 20));

        compare("time-series-chart-custom-margins.svg", chart);
    }
}
