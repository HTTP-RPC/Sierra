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

/**
 * Abstract base class for box panels.
 */
public abstract class BoxPanel extends LayoutPanel {
    /**
     * Abstract base class for box layout managers.
     */
    protected abstract static class BoxLayoutManager extends AbstractLayoutManager {
        /**
         * Returns a component's weight.
         *
         * @param component
         * The component.
         *
         * @return
         * The component's weight.
         */
        protected double getWeight(Component component) {
            var constraints = getConstraints(component);

            if (constraints == null) {
                return Double.NaN;
            }

            if (!(constraints instanceof Double)) {
                throw new IllegalStateException();
            }

            return (double)constraints;
        }
    }

    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;

    private int spacing;

    /**
     * Constructs a new box panel.
     *
     * @param horizontalAlignment
     * The horizontal alignment.
     *
     * @param verticalAlignment
     * The vertical alignment.
     *
     * @param spacing
     * The spacing value.
     */
    protected BoxPanel(HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int spacing) {
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;

        this.spacing = spacing;
    }

    /**
     * Returns the horizontal alignment.
     *
     * @return
     * The horizontal alignment.
     */
    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Sets the horizontal alignment.
     *
     * @param horizontalAlignment
     * The horizontal alignment.
     */
    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        if (horizontalAlignment == null) {
            throw new IllegalArgumentException();
        }

        this.horizontalAlignment = horizontalAlignment;

        revalidate();
    }

    /**
     * Returns the vertical alignment.
     *
     * @return
     * The vertical alignment.
     */
    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the vertical alignment.
     *
     * @param verticalAlignment
     * The vertical alignment.
     */
    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        if (verticalAlignment == null) {
            throw new IllegalArgumentException();
        }

        this.verticalAlignment = verticalAlignment;

        revalidate();
    }

    /**
     * Returns the spacing value.
     *
     * @return
     * The amount of space between successive sub-components.
     */
    public int getSpacing() {
        return spacing;
    }

    /**
     * Sets the spacing value.
     *
     * @param spacing
     * The amount of space between successive sub-components.
     */
    public void setSpacing(int spacing) {
        this.spacing = spacing;

        revalidate();
    }
}
