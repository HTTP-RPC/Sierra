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
import javax.swing.plaf.ComponentUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;
import java.util.List;

/**
 * Shows indeterminate progress.
 */
public class ActivityIndicator extends JComponent {
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
            return new Dimension(48, 48);
        }

        @Override
        public int getBaseline(JComponent component, int width, int height) {
            return -1;
        }

        @Override
        public void paint(Graphics graphics, JComponent component) {
            paint((Graphics2D)graphics);
        }

        private void paint(Graphics2D graphics) {
            if (!active) {
                return;
            }

            var size = getSize();
            var insets = getInsets();

            var width = Math.max(size.width - (insets.left + insets.right), 0);
            var height = Math.max(size.height - (insets.top + insets.bottom), 0);

            graphics = (Graphics2D)graphics.create();

            graphics.setClip(insets.left, insets.top, width, height);

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            // TODO
            System.out.println(angle);

            graphics.dispose();
        }
    }

    private boolean active = false;

    private static int angle = 0;

    private static List<ActivityIndicator> activeInstances = new LinkedList<>();

    private static final int SPOKE_COUNT = 6;

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
    }
}
