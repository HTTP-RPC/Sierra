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

/**
 * Shows indeterminate progress.
 */
public class ActivityIndicator extends JComponent {
    private static class ActivityIndicatorUI extends ComponentUI {
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
            // TODO
        }
    }

    private boolean active = false;

    private static int activeCount = 0;

    private static int angle = 0;

    private static Timer timer; // TODO

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
        // TODO If active count is 0, start timer

        activeCount++;

        active = true;
    }

    /**
     * Stops the activity indicator.
     */
    public void stop() {
        active = false;

        activeCount--;

        // TODO If active count is 0, stop timer
    }
}
