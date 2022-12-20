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
 * Arranges components horizontally in a row, optionally pinning component
 * edges to the container's insets.
 */
public class RowPanel extends BoxPanel {
    private class RowLayoutManager extends AbstractLayoutManager {
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

    private boolean alignToBaseline;

    /**
     * Constructs a new row panel.
     */
    public RowPanel() {
        this(HorizontalAlignment.FILL, VerticalAlignment.CENTER, 4, true);
    }

    /**
     * Constructs a new row panel.
     *
     * @param horizontalAlignment
     * The horizontal alignment.
     *
     * @param verticalAlignment
     * The vertical alignment.
     *
     * @param spacing
     * The spacing value.
     *
     * @param alignToBaseline
     * {@code true} to align components to baseline; {@code false}, otherwise.
     */
    public RowPanel(HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int spacing, boolean alignToBaseline) {
        super(horizontalAlignment, verticalAlignment, spacing);

        this.alignToBaseline = alignToBaseline;

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
     * Indicates that components will be aligned to baseline.
     *
     * @return
     * {@code true} if components will be baseline-aligned; {@code false},
     * otherwise.
     */
    public boolean getAlignToBaseline() {
        return alignToBaseline;
    }

    /**
     * Toggles baseline alignment.
     *
     * @param alignToBaseline
     * {@code true} to align components to baseline; {@code false}, otherwise.
     */
    public void setAlignToBaseline(boolean alignToBaseline) {
        this.alignToBaseline = alignToBaseline;

        revalidate();
    }
}
