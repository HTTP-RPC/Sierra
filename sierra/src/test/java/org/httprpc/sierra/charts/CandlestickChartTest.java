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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static org.httprpc.kilo.util.Collections.*;

public class CandlestickChartTest extends ChartTest {
    @Test
    public void testPositive() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        var dataSet = new DataSet<LocalDate, OHLC>("Positive", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(10, 30, 0, 20))
        ));

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet));

        compare("candlestick-chart-positive.svg", chart);
    }

    @Test
    public void testNegative() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        var dataSet = new DataSet<LocalDate, OHLC>("Negative", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(20, 30, 0, 10))
        ));

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet));

        compare("candlestick-chart-negative.svg", chart);
    }

    @Test
    public void testMultipleDataPoints() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        var dataSet = new DataSet<LocalDate, OHLC>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(10, 30, 0, 20)),
            entry(LocalDate.of(2025, 12, 18), new OHLC(20, 30, 0, 10))
        ));

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet));

        compare("candlestick-chart-multiple-data-points.svg", chart);
    }

    @Test
    public void testMultipleDataSets() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        var dataSet1 = new DataSet<LocalDate, OHLC>("Data Set 1", Color.RED);

        dataSet1.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(20, 30, 0, 10)),
            entry(LocalDate.of(2025, 12, 18), new OHLC(10, 30, 0, 20))
        ));

        var dataSet2 = new DataSet<LocalDate, OHLC>("Data Set 2", Color.GREEN);

        dataSet2.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(50, 70, 40, 60)),
            entry(LocalDate.of(2025, 12, 18), new OHLC(60, 70, 40, 50))
        ));

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet1, dataSet2));

        compare("candlestick-chart-multiple-data-sets.svg", chart);
    }

    @Test
    public void testMissingValue() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        var dataSet1 = new DataSet<LocalDate, OHLC>("Data Set 1", Color.RED);

        dataSet1.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(20, 30, 0, 10)),
            entry(LocalDate.of(2025, 12, 18), new OHLC(10, 30, 0, 20))
        ));

        var dataSet2 = new DataSet<LocalDate, OHLC>("Data Set 2", Color.GREEN);

        dataSet2.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 18), new OHLC(60, 70, 40, 50))
        ));

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet1, dataSet2));

        compare("candlestick-chart-missing-value.svg", chart);
    }

    @Test
    public void testNoValues() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        var dataSet = new DataSet<LocalDate, OHLC>("No Values", Color.RED);

        chart.setDataSets(listOf(dataSet));

        compare("candlestick-chart-no-values.svg", chart);
    }

    @Test
    public void testTransparency() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        chart.setBodyTransparency(0.5);

        var dataSet = new DataSet<LocalDate, OHLC>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(20, 30, 0, 10))
        ));

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet));

        compare("candlestick-chart-transparency.svg", chart);
    }

    @Test
    public void testDomainMarkers() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        var dataSet = new DataSet<LocalDate, OHLC>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(10, 30, 0, 20)),
            entry(LocalDate.of(2025, 12, 18), new OHLC(20, 30, 0, 10))
        ));

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet));

        var icon = new FlatSVGIcon(getClass().getResource("icons/flag_24dp.svg"));

        icon = icon.derive(18, 18);

        chart.setDomainMarkers(sortedMapOf(
            entry(dataSet.getDataPoints().firstKey(), new Chart.Marker("First", icon)),
            entry(dataSet.getDataPoints().lastKey(), new Chart.Marker("Last", icon))
        ));

        compare("candlestick-chart-domain-markers.svg", chart);
    }

    @Test
    public void testRangeMarkers() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        var dataSet = new DataSet<LocalDate, OHLC>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(10, 30, 0, 20)),
            entry(LocalDate.of(2025, 12, 18), new OHLC(20, 30, 0, 10))
        ));

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet));

        var icon = new FlatSVGIcon(getClass().getResource("icons/flag_24dp.svg"));

        icon = icon.derive(18, 18);

        chart.setRangeMarkers(sortedMapOf(
            entry(0.1, new Chart.Marker("Bottom", icon)),
            entry(10.0, new Chart.Marker("10", null)),
            entry(20.0, new Chart.Marker("20", null)),
            entry(29.9, new Chart.Marker("Top", icon))
        ));

        compare("candlestick-chart-range-markers.svg", chart);
    }

    @Test
    public void testCustomMargins() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        var dataSet = new DataSet<LocalDate, OHLC>("Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(10, 30, 0, 20))
        ));

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet));

        chart.validate();

        var margins = chart.getMargins();

        chart.setMargins(new Insets(20, margins.left * 4, margins.bottom * 4, 20));

        compare("candlestick-chart-custom-margins.svg", chart);
    }}
