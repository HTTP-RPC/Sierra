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

public class PieChartTest extends ChartTest {
    @Test
    public void testMissingValue() throws Exception {
        var chart = new PieChart<Integer, Integer>();

        var dataSet1 = new DataSet<Integer, Integer>("Data Set 1", Color.RED);

        dataSet1.setDataPoints(sortedMapOf(
            entry(1, 10),
            entry(2, 20),
            entry(3, 30)
        ));

        var dataSet2 = new DataSet<Integer, Integer>("Data Set 2", Color.GREEN);

        dataSet2.setDataPoints(sortedMapOf(
            entry(1, 5),
            entry(2, 10)
        ));

        chart.setDataSets(listOf(dataSet1, dataSet2));

        compare("pie-chart-missing-value.svg", chart);
    }

    @Test
    public void testNoValues() throws Exception {
        var chart = new PieChart<Integer, Integer>();

        compare("pie-chart-no-values.svg", chart);
    }

    @Test
    public void testDoughnut() throws Exception {
        var chart = new PieChart<Integer, Integer>(true);

        var dataSet1 = new DataSet<Integer, Integer>("Data Set 1", Color.RED);

        dataSet1.setDataPoints(sortedMapOf(
            entry(1, 20)
        ));

        var dataSet2 = new DataSet<Integer, Integer>("Data Set 2", Color.GREEN);

        dataSet2.setDataPoints(sortedMapOf(
            entry(1, 50)
        ));

        var dataSet3 = new DataSet<Integer, Integer>("Data Set 3", Color.BLUE);

        dataSet3.setDataPoints(sortedMapOf(
            entry(1, 30)
        ));

        chart.setDataSets(listOf(dataSet1, dataSet2, dataSet3));

        compare("pie-chart-doughnut.svg", chart);
    }

    @Test
    public void testNoValuesDoughnut() throws Exception {
        var chart = new PieChart<Integer, Integer>(true);

        compare("pie-chart-no-values-doughnut.svg", chart);
    }
}
