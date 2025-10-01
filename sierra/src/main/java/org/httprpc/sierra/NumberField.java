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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

/**
 * Text field that supports numeric data entry.
 */
public class NumberField extends JTextField {
    private Number value = null;

    private NumberFormat format = NumberFormat.getNumberInstance();

    private InputVerifier inputVerifier = new InputVerifier() {
        Number value = null;

        @Override
        public boolean verify(JComponent input) {
            var text = getText();

            if (!text.isEmpty()) {
                try {
                    value = format.parse(text);
                } catch (ParseException exception) {
                    return false;
                }
            } else {
                value = null;
            }

            return true;
        }

        @Override
        public boolean shouldYieldFocus(JComponent source, JComponent target) {
            if (verify(source)) {
                if (!Objects.equals(value, NumberField.this.value)) {
                    NumberField.this.value = value;

                    NumberField.super.fireActionPerformed();
                } else if (NumberField.this.value != null) {
                    setText(format.format(NumberField.this.value));
                } else {
                    setText(null);
                }

                selectAll();

                value = null;

                return true;
            } else {
                UIManager.getLookAndFeel().provideErrorFeedback(source);

                return false;
            }
        }
    };

    /**
     * Constructs a new number field.
     */
    public NumberField() {
        format.setGroupingUsed(false);

        setInputVerifier(inputVerifier);
    }

    /**
     * Returns the field's numeric value.
     *
     * @return
     * The field's numeric value, or {@code null} if no value is set.
     */
    public Number getValue() {
        return value;
    }

    /**
     * Sets the field's numeric value.
     *
     * @param value
     * The field's numeric value, or {@code null} for no value.
     */
    public void setValue(Number value) {
        if (value != null) {
            setText(format.format(value));
        } else {
            setText(null);
        }

        this.value = value;
    }

    /**
     * Verifies the contents of the text field.
     * {@inheritDoc}
     */
    @Override
    protected void fireActionPerformed() {
        inputVerifier.shouldYieldFocus(this, null);
    }
}
