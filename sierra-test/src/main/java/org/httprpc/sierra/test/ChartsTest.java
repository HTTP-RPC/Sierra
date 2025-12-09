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
import java.time.LocalDate;

import static org.httprpc.kilo.util.Collections.*;

public class ChartsTest extends JFrame implements Runnable {
    private @Outlet ChartPane<String, Double> pieChartPane = null;
    private @Outlet ChartPane<String, Double> barChartPane = null;
    private @Outlet ChartPane<LocalDate, Double> timeSeriesChartPane = null;

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

    private PieChart<String, Double> createPieChart() {
        var one = new DataPoint<>("one", 10.0);

        one.setLabel("One");
        one.setColor(UILoader.getColor("light-green"));

        var two = new DataPoint<>("two", 10.0);

        two.setLabel("Two");
        two.setColor(UILoader.getColor("light-yellow"));

        var three = new DataPoint<>("three", 30.0);

        three.setLabel("Three");
        three.setColor(UILoader.getColor("orange"));

        var four = new DataPoint<>("four", 50.0);

        four.setLabel("Four");
        four.setColor(UILoader.getColor("light-blue"));

        var dataSet = new DataSet<String, Double>("values", "Values");

        dataSet.setDataPoints(listOf(one, two, three, four));

        var pieChart = new PieChart<String, Double>(120);

        pieChart.setInnerRadius(100);

        pieChart.setDataSets(listOf(dataSet));

        return pieChart;
    }

    private BarChart<String, Double> createBarChart() {
        var dataSetA = new DataSet<String, Double>("a", "A");

        dataSetA.setDataPoints(listOf(
            new DataPoint<>("one", 10.0),
            new DataPoint<>("two", 10.0),
            new DataPoint<>("three", 30.0),
            new DataPoint<>("four", 50.0)
        ));

        dataSetA.setColor(UILoader.getColor("light-green"));

        var dataSetB = new DataSet<String, Double>("b", "B");

        dataSetB.setDataPoints(listOf(
            new DataPoint<>("one", 20.0),
            new DataPoint<>("two", 15.0),
            new DataPoint<>("three", 40.0),
            new DataPoint<>("four", 10.0)
        ));

        dataSetB.setColor(UILoader.getColor("light-blue"));

        var barChart = new BarChart<String, Double>(Chart.Orientation.HORIZONTAL, false);

        barChart.setDataSets(listOf(dataSetA, dataSetB));

        return barChart;
    }

    private TimeSeriesChart<LocalDate, Double> createTimeSeriesChart() {
        var today = LocalDate.now();

        var dataSetA = new DataSet<LocalDate, Double>("a", "A");

        dataSetA.setRangeAxis(Chart.RangeAxis.LEADING);
        dataSetA.setColor(UILoader.getColor("light-green"));

        dataSetA.setDataPoints(listOf(
            new DataPoint<>(today.minusDays(10), 10.0),
            new DataPoint<>(today.minusDays(5), 10.0),
            new DataPoint<>(today.minusDays(1), 30.0),
            new DataPoint<>(today, 50.0)
        ));

        var dataSetB = new DataSet<LocalDate, Double>("b", "B");

        dataSetB.setRangeAxis(Chart.RangeAxis.TRAILING);
        dataSetB.setColor(UILoader.getColor("light-blue"));

        dataSetB.setDataPoints(listOf(
            new DataPoint<>(today.minusDays(10), 200.0),
            new DataPoint<>(today.minusDays(5), 150.0),
            new DataPoint<>(today.minusDays(1), 400.0),
            new DataPoint<>(today, 100.0)
        ));

        var timeSeriesChart = new TimeSeriesChart<LocalDate, Double>(LocalDate.class);

        timeSeriesChart.setDataSets(listOf(dataSetA, dataSetB));

        return timeSeriesChart;
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ChartsTest());
    }
}
