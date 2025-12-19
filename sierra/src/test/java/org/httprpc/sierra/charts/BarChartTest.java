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

import static org.httprpc.kilo.util.Collections.*;

public class BarChartTest extends ChartTest {
    @Test
    public void testPositiveValues() throws Exception {
        var chart = new BarChart<Integer, Integer>();

        var dataSet = new DataSet<Integer, Integer>("Positive Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(1, 5),
            entry(2, 10),
            entry(3, 15),
            entry(4, 20),
            entry(5, 25)
        ));

        chart.setDataSets(listOf(dataSet));
        chart.setRangeMarkers(listOf(new Chart.Marker<>(null, 20.0, null, null)));

        compare("bar-chart-positive-values.svg", chart);
    }

    @Test
    public void testNegativeValues() throws Exception {
        var chart = new BarChart<Integer, Integer>();

        var dataSet = new DataSet<Integer, Integer>("Negative Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(1, -5),
            entry(2, -10),
            entry(3, -15),
            entry(4, -20),
            entry(5, -25)
        ));

        chart.setDataSets(listOf(dataSet));
        chart.setRangeMarkers(listOf(new Chart.Marker<>(null, -20.0, null, null)));

        compare("bar-chart-negative-values.svg", chart);
    }

    @Test
    public void testMixedValues() throws Exception {
        var chart = new BarChart<Integer, Integer>();

        var dataSet = new DataSet<Integer, Integer>("Mixed Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(1, 5),
            entry(2, 10),
            entry(3, -15),
            entry(4, -20),
            entry(5, -25)
        ));

        chart.setDataSets(listOf(dataSet));
        chart.setRangeMarkers(listOf(new Chart.Marker<>(null, -20.0, null, null)));

        compare("bar-chart-mixed-values.svg", chart);
    }

    @Test
    public void testMissingValue() throws Exception {
        var chart = new BarChart<Integer, Integer>();

        var dataSet1 = new DataSet<Integer, Integer>("Data Set 1", Color.RED);

        dataSet1.setDataPoints(sortedMapOf(
            entry(1, 10),
            entry(2, 20),
            entry(3, 30)
        ));

        var dataSet2 = new DataSet<Integer, Integer>("Data Set 2", Color.RED);

        dataSet2.setDataPoints(sortedMapOf(
            entry(1, 5),
            entry(2, 10)
        ));

        chart.setDataSets(listOf(dataSet1, dataSet2));

        compare("bar-chart-missing-value.svg", chart);
    }

    @Test
    public void testZeroValues() throws Exception {
        var chart = new BarChart<Integer, Integer>();

        var dataSet = new DataSet<Integer, Integer>("Zero Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(1, 0),
            entry(2, 0),
            entry(3, null)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("bar-chart-zero-values.svg", chart);
    }

    @Test
    public void testNoValues() throws Exception {
        var chart = new BarChart<Integer, Integer>();

        var dataSet = new DataSet<Integer, Integer>("No Values", Color.RED);

        chart.setDataSets(listOf(dataSet));

        compare("bar-chart-no-values.svg", chart);
    }

    @Test
    public void testLocalDates() throws Exception {
        var localDate = LocalDate.of(2025, 12, 19);

        var chart = new BarChart<LocalDate, Integer>();

        var dataSet1 = new DataSet<LocalDate, Integer>("Local Dates 1", Color.RED);

        dataSet1.setDataPoints(sortedMapOf(
            entry(localDate, 10)
        ));

        var dataSet2 = new DataSet<LocalDate, Integer>("Local Dates 2", Color.GREEN);

        dataSet2.setDataPoints(sortedMapOf(
            entry(localDate, 20)
        ));

        chart.setDataSets(listOf(dataSet1, dataSet2));

        compare("bar-chart-local-dates.svg", chart);
    }
}
