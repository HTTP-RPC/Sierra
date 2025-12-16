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

import static org.httprpc.kilo.util.Collections.*;

public class BarChartTest {
    @Test
    public void testPositiveValues() throws Exception {
        var barChart = new BarChart<Integer, Integer>();

        var dataSet = new DataSet<Integer, Integer>("Positive Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(1, 5),
            entry(2, 10),
            entry(3, 15),
            entry(4, 20),
            entry(5, 25)
        ));

        barChart.setDataSets(listOf(dataSet));

        ChartTest.compare("bar-chart-positive-values.svg", barChart);
    }

    @Test
    public void testNegativeValues() throws Exception {
        var barChart = new BarChart<Integer, Integer>();

        var dataSet = new DataSet<Integer, Integer>("Negative Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(1, -5),
            entry(2, -10),
            entry(3, -15),
            entry(4, -20),
            entry(5, -25)
        ));

        barChart.setDataSets(listOf(dataSet));

        ChartTest.compare("bar-chart-negative-values.svg", barChart);
    }

    @Test
    public void testMixedValues() throws Exception {
        var barChart = new BarChart<Integer, Integer>();

        var dataSet = new DataSet<Integer, Integer>("Mixed Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(1, -5),
            entry(2, -10),
            entry(3, 15),
            entry(4, 20),
            entry(5, 25)
        ));

        barChart.setDataSets(listOf(dataSet));

        barChart.setRangeMarkers(listOf(
            new Chart.Marker<>(null, 0.0, null, null),
            new Chart.Marker<>(null, 25.0, null, null),
            new Chart.Marker<>(null, -10.0, null, null)
        ));

        ChartTest.compare("bar-chart-mixed-values.svg", barChart);
    }
}
