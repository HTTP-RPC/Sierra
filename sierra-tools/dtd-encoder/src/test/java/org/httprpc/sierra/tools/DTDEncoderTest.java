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

package org.httprpc.sierra.tools;

import org.httprpc.kilo.io.TextDecoder;
import org.jfree.chart.ChartPanel;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DTDEncoderTest {
    @Test
    public void testDTDEncoder() throws Exception {
        var workingPath = Path.of(System.getProperty("user.dir"));

        var bindingsPath = workingPath.resolve("bindings.properties");
        var dtdPath = workingPath.resolve("sierra.dtd");

        var properties = new Properties();

        properties.put("chart-panel", ChartPanel.class.getName());

        try {
            try (var outputStream = Files.newOutputStream(bindingsPath)) {
                properties.store(outputStream, "Test Bindings");
            }

            DTDEncoder.main(new String[]{bindingsPath.getFileName().toString()});

            String text;
            try (var inputStream = Files.newInputStream(dtdPath)) {
                var textDecoder = new TextDecoder();

                text = textDecoder.read(inputStream);
            }

            assertTrue(text.contains("chart-panel"));
        } finally {
            Files.deleteIfExists(bindingsPath);
            Files.deleteIfExists(dtdPath);
        }
    }
}
