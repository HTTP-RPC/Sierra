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

import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.httprpc.kilo.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;

public class BarChartTest {
    @Test
    public void testPositiveValues() throws IOException {
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

        compare("positive-values.svg", barChart);
    }

    @Test
    public void testNegativeValues() throws IOException {
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

        compare("negative-values.svg", barChart);
    }

    @Test
    public void testMixedValues() throws IOException {
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

        compare("mixed-values.svg", barChart);
    }

    private void compare(String name, BarChart<?, ?> barChart) throws IOException {
        var textDecoder = new TextDecoder();

        String expected;
        try (var inputStream = getClass().getResourceAsStream(name)) {
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

    private Path writeSVG(String name, BarChart<?, ?> barChart) throws IOException {
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
