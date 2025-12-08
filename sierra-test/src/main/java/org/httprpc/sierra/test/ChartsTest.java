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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.time.Instant;

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

        // TODO
        lineChartPane.setChart(null);
        barChartPane.setChart(null);
        pieChartPane.setChart(null);

        setSize(640, 480);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new ChartsTest());
    }
}
