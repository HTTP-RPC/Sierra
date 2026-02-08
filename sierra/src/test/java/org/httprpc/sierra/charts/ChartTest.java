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

import com.formdev.flatlaf.FlatLightLaf;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.httprpc.kilo.xml.ElementAdapter;
import org.junit.jupiter.api.BeforeAll;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ChartTest {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    @BeforeAll
    public static void setUp() {
        System.setProperty("java.awt.headless", "true");

        FlatLightLaf.setup();
    }

    public void compare(String name, Chart<?, ?> chart) throws Exception {
        chart.setSize(WIDTH, HEIGHT);

        var documentBuilder = ElementAdapter.newDocumentBuilder();

        Document expected;
        try (var inputStream = ChartTest.class.getResourceAsStream(name)) {
            if (inputStream != null) {
                expected = documentBuilder.parse(inputStream);
            } else {
                expected = documentBuilder.newDocument();
            }
        }

        var path = writeSVG(name, chart);

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

    private Path writeSVG(String name, Chart<?, ?> chart) throws Exception {
        var domImplementation = GenericDOMImplementation.getDOMImplementation();

        var document = domImplementation.createDocument("http://www.w3.org/2000/svg", "svg", null);

        var svgGraphics = new SVGGraphics2D(document);

        chart.draw(svgGraphics);

        var writer = new StringWriter();

        svgGraphics.stream(writer, false);

        var directory = Path.of(System.getProperty("user.dir"), "charts");

        Files.createDirectories(directory);

        var file = directory.resolve(name);

        var transformer = ElementAdapter.newTransformer();

        try (var outputStream = Files.newOutputStream(file)) {
            transformer.transform(new DOMSource(getDocument(writer.toString())), new StreamResult(outputStream));
        } catch (TransformerException exception) {
            throw new IOException(exception);
        }

        return file;
    }

    private Document getDocument(String content) throws Exception {
        var documentBuilder = ElementAdapter.newDocumentBuilder();

        var document = documentBuilder.parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        var documentElement = document.getDocumentElement();

        documentElement.setAttribute("width", String.valueOf(WIDTH));
        documentElement.setAttribute("height", String.valueOf(HEIGHT));

        return document;
    }
}
