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

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.httprpc.kilo.io.TextDecoder;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ChartTest {
    public static class TestChart extends Chart<String, Integer> {
        @Override
        protected void validate() {
            // No-op
        }

        @Override
        protected void draw(Graphics2D graphics) {
            // No-op
        }
    }

    @Test
    public void testRangeStep() {
        var testChart = new TestChart();

        assertEquals(30.0, testChart.calculateRangeStep(0.0, 100.0));
        assertEquals(300.0, testChart.calculateRangeStep(0.0, 1000.0));
        assertEquals(3000.0, testChart.calculateRangeStep(0.0, 10000.0));

        assertEquals(30.0, testChart.calculateRangeStep(-100.0, 0.0));
        assertEquals(300.0, testChart.calculateRangeStep(-1000.0, 0.0));
        assertEquals(3000.0, testChart.calculateRangeStep(-10000.0, 0.0));

        assertEquals(40.0, testChart.calculateRangeStep(-50.0, 100.0));
        assertEquals(40.0, testChart.calculateRangeStep(-100.0, 50.0));

        assertEquals(5.0, testChart.calculateRangeStep(80.0, 100.0));
        assertEquals(5.0, testChart.calculateRangeStep(-100.0, -80.0));

        assertEquals(0.3, testChart.calculateRangeStep(0.0, 1.0));
        assertEquals(0.03, testChart.calculateRangeStep(0.0, 0.1));
        assertEquals(0.003, testChart.calculateRangeStep(0.0, 0.01));

        assertEquals(0.3, testChart.calculateRangeStep(-1.0, 0.0));
        assertEquals(0.03, testChart.calculateRangeStep(-0.1, 0.0));
        assertEquals(0.003, testChart.calculateRangeStep(-0.01, 0.0));

        assertEquals(0.5, testChart.calculateRangeStep(-1.0, 1.0));
        assertEquals(0.05, testChart.calculateRangeStep(-0.1, 0.1));
        assertEquals(0.005, testChart.calculateRangeStep(-0.01, 0.01));
    }

    public static void compare(String name, BarChart<?, ?> barChart) throws IOException {
        var textDecoder = new TextDecoder();

        String expected;
        try (var inputStream = ChartTest.class.getResourceAsStream(name)) {
            expected = textDecoder.read(new InputStreamReader(inputStream));
        }

        var path = writeSVG(name, barChart);

        String actual;
        try {
            try (var inputStream = Files.newInputStream(path)) {
                actual = textDecoder.read(new InputStreamReader(inputStream));
            }
        } finally {
            Files.deleteIfExists(path);
        }

        assertEquals(expected, actual);
    }

    public static Path writeSVG(String name, BarChart<?, ?> barChart) throws IOException {
        var domImplementation = GenericDOMImplementation.getDOMImplementation();

        var document = domImplementation.createDocument("http://www.w3.org/2000/svg", "svg", null);

        var svgGraphics = new SVGGraphics2D(document);

        barChart.draw(svgGraphics, 320, 240);

        var path = Path.of(System.getProperty("user.dir"), name);

        try (var outputStream = Files.newOutputStream(path)) {
            svgGraphics.stream(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), false);
        }

        return path;
    }
}
