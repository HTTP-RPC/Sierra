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

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.function.Function;

/**
 * Scatter chart.
 */
public class ScatterChart<K extends Comparable<? super K>, V extends Number> extends XYChart<K, Collection<V>> {
    /**
     * Constructs a new scatter chart.
     *
     * @param domainValueTransform
     * The domain value transform.
     *
     * @param domainKeyTransform
     * The domain key transform.
     */
    public ScatterChart(Function<K, Number> domainValueTransform, Function<Number, K> domainKeyTransform) {
        super(domainValueTransform, domainKeyTransform);
    }

    @Override
    public void validate() {
        // TODO
    }

    @Override
    protected void draw(Graphics2D graphics) {
        // TODO
    }
}
