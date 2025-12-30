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

import java.util.function.Function;

/**
 * Abstract base class for XY charts.
 *
 * @param <K>
 * The key type.
 *
 * @param <V>
 * The value type.
 */
public abstract class XYChart<K extends Comparable<? super K>, V> extends Chart<K, V> {
    protected Function<Number, K> domainKeyTransform;
    protected Function<K, Number> domainValueTransform;

    XYChart(Function<K, Number> domainValueTransform, Function<Number, K> domainKeyTransform) {
        if (domainKeyTransform == null || domainValueTransform == null) {
            throw new IllegalArgumentException();
        }

        this.domainKeyTransform = domainKeyTransform;
        this.domainValueTransform = domainValueTransform;
    }

    /**
     * Returns the domain value transform.
     *
     * @return
     * The domain value transform.
     */
    public Function<K, Number> getDomainValueTransform() {
        return domainValueTransform;
    }

    /**
     * Returns the domain key transform.
     *
     * @return
     * The domain key transform.
     */
    public Function<Number, K> getDomainKeyTransform() {
        return domainKeyTransform;
    }

    @Override
    protected int getColumnCount() {
        return getDomainLabelCount() - 1;
    }

    @Override
    protected void populateDomainLabels() {
        var domainLabelCount = getDomainLabelCount();

        var domainLabelTransform = getDomainLabelTransform();
        var domainLabelFont = getDomainLabelFont();

        var domainStep = (domainMaximum - domainMinimum) / (domainLabelCount - 1);

        for (var i = 0; i < domainLabelCount; i++) {
            var key = domainKeyTransform.apply(domainMinimum + domainStep * i);

            var textPane = new TextPane(domainLabelTransform.apply(key));

            textPane.setFont(domainLabelFont);

            domainLabelTextPanes.add(textPane);
        }
    }

    @Override
    protected void validateDomainLabels() {
        var domainLabelCount = getDomainLabelCount();

        var domainLabelX = chartOffset;
        var domainLabelY = chartHeight + DOMAIN_LABEL_SPACING + horizontalGridLineWidth;

        for (var i = 0; i < domainLabelCount; i++) {
            var textPane = domainLabelTextPanes.get(i);

            textPane.setSize(textPane.getPreferredSize());

            var size = textPane.getSize();

            int x;
            if (i == 0) {
                x = (int)domainLabelX;
            } else if (i < domainLabelCount - 1) {
                x = (int)domainLabelX - size.width / 2;
            } else {
                x = (int)domainLabelX - size.width;
            }

            textPane.setLocation(x, (int)domainLabelY);
            textPane.doLayout();

            domainLabelX += columnWidth;
        }
    }
}
