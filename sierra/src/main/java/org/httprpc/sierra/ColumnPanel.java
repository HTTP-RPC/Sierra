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
 * Arranges components vertically in a column, optionally pinning component
 * edges to the container's insets.
 */
public class ColumnPanel extends BoxPanel {
    private class ColumnLayoutManager extends AbstractLayoutManager {
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

    private boolean alignToGrid;

    /**
     * Constructs a new column panel.
     */
    public ColumnPanel() {
        this(HorizontalAlignment.FILL, VerticalAlignment.FILL, 4, true);
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
     * @param alignToGrid
     * {@code true} to align components to grid; {@code false}, otherwise.
     */
    public ColumnPanel(HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int spacing, boolean alignToGrid) {
        super(horizontalAlignment, verticalAlignment, spacing);

        this.alignToGrid = alignToGrid;

        setLayout(new ColumnLayoutManager());
    }

    /**
     * Sets the layout manager.
     * {@inheritDoc}
     */
    @Override
    public void setLayout(LayoutManager layoutManager) {
        if (layoutManager != null && !(layoutManager instanceof ColumnLayoutManager)) {
            throw new IllegalArgumentException();
        }

        super.setLayout(layoutManager);
    }

    /**
     * Indicates that components will be aligned to grid.
     *
     * @return
     * {@code true} if components will be grid-aligned; {@code false},
     * otherwise.
     */
    public boolean getAlignToGrid() {
        return alignToGrid;
    }

    /**
     * Toggles grid alignment.
     *
     * @param alignToGrid
     * {@code true} to align components to grid; {@code false}, otherwise.
     */
    public void setAlignToGrid(boolean alignToGrid) {
        this.alignToGrid = alignToGrid;

        revalidate();
    }
}
