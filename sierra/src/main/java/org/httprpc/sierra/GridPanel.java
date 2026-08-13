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

/**
 * Abstract base class for grid panels.
 */
public abstract class GridPanel extends LayoutPanel {
    private int horizontalSpacing = 4;
    private int verticalSpacing = 4;

    /**
     * Returns the horizontal spacing. The default value is 4.
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
     * Returns the vertical spacing. The default value is 4.
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
}
