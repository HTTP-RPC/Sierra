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

import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Sizes sub-components to fill the available space. The panel's preferred size
 * is the maximum preferred width/height of all sub-components, plus insets.
 */
public class StackPanel extends LayoutPanel {
    private class StackLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize() {
            var insets = getInsets();

            var preferredWidth = 0;
            var preferredHeight = 0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var preferredSize = component.getPreferredSize();

                preferredWidth = Math.max(preferredWidth, preferredSize.width);
                preferredHeight = Math.max(preferredHeight, preferredSize.height);
            }

            return new Dimension(preferredWidth + insets.left + insets.right, preferredHeight + insets.top + insets.bottom);
        }

        @Override
        public void layoutContainer() {
            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);
            var height = Math.max(size.height - (insets.top + insets.bottom), 0);

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                getComponent(i).setBounds(insets.left, insets.top, width, height);
            }
        }
    }

    /**
     * Constructs a new stack panel.
     */
    public StackPanel() {
        setLayout(new StackLayoutManager());
    }

    /**
     * Sets the layout manager.
     * {@inheritDoc}
     */
    @Override
    public void setLayout(LayoutManager layoutManager) {
        if (layoutManager != null && !(layoutManager instanceof StackLayoutManager)) {
            throw new IllegalArgumentException();
        }

        super.setLayout(layoutManager);
    }
}
