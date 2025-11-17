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

import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * Horizontal alignment options.
 */
public enum HorizontalAlignment implements ConstantAdapter {
    /**
     * Left alignment.
     */
    LEFT("left", SwingConstants.LEFT),

    /**
     * Right alignment.
     */
    RIGHT("right", SwingConstants.RIGHT),

    /**
     * Center alignment.
     */
    CENTER("center", SwingConstants.CENTER),

    /**
     * Leading alignment.
     */
    LEADING("leading", SwingConstants.LEADING),

    /**
     * Trailing alignment.
     */
    TRAILING("trailing", SwingConstants.TRAILING);

    private final String key;
    private final int value;

    HorizontalAlignment(String key, int value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public int getValue() {
        return value;
    }

    HorizontalAlignment getLocalizedValue(JComponent component) {
        return switch (this) {
            case LEFT, RIGHT, CENTER -> this;
            case LEADING -> component.getComponentOrientation().isLeftToRight() ? LEFT : RIGHT;
            case TRAILING -> component.getComponentOrientation().isLeftToRight() ? RIGHT : LEFT;
        };
    }
}
