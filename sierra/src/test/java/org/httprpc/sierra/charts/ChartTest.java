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
import org.httprpc.kilo.xml.ElementAdapter;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ChartTest {
    public void compare(String name, BarChart<?, ?> barChart) throws Exception {
        var documentBuilder = ElementAdapter.newDocumentBuilder();

        Document expected;
        try (var inputStream = ChartTest.class.getResourceAsStream(name)) {
            if (inputStream != null) {
                expected = documentBuilder.parse(inputStream);
            } else {
                expected = documentBuilder.newDocument();
            }
        }

        var path = writeSVG(name, barChart);

        var result = false;

        Document actual;
        try {
            try (var inputStream = Files.newInputStream(path)) {
                actual = documentBuilder.parse(inputStream);
            }

            result = expected.isEqualNode(actual);
        } finally {
            if (result) {
                Files.deleteIfExists(path);
            }
        }

        assertTrue(result);
    }

    public Path writeSVG(String name, BarChart<?, ?> barChart) throws IOException {
        var domImplementation = GenericDOMImplementation.getDOMImplementation();

        var document = domImplementation.createDocument("http://www.w3.org/2000/svg", "svg", null);

        var svgGraphics = new SVGGraphics2D(document);

        barChart.draw(svgGraphics, 320, 240);

        var directory = Path.of(System.getProperty("user.dir"), "charts");

        Files.createDirectories(directory);

        var file = directory.resolve(name);

        try (var outputStream = Files.newOutputStream(file)) {
            svgGraphics.stream(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), false);
        }

        return file;
    }
}
