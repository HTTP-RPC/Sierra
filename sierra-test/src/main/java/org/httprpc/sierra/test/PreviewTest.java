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
import org.httprpc.sierra.UILoader;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.httprpc.kilo.util.Collections.*;

public class PreviewTest extends JFrame implements Runnable {
    private PreviewTest() {
        super("Preview Test");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        var workingPath = Path.of(System.getProperty("java.io.tmpdir"));

        var names = listOf("GreetingTest.xml", "world.png");
        var paths = names.stream().map(workingPath::resolve).toList();

        try {
            try {
                var n = names.size();

                for (var i = 0; i < n; i++) {
                    try (var inputStream = GreetingTest.class.getResourceAsStream(names.get(i))) {
                        Files.copy(inputStream, paths.get(i), StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                setContentPane(UILoader.load(paths.getFirst()));
            } finally {
                for (var path : paths) {
                    Files.deleteIfExists(path);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setSize(320, 480);
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new PreviewTest());
    }
}
