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
package org.httprpc.sierra.tools.previewer.engine;

import org.httprpc.sierra.UILoader;
import org.httprpc.sierra.tools.previewer.model.RenderError;
import org.httprpc.sierra.tools.previewer.model.RenderResult;

import javax.swing.JPanel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class RenderingEngine {
    /**
     * Renders the given XML text by first saving it to a target file.
     * If targetPath is not null, it saves to that file.
     * If targetPath is null, it saves to a temporary file.
     * * @param xmlText The XML content to render.
     * @param targetPath The file path to save the XML content to. Can be null.
     * @return The result of the rendering operation.
     */
    public RenderResult render(String xmlText, Path targetPath) { // MODIFIED SIGNATURE
        if (xmlText == null || xmlText.isBlank()) {
            return new RenderResult.Success(new JPanel());
        }

        var savePath = targetPath;

        try {
            if (savePath == null) {
                // If no file is open, create a temporary file as fallback
                savePath = Files.createTempFile("sierrapreview", ".xml");
                // Only delete temporary files on exit
                savePath.toFile().deleteOnExit();
            }

            // Write the content to the chosen path (either the open file or the temp file)
            // This is the core change: saving to the targetPath instead of always a temp file.
            Files.writeString(savePath, xmlText,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

            System.out.println("Data saved for rendering to file: " + savePath.toAbsolutePath());

            var rootComponent = UILoader.load(savePath);

            return new RenderResult.Success(rootComponent);
        } catch (IOException e) {
            return new RenderResult.Error(new RenderError(e.getMessage(), e));
        }
    }
}