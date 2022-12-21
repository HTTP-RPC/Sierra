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
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.event.FocusEvent;

/**
 * Keyboard focus manager that automatically scrolls focused components into
 * view.
 */
public class ScrollingKeyboardFocusManager extends DefaultKeyboardFocusManager {
    /**
     * Dispatches an event.
     * {@inheritDoc}
     */
    @Override
    public boolean dispatchEvent(AWTEvent event) {
        var dispatched = super.dispatchEvent(event);

        if (dispatched && event.getID() == FocusEvent.FOCUS_GAINED) {
            var component = (Component)event.getSource();
            var parent = component.getParent();

            if (parent instanceof JComponent) {
                ((JComponent)parent).scrollRectToVisible(SwingUtilities.convertRectangle(parent, component.getBounds(), parent));
            }
        }

        return dispatched;
    }
}
