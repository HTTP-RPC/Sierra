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

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static org.httprpc.kilo.util.Collections.*;

public class CandlestickChartTest extends ChartTest {
    @Test
    public void testPositive() throws Exception {
        var chart = new CandlestickChart<LocalDate>();

        var dataSet = new DataSet<LocalDate, OHLC>("Values", Color.RED);

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

        var dataSet = new DataSet<LocalDate, OHLC>("Values", Color.RED);

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
            entry(LocalDate.of(2025, 12, 18), new OHLC(50, 70, 40, 60))
        ));

        var dataSet2 = new DataSet<LocalDate, OHLC>("Data Set 2", Color.GREEN);

        dataSet2.setDataPoints(sortedMapOf(
            entry(LocalDate.of(2025, 12, 17), new OHLC(60, 70, 40, 50)),
            entry(LocalDate.of(2025, 12, 18), new OHLC(10, 30, 0, 20))
        ));

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        chart.setDomainLabelTransform(dateFormatter::format);

        chart.setDataSets(listOf(dataSet1, dataSet2));

        chart.setRangeMarkers(listOf(
            new Chart.Marker<>(null, 70.0, null, null),
            new Chart.Marker<>(null, 0.0, null, null)
        ));

        compare("candlestick-chart-multiple-data-sets.svg", chart);
    }
}
