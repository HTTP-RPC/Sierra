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
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.httprpc.sierra.ChartPane;
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.RowPanel;
import org.httprpc.sierra.UILoader;
import org.httprpc.sierra.charts.BarChart;
import org.httprpc.sierra.charts.Chart;
import org.httprpc.sierra.charts.DataSet;
import org.httprpc.sierra.charts.PieChart;
import org.httprpc.sierra.charts.TimeSeriesChart;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.text.NumberFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import static org.httprpc.kilo.util.Collections.*;

public class ChartsTest extends JFrame implements Runnable {
    private @Outlet ChartPane<Chart<?, ?>> pieChartPane = null;
    private @Outlet RowPanel pieChartLegendPanel = null;

    private @Outlet ChartPane<Chart<?, ?>> barChartPane = null;
    private @Outlet RowPanel barChartLegendPanel = null;

    private @Outlet ChartPane<Chart<?, ?>> timeSeriesChartPane = null;
    private @Outlet RowPanel timeSeriesChartLegendPanel = null;

    private ChartsTest() {
        super("Charts Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "ChartsTest.xml"));

        pieChartPane.setChart(createPieChart());

        for (var dataSet : pieChartPane.getChart().getDataSets()) {
            pieChartLegendPanel.add(new JLabel(dataSet.getLabel(),
                new PieChart.LegendIcon(dataSet),
                SwingConstants.LEADING));
        }

        barChartPane.setChart(createBarChart());

        for (var dataSet : barChartPane.getChart().getDataSets()) {
            barChartLegendPanel.add(new JLabel(dataSet.getLabel(),
                new BarChart.LegendIcon(dataSet),
                SwingConstants.LEADING));
        }

        timeSeriesChartPane.setChart(createTimeSeriesChart());

        for (var dataSet : timeSeriesChartPane.getChart().getDataSets()) {
            timeSeriesChartLegendPanel.add(new JLabel(dataSet.getLabel(),
                new TimeSeriesChart.LegendIcon(dataSet),
                SwingConstants.LEADING));
        }

        setSize(640, 480);
        setVisible(true);
    }

    private PieChart<Month, Double> createPieChart() {
        var pieChart = new PieChart<Month, Double>(true);

        pieChart.setDataSets(createCategoryDataSets());

        return pieChart;
    }

    private BarChart<Month, Double> createBarChart() {
        var barChart = new BarChart<Month, Double>(false);

        barChart.setBarTransparency(0.75);

        var rangeLabelFormat = NumberFormat.getNumberInstance();

        rangeLabelFormat.setMinimumFractionDigits(1);
        rangeLabelFormat.setMaximumFractionDigits(1);

        barChart.setRangeLabelTransform(rangeLabelFormat::format);
        barChart.setDomainLabelTransform(month -> month.getDisplayName(TextStyle.FULL, Locale.getDefault()));

        barChart.setVerticalGridLineStroke(new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL,
            1.0f, new float[] {3.0f}, 0.0f));

        barChart.setDataSets(createCategoryDataSets());

        return barChart;
    }

    private List<DataSet<Month, Double>> createCategoryDataSets() {
        var northDataSet = new DataSet<Month, Double>("North", UILoader.getColor("light-coral"));

        northDataSet.setDataPoints(sortedMapOf(
            entry(Month.JANUARY, 30.0),
            entry(Month.FEBRUARY, 5.0),
            entry(Month.MARCH, 20.0)
        ));

        var southDataSet = new DataSet<Month, Double>("South", UILoader.getColor("orange"));

        southDataSet.setDataPoints(sortedMapOf(
            entry(Month.JANUARY, 40.0),
            entry(Month.FEBRUARY, 10.0),
            entry(Month.MARCH, 75.0)
        ));

        var eastDataSet = new DataSet<Month, Double>("East", UILoader.getColor("gold"));

        eastDataSet.setDataPoints(sortedMapOf(
            entry(Month.JANUARY, 60.0),
            entry(Month.FEBRUARY, 5.0),
            entry(Month.MARCH, 80.0)
        ));

        var centralDataSet = new DataSet<Month, Double>("Central", UILoader.getColor("light-green"));

        centralDataSet.setDataPoints(sortedMapOf(
            entry(Month.JANUARY, 60.0),
            entry(Month.FEBRUARY, 45.0),
            entry(Month.MARCH, 90.0)
        ));

        var westDataSet = new DataSet<Month, Double>("West", UILoader.getColor("light-blue"));

        westDataSet.setDataPoints(sortedMapOf(
            entry(Month.JANUARY, 85.0),
            entry(Month.FEBRUARY, 35.0),
            entry(Month.MARCH, 140.0)
        ));

        return listOf(northDataSet, southDataSet, eastDataSet, centralDataSet, westDataSet);
    }

    private TimeSeriesChart<Integer, Double> createTimeSeriesChart() {
        var timeSeriesChart = new TimeSeriesChart<Integer, Double>(key -> key, Number::intValue);

        var n = 250;

        var icon = new FlatSVGIcon(getClass().getResource("icons/flag_24dp.svg"));

        icon = icon.derive(18, 18);

        var rangeLabelFormat = NumberFormat.getNumberInstance();

        rangeLabelFormat.setMinimumFractionDigits(1);
        rangeLabelFormat.setMaximumFractionDigits(1);

        timeSeriesChart.setRangeLabelTransform(rangeLabelFormat::format);

        timeSeriesChart.setHorizontalGridLineStroke(new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL,
            1.0f, new float[] {3.0f}, 0.0f));

        timeSeriesChart.setDataSets(createTimeSeriesDataSets(n));
        timeSeriesChart.setDomainMarkers(listOf(
            new Chart.Marker<>((int)(Math.random() * n), null, "Marker 1", icon),
            new Chart.Marker<>((int)(Math.random() * n), null, "Marker 2", icon)
        ));

        return timeSeriesChart;
    }

    private List<DataSet<Integer, Double>> createTimeSeriesDataSets(int n) {
        var colors = listOf(
            UILoader.getColor("light-coral"),
            UILoader.getColor("light-green"),
            UILoader.getColor("light-blue")
        );

        var m = colors.size();

        var dataSets = new ArrayList<DataSet<Integer, Double>>(m);

        for (var i = 0; i < m; i++) {
            var dataSet = new DataSet<Integer, Double>(String.format("Data Set %d", i + 1), colors.get(i));

            var dataPoints = new TreeMap<Integer, Double>();

            for (var j = 0; j < n; j++) {
                double value;
                if (j == 0) {
                    value = 100 - i * 100;
                } else {
                    value = dataPoints.get(j - 1) + (1.0 - Math.random() * 2.0) * 25.0;
                }

                dataPoints.put(j, value);
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
