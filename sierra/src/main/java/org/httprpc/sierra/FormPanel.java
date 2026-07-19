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

import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * Arranges components in a labled grid.
 */
public class FormPanel extends LayoutPanel {
    private class FormLayoutManager extends AbstractLayoutManager {
        @Override
        public Dimension preferredLayoutSize(Container container) {
            var insets = getInsets();

            var maximumLabelWidth = 0;
            var maximumFieldWidth = 0;

            var totalRowHeight = 0;

            var n = labels.size();

            for (var i = 0; i < n; i++) {
                var label = labels.get(i);
                var field = fields.get(i);

                var labelSize = label.getPreferredSize();
                var fieldSize = field.getPreferredSize();

                maximumLabelWidth = Math.max(maximumLabelWidth, labelSize.width);
                maximumFieldWidth = Math.max(maximumFieldWidth, fieldSize.width);

                var labelBaseline = label.getBaseline(labelSize.width, labelSize.height);
                var fieldBaseline = field.getBaseline(fieldSize.width, fieldSize.height);

                if (fieldBaseline >= 0) {
                    var maximumAscent = Math.max(labelBaseline, fieldBaseline);
                    var maximumDescent = Math.max(labelSize.height - labelBaseline, fieldSize.height - fieldBaseline);

                    totalRowHeight += maximumAscent + maximumDescent;
                } else {
                    totalRowHeight += Math.max(labelSize.height, fieldSize.height);
                }
            }

            var preferredWidth = maximumLabelWidth + maximumFieldWidth + horizontalSpacing + insets.left + insets.right;
            var preferredHeight = totalRowHeight + verticalSpacing * (n - 1) + insets.top + insets.bottom;

            return new Dimension(preferredWidth, preferredHeight);
        }

        @Override
        public void layoutContainer(Container container) {
            var insets = getInsets();

            var maximumLabelWidth = 0;
            var maximumFieldWidth = 0;

            var n = labels.size();

            for (var i = 0; i < n; i++) {
                var label = labels.get(i);
                var field = fields.get(i);

                label.setSize(label.getPreferredSize());
                field.setSize(field.getPreferredSize());

                maximumLabelWidth = Math.max(maximumLabelWidth, label.getWidth());
                maximumFieldWidth = Math.max(maximumFieldWidth, field.getWidth());
            }

            var y = insets.top;

            for (var i = 0; i < n; i++) {
                var label = labels.get(i);
                var field = fields.get(i);

                var labelSize = label.getSize();
                var fieldSize = field.getSize();

                if (fieldSize.width == 0) {
                    field.setSize(maximumFieldWidth, fieldSize.height);
                }

                var labelBaseline = label.getBaseline(labelSize.width, labelSize.height);
                var fieldBaseline = field.getBaseline(fieldSize.width, fieldSize.height);

                int labelOffset;
                int fieldOffset;
                int rowHeight;
                if (fieldBaseline >= 0) {
                    var maximumAscent = Math.max(labelBaseline, fieldBaseline);
                    var maximumDescent = Math.max(labelSize.height - labelBaseline, fieldSize.height - fieldBaseline);

                    labelOffset = maximumAscent - labelBaseline;
                    fieldOffset = maximumAscent - fieldBaseline;

                    rowHeight = maximumAscent + maximumDescent;
                } else {
                    labelOffset = 0;
                    fieldOffset = 0;

                    rowHeight = Math.max(labelSize.height, fieldSize.height);
                }

                label.setLocation(insets.left + maximumLabelWidth - labelSize.width, y + labelOffset);
                field.setLocation(insets.left + maximumLabelWidth + horizontalSpacing, y + fieldOffset);

                y += rowHeight + verticalSpacing;
            }
        }
    }

    private List<JLabel> labels = new ArrayList<>();
    private List<Component> fields = new ArrayList<>();

    private int horizontalSpacing = 4;
    private int verticalSpacing = 4;

    /**
     * Constructs a new form panel.
     */
    public FormPanel() {
        setLayout(new FormLayoutManager());
    }

    @Override
    public void add(Component component, Object constraints) {
        var label = new JLabel((String)constraints);

        addImpl(label, null, -1);
        addImpl(component, constraints, -1);

        labels.add(label);
        fields.add(component);
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
}
