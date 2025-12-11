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

package org.httprpc.sierra.test;

import com.formdev.flatlaf.FlatLightLaf;
import org.httprpc.sierra.ChartPane;
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.UILoader;
import org.httprpc.sierra.charts.BarChart;
import org.httprpc.sierra.charts.Chart;
import org.httprpc.sierra.charts.DataPoint;
import org.httprpc.sierra.charts.DataSet;
import org.httprpc.sierra.charts.PieChart;
import org.httprpc.sierra.charts.TimeSeriesChart;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;

public class ChartsTest extends JFrame implements Runnable {
    private @Outlet ChartPane<Chart<?, ?>> pieChartPane = null;
    private @Outlet ChartPane<Chart<?, ?>> barChartPane = null;
    private @Outlet ChartPane<Chart<?, ?>> timeSeriesChartPane = null;

    private ChartsTest() {
        super("Charts Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "ChartsTest.xml"));

        pieChartPane.setChart(createPieChart());
        barChartPane.setChart(createBarChart());
        timeSeriesChartPane.setChart(createTimeSeriesChart());

        setSize(640, 480);
        setVisible(true);
    }

    private PieChart<Month, Double> createPieChart() {
        var pieChart = new PieChart<Month, Double>();

        pieChart.setDataSets(createCategoryDataSets());

        return pieChart;
    }

    private BarChart<Month, Double> createBarChart() {
        var barChart = new BarChart<Month, Double>();

        barChart.setDataSets(createCategoryDataSets());

        return barChart;
    }

    private List<DataSet<Month, Double>> createCategoryDataSets() {
        var eastDataSet = new DataSet<Month, Double>("East", UILoader.getColor("light-green"));

        eastDataSet.setDataPoints(listOf(
            new DataPoint<>(Month.JANUARY, 150.0),
            new DataPoint<>(Month.FEBRUARY, 10.0),
            new DataPoint<>(Month.MARCH, 325.0)
        ));

        var centralDataSet = new DataSet<Month, Double>("Central", UILoader.getColor("orange"));

        centralDataSet.setDataPoints(listOf(
            new DataPoint<>(Month.JANUARY, 60.0),
            new DataPoint<>(Month.FEBRUARY, 25.0),
            new DataPoint<>(Month.MARCH, 90.0)
        ));

        var westDataSet = new DataSet<Month, Double>("West", UILoader.getColor("light-blue"));

        westDataSet.setDataPoints(listOf(
            new DataPoint<>(Month.JANUARY, 220.0),
            new DataPoint<>(Month.FEBRUARY, 35.0),
            new DataPoint<>(Month.MARCH, 140.0)
        ));

        return listOf(eastDataSet, centralDataSet, westDataSet);
    }

    private TimeSeriesChart<Integer, Double> createTimeSeriesChart() {
        var timeSeriesChart = new TimeSeriesChart<Integer, Double>();

        timeSeriesChart.setDataSets(createTimeSeriesDataSets());

        return timeSeriesChart;
    }

    private List<DataSet<Integer, Double>> createTimeSeriesDataSets() {
        var colors = listOf(
            UILoader.getColor("light-green"),
            UILoader.getColor("orange"),
            UILoader.getColor("light-blue")
        );

        var m = colors.size();

        var n = 25;

        var dataSets = new ArrayList<DataSet<Integer, Double>>(m);

        for (var i = 0; i < m; i++) {
            var dataSet = new DataSet<Integer, Double>(String.format("Data Set %d", m), colors.get(i));

            var dataPoints = new ArrayList<DataPoint<Integer, Double>>(n);

            for (var j = 0; j < n; j++) {
                dataPoints.add(new DataPoint<>(j, Math.random() * 100.0));
            }

            dataSet.setDataPoints(dataPoints);

            dataSets.add(dataSet);
        }

        return dataSets;
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ChartsTest());
    }
}
