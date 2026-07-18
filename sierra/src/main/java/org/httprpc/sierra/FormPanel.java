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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

/**
 * Arranges components in a labled grid.
 */
public class FormPanel extends LayoutPanel {
    private class FormLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize(Container container) {
            var insets = getInsets();

            var preferredWidth = 0;
            var preferredHeight = 0;

            // TODO

            return new Dimension(preferredWidth + insets.left + insets.right, preferredHeight + insets.top + insets.bottom);
        }

        @Override
        public void layoutContainer(Container container) {
            var insets = getInsets();

            // TODO
        }
    }

    private int horizontalSpacing = 4;
    private int verticalSpacing = 4;

    /**
     * Constructs a new form panel.
     */
    public FormPanel() {
        setLayout(new FormLayoutManager());
    }

    @Override
    protected void addImpl(Component component, Object constraints, int index) {
        if (constraints != null && !(constraints instanceof String)) {
            throw new IllegalArgumentException("Invalid constraints.");
        }

        super.addImpl(component, constraints, index);
    }

    /**
     * Returns the horizontal spacing.
     *
     * @return
     * The horizontal spacing.
     */
    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    /**
     * Sets the horizontal spacing.
     *
     * @param horizontalSpacing
     * The horizontal spacing.
     */
    public void setHorizontalSpacing(int horizontalSpacing) {
        if (horizontalSpacing < 0) {
            throw new IllegalArgumentException();
        }

        this.horizontalSpacing = horizontalSpacing;
    }

    /**
     * Returns the vertical spacing.
     *
     * @return
     * The vertical spacing.
     */
    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    /**
     * Sets the vertical spacing.
     *
     * @param verticalSpacing
     * The vertical spacing.
     */
    public void setVerticalSpacing(int verticalSpacing) {
        if (verticalSpacing < 0) {
            throw new IllegalArgumentException();
        }

        this.verticalSpacing = verticalSpacing;
    }

    @Override
    public int getBaseline(int width, int height) {
        // TODO
        return -1;
    }
}
