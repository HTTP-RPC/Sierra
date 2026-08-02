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
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.HashSet;
import java.util.Set;

/**
 * Shows indeterminate progress.
 */
public class ActivityIndicator extends JComponent {
    /**
     * Indicator style options.
     */
    public enum IndicatorStyle {
        /**
         * Small.
         */
        SMALL,

        /**
         * Medium.
         */
        MEDIUM,

        /**
         * Large.
         */
        LARGE
    }

    private class ActivityIndicatorUI extends ComponentUI {
        @Override
        public Dimension getMinimumSize(JComponent component) {
            return new Dimension(0, 0);
        }

        @Override
        public Dimension getMaximumSize(JComponent component) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Override
        public Dimension getPreferredSize(JComponent component) {
            var insets = getInsets();

            var indicatorSize = getIndicatorSize();

            var preferredWidth = indicatorSize + (insets.left + insets.right);
            var preferredHeight = indicatorSize + (insets.top + insets.bottom);

            return new Dimension(preferredWidth, preferredHeight);
        }

        @Override
        public void paint(Graphics graphics, JComponent component) {
            paint((Graphics2D)graphics);
        }

        void paint(Graphics2D graphics) {
            if (!active) {
                return;
            }

            var insets = getInsets();

            var width = Math.max(getWidth() - (insets.left + insets.right), 0);
            var height = Math.max(getHeight() - (insets.top + insets.bottom), 0);

            graphics = (Graphics2D)graphics.create();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            var indicatorSize = getIndicatorSize();

            var radius = indicatorSize / 2;

            var x = (width - indicatorSize) / 2 + insets.left + radius;
            var y = (height - indicatorSize) / 2 + insets.top + radius;

            graphics.translate(x, y);

            graphics.rotate((angle % 360) * Math.PI / 180);

            var foreground = getForeground();

            var spokeWidth = indicatorSize / 3.0;
            var spokeHeight = indicatorSize / 8.0;

            var spokeShape = new RoundRectangle2D.Double(spokeWidth / 2, -spokeHeight / 2, spokeWidth, spokeHeight, spokeHeight, spokeHeight);

            for (var i = 0; i < SPOKE_COUNT; i++) {
                var alpha = (int)Math.round((i * (1.0 / SPOKE_COUNT)) * 255);

                var color = new Color(foreground.getRed(), foreground.getGreen(), foreground.getBlue(), alpha);

                graphics.setColor(color);
                graphics.fill(spokeShape);

                graphics.rotate(INCREMENT);
            }

            graphics.dispose();
        }

        int getIndicatorSize() {
            return switch (indicatorStyle) {
                case SMALL -> 12;
                case MEDIUM -> 18;
                case LARGE -> 24;
            };
        }
    }

    private IndicatorStyle indicatorStyle = IndicatorStyle.MEDIUM;

    private boolean active = false;

    private static int angle = 0;

    private static Set<ActivityIndicator> activeInstances = new HashSet<>();

    private static final int SPOKE_COUNT = 8;

    private static final double INCREMENT = (2 * Math.PI) / SPOKE_COUNT;

    private static Timer timer = new Timer(100, event -> {
        angle = (angle + 360 / SPOKE_COUNT) % 360;

        for (var instance : activeInstances) {
            instance.repaint();
        }
    });

    /**
     * Constructs a new activity indicator.
     */
    public ActivityIndicator() {
        setUI(new ActivityIndicatorUI());

        setForeground(UIManager.getColor("Label.disabledForeground"));
    }

    /**
     * Constructs a new activity indicator.
     *
     * @param indicatorStyle
     * The indicator style.
     */
    public ActivityIndicator(IndicatorStyle indicatorStyle) {
        this();

        setIndicatorStyle(indicatorStyle);
    }

    /**
     * Returns the indicator style. The default value is {@link IndicatorStyle#MEDIUM}.
     *
     * @return
     * The indicator style.
     */
    public IndicatorStyle getIndicatorStyle() {
        return indicatorStyle;
    }

    /**
     * Sets the indicator style.
     *
     * @param indicatorStyle
     * The indicator style.
     */
    public void setIndicatorStyle(IndicatorStyle indicatorStyle) {
        if (indicatorStyle == null) {
            throw new IllegalArgumentException();
        }

        this.indicatorStyle = indicatorStyle;

        revalidate();
        repaint();
    }

    /**
     * Indicates that the activity indicator is active.
     *
     * @return
     * {@code true} if the indicator is active; {@code false}, otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Starts the activity indicator.
     */
    public void start() {
        repaint();

        if (activeInstances.isEmpty()) {
            timer.start();
        }

        activeInstances.add(this);

        active = true;
    }

    /**
     * Stops the activity indicator.
     */
    public void stop() {
        active = false;

        activeInstances.remove(this);

        if (activeInstances.isEmpty()) {
            timer.stop();
        }

        repaint();
    }
}
