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
    private LocalTime time = LocalTime.now();

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
        this.time = time;
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
