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

package org.httprpc.sierra.tools.dtd;

import org.httprpc.kilo.io.TextDecoder;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DTDEncoderTest {
    @Test
    public void testDTDEncoder() throws Exception {
        var workingPath = Path.of(System.getProperty("user.dir"));

        var bindingsPath = workingPath.resolve("bindings.properties");

        var properties = new Properties();

        properties.put("chart-panel", "org.jfree.chart.ChartPanel");

        var libraryPath = workingPath.resolve("lib");

        Files.createDirectories(libraryPath);

        var dependencyName = "jfreechart-1.5.6.jar";

        var dependencyPath = libraryPath.resolve(dependencyName);

        try (var inputStream = DTDEncoderTest.class.getResourceAsStream(dependencyName)) {
            Files.copy(inputStream, dependencyPath, StandardCopyOption.REPLACE_EXISTING);
        }

        var dtdPath = workingPath.resolve("sierra.dtd");

        try {
            try (var outputStream = Files.newOutputStream(bindingsPath)) {
                properties.store(outputStream, "Test Bindings");
            }

            DTDEncoder.main(new String[]{
                bindingsPath.getFileName().toString(),
                libraryPath.getFileName().toString()
            });

            String text;
            try (var inputStream = Files.newInputStream(dtdPath)) {
                var textDecoder = new TextDecoder();

                text = textDecoder.read(inputStream);
            }

            assertTrue(text.contains("chart-panel"));
        } finally {
            Files.deleteIfExists(bindingsPath);
            Files.deleteIfExists(dependencyPath);
            Files.deleteIfExists(libraryPath);

            Files.deleteIfExists(dtdPath);
        }
    }
}
