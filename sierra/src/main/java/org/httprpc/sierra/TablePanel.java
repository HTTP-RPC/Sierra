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

import java.awt.Container;
import java.awt.Dimension;

/**
 * Arranges components in a two-dimensional grid.
 */
public class TablePanel extends LayoutPanel {
    private class TableLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize(Container container) {
            // TODO
            return new Dimension(0, 0);
        }

        @Override
        public void layoutContainer(Container container) {
            // TODO
        }
    }

    /**
     * Constructs a new table panel.
     */
    public TablePanel() {
        setLayout(new TableLayoutManager());
    }
}
