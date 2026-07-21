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
 * Arranges components in a vertical line.
 */
public class ColumnPanel extends BoxPanel {
    private class ColumnLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize(Container container) {
            var contentWidth = 0;
            var contentHeight = 0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var preferredSize = component.getPreferredSize();

                contentWidth = Math.max(contentWidth, preferredSize.width);

                if (Double.isNaN(getWeight(i))) {
                    contentHeight += preferredSize.height;
                }
            }

            var insets = getInsets();

            var preferredWidth = contentWidth + insets.left + insets.right;
            var preferredHeight = contentHeight + getSpacing() * (n - 1) + insets.top + insets.bottom;

            return new Dimension(preferredWidth, preferredHeight);
        }

        @Override
        public void layoutContainer(Container container) {
            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);

            var excessHeight = Math.max(size.height - (insets.top + insets.bottom), 0);
            var totalWeight = 0.0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (Double.isNaN(weight)) {
                    component.setSize(width, Integer.MAX_VALUE);
                    component.setSize(width, component.getPreferredSize().height);

                    excessHeight -= component.getHeight();
                } else {
                    component.setSize(width, 0);

                    totalWeight += weight;
                }
            }

            var spacing = getSpacing();

            excessHeight = Math.max(excessHeight - spacing * (n - 1), 0);

            var y = insets.top;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (!Double.isNaN(weight)) {
                    component.setSize(component.getWidth(), (int)Math.round(excessHeight * (weight / totalWeight)));
                }

                component.setLocation(insets.left, y);

                y += component.getHeight() + spacing;
            }
        }
    }

    /**
     * Constructs a new column panel.
     */
    public ColumnPanel() {
        setLayout(new ColumnLayoutManager());
    }
}
