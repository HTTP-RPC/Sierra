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
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Allows user to select a local time value.
 */
public class TimePicker extends JComponent {
    private LocalTime time = null;

    private int minuteInterval = 15;

    private List<ActionListener> actionListeners = new LinkedList<>();

    /**
     * Returns the selected time.
     *
     * @return
     * The selected time.
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Sets the selected time.
     *
     * @param time
     * The selected time.
     */
    public void setTime(LocalTime time) {
        // TODO Throw if not on interval
        this.time = time;
    }

    /**
     * Returns the minute interval. The default value is 15.
     *
     * @return
     * The minute interval.
     */
    public int getMinuteInterval() {
        return minuteInterval;
    }

    /**
     * Sets the minute interval. Must evenly divide into 60.
     *
     * @param minuteInterval
     * The minute interval.
     */
    public void setMinuteInterval(int minuteInterval) {
        // TODO Throw if value does not divide evenly into 60
        this.minuteInterval = minuteInterval;
    }

    /**
     * Adds an action listener.
     *
     * @param listener
     * The listener to add.
     */
    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    /**
     * Removes an action listener.
     *
     * @param listener
     * The listener to remove.
     */
    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }
}
