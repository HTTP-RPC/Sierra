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
    // Abstract base class for box layout managers
    abstract static class BoxLayoutManager extends AbstractLayoutManager {
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

    private int spacing = 0;

    /**
     * Returns the amount of space between successive sub-components.
     *
     * @return
     * The sub-component spacing.
     */
    public int getSpacing() {
        return spacing;
    }

    /**
     * Sets the amount of space between successive sub-components.
     *
     * @param spacing
     * The sub-component spacing.
     */
    public void setSpacing(int spacing) {
        if (spacing < 0) {
            throw new IllegalArgumentException();
        }

        this.spacing = spacing;

        revalidate();
    }
}
