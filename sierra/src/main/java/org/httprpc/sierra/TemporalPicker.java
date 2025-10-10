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

import javax.swing.AbstractButton;
import javax.swing.JRootPane;
import java.awt.event.KeyEvent;

import static org.httprpc.kilo.util.Optionals.*;

/**
 * Abstract base class for temporal pickers.
 */
public abstract class TemporalPicker extends Picker {
    TemporalPicker() {
        addActionListener(event -> {
            if (getInputVerifier().shouldYieldFocus(this, null)) {
                perform(map(getRootPane(), JRootPane::getDefaultButton), AbstractButton::doClick);
            }
        });
    }

    /**
     * Verifies the contents of the text field.
     * {@inheritDoc}
     */
    @Override
    protected void fireActionPerformed() {
        if (getInputVerifier().shouldYieldFocus(this, null)) {
            super.fireActionPerformed();
        }
    }

    /**
     * Processes a key event.
     * {@inheritDoc}
     */
    @Override
    protected void processKeyEvent(KeyEvent event) {
        if (event.getID() == KeyEvent.KEY_TYPED) {
            var keyChar = event.getKeyChar();

            if (Character.isSpaceChar(keyChar)) {
                event.setKeyChar((char)0x202f);
            }
        }

        super.processKeyEvent(event);
    }
}
