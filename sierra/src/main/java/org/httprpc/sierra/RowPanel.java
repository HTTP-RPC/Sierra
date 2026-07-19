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

import java.awt.Container;
import java.awt.Dimension;

/**
 * Arranges components in a horizontal line.
 */
public class RowPanel extends BoxPanel {
    private class RowLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize(Container container) {
            var contentWidth = 0;
            var contentHeight = 0;

            var maximumAscent = 0;
            var maximumDescent = 0;

            var n = getComponentCount();

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var preferredSize = component.getPreferredSize();

                if (Double.isNaN(getWeight(i))) {
                    contentWidth += preferredSize.width;
                }

                if (alignToBaseline) {
                    var baseline = component.getBaseline(preferredSize.width, preferredSize.height);

                    if (baseline >= 0) {
                        maximumAscent = Math.max(maximumAscent, baseline);
                        maximumDescent = Math.max(maximumDescent, preferredSize.height - baseline);
                    }
                } else {
                    contentHeight = Math.max(contentHeight, preferredSize.height);
                }
            }

            if (alignToBaseline) {
                contentHeight = maximumAscent + maximumDescent;
            }

            var insets = getInsets();

            var preferredWidth = contentWidth + getSpacing() * (n - 1) + insets.left + insets.right;
            var preferredHeight = contentHeight + insets.top + insets.bottom;

            return new Dimension(preferredWidth, preferredHeight);
        }

        @Override
        public void layoutContainer(Container container) {
            var size = getSize();
            var insets = getInsets();

            var excessWidth = Math.max(size.width - (insets.left + insets.right), 0);
            var totalWeight = 0.0;

            var height = Math.max(size.height - (insets.top + insets.bottom), 0);

            var n = getComponentCount();

            int[] baselines;
            if (alignToBaseline) {
                baselines = new int[n];
            } else {
                baselines = null;
            }

            var maximumBaseline = 0;

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (Double.isNaN(weight)) {
                    component.setSize(Integer.MAX_VALUE, height);

                    if (alignToBaseline) {
                        component.setSize(component.getPreferredSize());

                        var baseline = component.getBaseline(component.getWidth(), component.getHeight());

                        baselines[i] = baseline;

                        if (baseline >= 0) {
                            maximumBaseline = Math.max(maximumBaseline, baseline);
                        }
                    } else {
                        component.setSize(component.getPreferredSize().width, height);
                    }

                    excessWidth -= component.getWidth();
                } else {
                    totalWeight += weight;
                }
            }

            var spacing = getSpacing();

            excessWidth = Math.max(excessWidth - spacing * (n - 1), 0);

            var leftToRight = getComponentOrientation().isLeftToRight();

            int x;
            if (leftToRight) {
                x = insets.left;
            } else {
                x = size.width - insets.right;
            }

            for (var i = 0; i < n; i++) {
                var component = getComponent(i);

                var weight = getWeight(i);

                if (!Double.isNaN(weight)) {
                    component.setSize((int)Math.round(excessWidth * (weight / totalWeight)), height);
                }

                int y;
                if (alignToBaseline) {
                    var baseline = baselines[i];

                    if (baseline >= 0) {
                        y = insets.top + (maximumBaseline - baseline);
                    } else {
                        y = (height - component.getHeight()) / 2;
                    }
                } else {
                    y = insets.top;
                }

                if (leftToRight) {
                    component.setLocation(x, y);

                    x += component.getWidth() + spacing;
                } else {
                    x-= component.getWidth();

                    component.setLocation(x, y);

                    x -= spacing;
                }
            }
        }
    }

    private boolean alignToBaseline = false;

    /**
     * Constructs a new row panel.
     */
    public RowPanel() {
        setLayout(new RowLayoutManager());
    }

    /**
     * Indicates that components will be aligned to baseline. The default value
     * is {@code false}.
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
     * {@code true} to enable baseline alignment; {@code false} to disable it.
     */
    public void setAlignToBaseline(boolean alignToBaseline) {
        this.alignToBaseline = alignToBaseline;

        revalidate();
        repaint();
    }

    @Override
    public int getBaseline(int width, int height) {
        return alignToBaseline ? super.getBaseline(width, height) : -1;
    }
}
