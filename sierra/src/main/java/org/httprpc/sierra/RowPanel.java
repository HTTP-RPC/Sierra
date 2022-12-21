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
 * Arranges sub-components horizontally in a row, optionally pinning component
 * edges to the container's insets.
 */
public class RowPanel extends BoxPanel {
    private class RowLayoutManager extends BoxLayoutManager {
        @Override
        public Dimension preferredLayoutSize() {
            // TODO
            return null;
        }


        @Override
        public void layoutContainer() {
            // TODO
        }
    }

    private boolean alignToBaseline = true;

    /**
     * Constructs a new row panel.
     */
    public RowPanel() {
        super(HorizontalAlignment.FILL, VerticalAlignment.CENTER, 4);

        setLayout(new RowLayoutManager());
    }

    /**
     * Sets the layout manager.
     * {@inheritDoc}
     */
    @Override
    public void setLayout(LayoutManager layoutManager) {
        if (layoutManager != null && !(layoutManager instanceof RowLayoutManager)) {
            throw new IllegalArgumentException();
        }

        super.setLayout(layoutManager);
    }

    /**
     * Indicates that sub-components will be aligned to baseline.
     *
     * @return
     * {@code true} to align to baseline; {@code false}, otherwise.
     */
    public boolean getAlignToBaseline() {
        return alignToBaseline;
    }

    /**
     * Toggles baseline alignment.
     *
     * @param alignToBaseline
     * {@code true} to align to baseline; {@code false}, otherwise.
     */
    public void setAlignToBaseline(boolean alignToBaseline) {
        this.alignToBaseline = alignToBaseline;

        revalidate();
    }
}
