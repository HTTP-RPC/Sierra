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
import java.util.ArrayList;
import java.util.List;

import static org.httprpc.kilo.util.Optionals.*;

/**
 * Abstract base class for box panels.
 */
public abstract class BoxPanel extends LayoutPanel {
    private List<Double> weights = new ArrayList<>();

    private int spacing = 0;

    @Override
    protected void addImpl(Component component, Object constraints, int index) {
        super.addImpl(component, constraints, index);

        var weight = coalesce(map((Number)constraints, Number::doubleValue), () -> Double.NaN);

        weights.add(index == -1 ? weights.size() : index, weight);
    }

    @Override
    public void remove(int index) {
        super.remove(index);

        weights.remove(index);
    }

    @Override
    public void removeAll() {
        super.removeAll();

        weights.clear();
    }

    /**
     * Returns the weight value associated with a component.
     *
     * @param index
     * The component index.
     *
     * @return
     * The component's weight, or {@link Double#NaN} if the component does not
     * have a weight.
     */
    protected double getWeight(int index) {
        return weights.get(index);
    }

    /**
     * Returns the amount of space between successive components. The default
     * value is 0.
     *
     * @return
     * The component spacing.
     */
    public int getSpacing() {
        return spacing;
    }

    /**
     * Sets the amount of space between successive components.
     *
     * @param spacing
     * The component spacing.
     */
    public void setSpacing(int spacing) {
        if (spacing < 0) {
            throw new IllegalArgumentException();
        }

        this.spacing = spacing;

        revalidate();
        repaint();
    }

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
