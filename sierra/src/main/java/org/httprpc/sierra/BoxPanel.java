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
 * Abstract base class for box panels.
 */
public abstract class BoxPanel extends LayoutPanel {
    private int spacing = 0;

    BoxPanel() {
    }

    double getWeight(int index) {
        var constraints = getConstraints(index);

        if (constraints instanceof Double weight) {
            if (weight <= 0.0) {
                throw new IllegalStateException();
            }

            return weight;
        } else {
            return Double.NaN;
        }
    }

    /**
     * Returns the amount of space between successive sub-components. The
     * default value is 0.
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

    /**
     * Calculates the box panel's baseline.
     * {@inheritDoc}
     */
    @Override
    public int getBaseline(int width, int height) {
        setSize(width, height);
        validate();

        var n = getComponentCount();

        for (var i = 0; i < n; i++) {
            var component = getComponent(i);

            var baseline = component.getBaseline(component.getWidth(), component.getHeight());

            if (baseline >= 0) {
                return component.getY() + baseline;
            }
        }

        return -1;
    }
}
