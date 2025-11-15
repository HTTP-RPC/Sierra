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
package org.httprpc.sierra.previewer;

import com.formdev.flatlaf.FlatLightLaf;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.httprpc.sierra.UILoader;

import javax.swing.*;

public class SierraPreviewerApp {

    public static void main(String[] args) {
        FlatLightLaf.setup();

        UILoader.bind("syntax-text-area", RSyntaxTextArea.class, RSyntaxTextArea::new);

        // Run all UI code on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            var frame = new MainFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }
}