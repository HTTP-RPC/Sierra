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
import org.httprpc.sierra.charts.DataPoint;
import org.httprpc.sierra.charts.DataSet;
import org.httprpc.sierra.charts.LineChart;
import org.httprpc.sierra.charts.PieChart;
import org.httprpc.sierra.charts.TimeSeriesChart;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.time.Instant;

import static org.httprpc.kilo.util.Collections.*;

public class ChartsTest extends JFrame implements Runnable {
    private @Outlet ChartPane<String, Double> pieChartPane = null;
    private @Outlet ChartPane<String, Double> barChartPane = null;
    private @Outlet ChartPane<Double, Double> lineChartPane = null;
    private @Outlet ChartPane<Instant, Double> timeSeriesChartPane = null;

    private ChartsTest() {
        super("Charts Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "ChartsTest.xml"));

        pieChartPane.setChart(createPieChart());
        barChartPane.setChart(createBarChart());
        lineChartPane.setChart(createLineChart());
        timeSeriesChartPane.setChart(createTimeSeriesChart());

        setSize(640, 480);
        setVisible(true);
    }

    private PieChart<String, Double> createPieChart() {
        var one = new DataPoint<>("one", 10.0);

        one.setLabel("One");
        one.setColor(UILoader.getColor("light-green"));

        var two = new DataPoint<>("one", 10.0);

        one.setLabel("Two");
        one.setColor(UILoader.getColor("light-yellow"));

        var three = new DataPoint<>("three", 30.0);

        one.setLabel("Three");
        one.setColor(UILoader.getColor("orange"));

        var four = new DataPoint<>("four", 50.0);

        one.setLabel("Four");
        one.setColor(UILoader.getColor("light-blue"));

        var dataSet = new DataSet<String, Double>("values", "Values");

        dataSet.setDataPoints(listOf(one, two, three, four));

        var pieChart = new PieChart<String, Double>(120);

        pieChart.setSize(320, 240);

        pieChart.setInnerRadius(100);

        pieChart.setDataSets(listOf(dataSet));

        return pieChart;
    }

    private BarChart<String, Double> createBarChart() {
        // TODO
        return null;
    }

    private LineChart<Double, Double> createLineChart() {
        // TODO
        return null;
    }

    private TimeSeriesChart<Instant, Double> createTimeSeriesChart() {
        // TODO
        return null;
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ChartsTest());
    }
}
