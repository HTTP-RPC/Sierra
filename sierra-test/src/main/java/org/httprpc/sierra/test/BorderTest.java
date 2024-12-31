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
import java.awt.Font;
import java.util.ResourceBundle;

public class BorderTest extends JFrame implements Runnable {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(BorderTest.class.getName());

    private BorderTest() {
        super(resourceBundle.getString("title"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(UILoader.load(this, "border-test.xml", resourceBundle));

        setSize(480, 320);
        setVisible(true);
    }

    public static void main(String[] args) {
        UILoader.define("h1", new Font("Arial", Font.BOLD, 24));
        UILoader.define("h2", new Font("Arial", Font.PLAIN, 18));

        FlatLightLaf.setup();

        SwingUtilities.invokeLater(new BorderTest());
    }
}