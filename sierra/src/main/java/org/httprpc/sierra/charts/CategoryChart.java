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

package org.httprpc.sierra.charts;

import org.httprpc.sierra.TextPane;

import java.util.SortedSet;

import static org.httprpc.kilo.util.Collections.*;

/**
 * Abstract base class for category charts.
 *
 * @param <K>
 * The key type.
 *
 * @param <V>
 * The value type.
 */
public abstract class CategoryChart<K extends Comparable<? super K>, V> extends Chart<K, V> {
    protected SortedSet<K> keys = sortedSetOf();

    protected double maximumDomainLabelWidth = 0.0;

    CategoryChart() {
    }

    @Override
    protected int getColumnCount() {
        return keys.isEmpty() ? 1 : keys.size();
    }

    @Override
    protected void populateDomainLabels() {
        maximumDomainLabelWidth = 0.0;

        var domainLabelTransform = getDomainLabelTransform();
        var domainLabelFont = getDomainLabelFont();

        if (keys.isEmpty()) {
            var textPane = new TextPane("");

            textPane.setFont(domainLabelFont);
            textPane.setSize(textPane.getPreferredSize());

            domainLabelTextPanes.add(textPane);
        } else {
            for (var key : keys) {
                var label = domainLabelTransform.apply(key);

                var textPane = new TextPane(label);

                textPane.setFont(domainLabelFont);
                textPane.setSize(textPane.getPreferredSize());

                maximumDomainLabelWidth = Math.max(maximumDomainLabelWidth, textPane.getWidth());

                domainLabelTextPanes.add(textPane);
            }
        }
    }

    @Override
    protected void validateDomainLabels() {
        var showDomainLabels = maximumDomainLabelWidth < columnWidth * 0.85;

        var keyCount = keys.size();

        var domainLabelX = chartOffset;
        var domainLabelY = chartHeight + DOMAIN_LABEL_SPACING + horizontalGridLineWidth;

        for (var i = 0; i < keyCount; i++) {
            var textPane = domainLabelTextPanes.get(i);

            var size = textPane.getSize();

            int x;
            if (showDomainLabels) {
                x = (int)(domainLabelX + columnWidth / 2) - size.width / 2;
            } else if (i == 0) {
                x = (int)domainLabelX;
            } else if (i < keyCount - 1) {
                x = (int)(domainLabelX + columnWidth / 2) - size.width / 2;

                textPane.setText(null);
            } else {
                x = (int)(domainLabelX + columnWidth) - size.width;
            }

            textPane.setLocation(x, (int)domainLabelY);
            textPane.doLayout();

            domainLabelX += columnWidth;
        }
    }
}
