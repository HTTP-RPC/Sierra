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

public class TimeSeriesChartTest extends ChartTest {
    @Test
    public void testPositiveValues() throws Exception {
        var chart = new TimeSeriesChart<Integer, Double>(key -> key, Number::intValue);

        var dataSet = new DataSet<Integer, Double>("Positive Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0, 0.0),
            entry(1, 10.0),
            entry(2, 20.0),
            entry(3, 30.0),
            entry(4, 40.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("time-series-chart-positive-values.svg", chart);
    }

    @Test
    public void testNegativeValues() throws Exception {
        var chart = new TimeSeriesChart<Integer, Double>(key -> key, Number::intValue);

        var dataSet = new DataSet<Integer, Double>("Positive Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0, 0.0),
            entry(1, -10.0),
            entry(2, -20.0),
            entry(3, -30.0),
            entry(4, -40.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("time-series-negative-values.svg", chart);
    }

    @Test
    public void testMixedValues() throws Exception {
        var chart = new TimeSeriesChart<Integer, Double>(key -> key, Number::intValue);

        var dataSet = new DataSet<Integer, Double>("Positive Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(-4, -40.0),
            entry(-3, -30.0),
            entry(-2, -20.0),
            entry(-1, -10.0),
            entry(0, 0.0),
            entry(1, 10.0),
            entry(2, 20.0),
            entry(3, 30.0),
            entry(4, 40.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("time-series-mixed-values.svg", chart);
    }
}
