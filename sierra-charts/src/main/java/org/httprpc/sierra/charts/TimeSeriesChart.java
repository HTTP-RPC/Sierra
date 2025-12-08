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

import java.awt.Color;
import java.awt.Graphics2D;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import static org.httprpc.kilo.util.Collections.*;

/**
 * Time series chart.
 *
 * @param <K>
 * The key type.
 */
public class TimeSeriesChart<K extends TemporalAccessor, V extends Number> extends Chart<K, V> {
    private Class<K> keyType;

    private List<DataPoint<K, V>> markers = listOf();

    /**
     * Constructs a new time series chart.
     *
     * @param keyType
     * The key type.
     */
    public TimeSeriesChart(Class<K> keyType) {
        if (keyType == null) {
            throw new IllegalArgumentException();
        }

        this.keyType = keyType;
    }

    /**
     * Returns the key type.
     *
     * @return
     * The key type.
     */
    public Class<K> getKeyType() {
        return keyType;
    }

    /**
     * Returns the chart markers.
     *
     * @return
     * The chart markers.
     */
    public List<DataPoint<K, V>> getMarkers() {
        return markers;
    }

    /**
     * Sets the chart markers.
     *
     * @param markers
     * The chart markers.
     */
    public void setMarkers(List<DataPoint<K, V>> markers) {
        if (markers == null) {
            throw new IllegalArgumentException();
        }

        this.markers = markers;
    }

    /**
     * Draws the time series chart.
     * {@inheritDoc}
     */
    @Override
    public void draw(Graphics2D graphics) {
        // TODO
        graphics.setColor(Color.BLUE);

        var width = getWidth();
        var height = getHeight();

        graphics.drawRect(0, 0, width, height);
    }
}
