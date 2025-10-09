package org.httprpc.sierra;

import javax.swing.AbstractButton;
import javax.swing.JRootPane;
import java.awt.event.KeyEvent;

import static org.httprpc.kilo.util.Optionals.*;

/**
 * Abstract base class for temporal pickers.
 */
public abstract class TemporalPicker extends Picker {
    /**
     * Constructs a new temporal picker.
     */
    protected TemporalPicker(int columns) {
        super(columns);

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
