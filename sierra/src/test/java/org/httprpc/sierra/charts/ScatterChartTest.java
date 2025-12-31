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

import static org.httprpc.kilo.util.Collections.*;

public class ScatterChartTest extends ChartTest {
    @Test
    public void testPositiveValues() throws Exception {
        var chart = new ScatterChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Positive Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, 10.0),
            entry(2.0, 20.0),
            entry(3.0, 30.0),
            entry(4.0, 40.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("scatter-chart-positive-values.svg", chart);
    }

    @Test
    public void testNegativeValues() throws Exception {
        var chart = new ScatterChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Negative Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, -10.0),
            entry(2.0, -20.0),
            entry(3.0, -30.0),
            entry(4.0, -40.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("scatter-chart-negative-values.svg", chart);
    }

    @Test
    public void testMixedValues() throws Exception {
        var chart = new ScatterChart<Double, Double>(key -> key, Number::doubleValue);

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

        compare("scatter-chart-mixed-values.svg", chart);
    }

    @Test
    public void testZeroValues() throws Exception {
        var chart = new ScatterChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("Zero Values", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, 0.0),
            entry(2.0, 0.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("scatter-chart-zero-values.svg", chart);
    }

    @Test
    public void testNoValues() throws Exception {
        var chart = new ScatterChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("No Values", Color.RED);

        chart.setDataSets(listOf(dataSet));

        compare("scatter-chart-no-values.svg", chart);
    }

    @Test
    public void testOneValue() throws Exception {
        var chart = new ScatterChart<Double, Double>(key -> key, Number::doubleValue);

        var dataSet = new DataSet<Double, Double>("One Value", Color.RED);

        dataSet.setDataPoints(sortedMapOf(
            entry(0.0, 0.0)
        ));

        chart.setDataSets(listOf(dataSet));

        compare("scatter-chart-one-value.svg", chart);
    }

    @Test
    public void testDomainMarkers() throws Exception {
        var chart = new ScatterChart<Double, Double>(key -> key, Number::doubleValue);

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

        chart.setDomainMarkers(listOf(
            new Chart.Marker<>(0.25, 2.5, null, icon),
            new Chart.Marker<>(5.0, 50.0, null, icon)
        ));

        compare("scatter-chart-domain-markers.svg", chart);
    }

    @Test
    public void testRangeMarkers() throws Exception {
        var chart = new ScatterChart<Double, Double>(key -> key, Number::doubleValue);

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

        chart.setRangeMarkers(listOf(
            new Chart.Marker<>(0.25, 2.5, "Marker 1", icon),
            new Chart.Marker<>(2.0, 20.0, "Marker 2", icon)
        ));

        compare("scatter-chart-range-markers.svg", chart);
    }

    @Test
    public void testTrendLines() throws Exception {
        var chart = new ScatterChart<Double, Double>(key -> key, Number::doubleValue);

        chart.setShowTrendLines(true);

        var dataSet1 = new DataSet<Double, Double>("Positive Values", Color.RED);

        dataSet1.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, 10.0),
            entry(2.0, 20.0),
            entry(3.0, 30.0),
            entry(4.0, 40.0)
        ));

        var dataSet2 = new DataSet<Double, Double>("Negative Values", Color.GREEN);

        dataSet2.setDataPoints(sortedMapOf(
            entry(0.0, 0.0),
            entry(1.0, -10.0),
            entry(2.0, -20.0),
            entry(3.0, -30.0),
            entry(4.0, -40.0)
        ));

        chart.setDataSets(listOf(dataSet1, dataSet2));

        compare("scatter-chart-trend-lines.svg", chart);
    }
}
