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

package org.httprpc.sierra;

import javax.swing.JComponent;
import java.awt.Dimension;

/**
 * Provides space between components.
 */
public class Spacer extends JComponent {
    /**
     * Constructs a new spacer.
     *
     * @param size
     * The spacer's size.
     */
    public Spacer(int size) {
        setPreferredSize(new Dimension(size, size));
    }
}
