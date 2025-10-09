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
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.util.regex.Pattern;

import static org.httprpc.kilo.util.Optionals.*;

/**
 * Text field that validates input against a regular expression.
 */
public class ValidatedTextField extends JTextField {
    private String value = null;

    private Pattern pattern = Pattern.compile(".*");

    private InputVerifier inputVerifier = new InputVerifier() {
        String value = null;

        @Override
        public boolean verify(JComponent input) {
            if (input != ValidatedTextField.this) {
                throw new IllegalArgumentException();
            }

            var text = getText();

            value = null;

            if (!text.isEmpty()) {
                if (pattern.matcher(text).matches()) {
                    value = text;
                } else {
                    return false;
                }
            }

            return true;

        }

        @Override
        public boolean shouldYieldFocus(JComponent source, JComponent target) {
            if (verify(source)) {
                setText(value);

                ValidatedTextField.this.value = value;

                value = null;

                return true;
            } else {
                UIManager.getLookAndFeel().provideErrorFeedback(source);

                return false;
            }
        }
    };

    /**
     * Constructs a new validated text field.
     */
    public ValidatedTextField() {
        super.setInputVerifier(inputVerifier);

        addActionListener(event -> {
            if (inputVerifier.shouldYieldFocus(this, null)) {
                perform(map(getRootPane(), JRootPane::getDefaultButton), AbstractButton::doClick);
            }
        });
    }

    /**
     * Returns the field's value.
     *
     * @return
     * The field's value, or {@code null} if no value has been set.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the field's value.
     *
     * @param value
     * The field's value, or {@code null} for no value.
     */
    public void setValue(String value) {
        if (value != null && !pattern.matcher(value).matches()) {
            throw new IllegalArgumentException();
        }

        setText(value);

        this.value = value;
    }

    /**
     * Returns the validation pattern. The default value is ".*".
     *
     * @return
     * The validation pattern.
     */
    public String getPattern() {
        return pattern.toString();
    }

    /**
     * Sets the validation pattern.
     *
     * @param pattern
     * The validation pattern.
     */
    public void setPattern(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException();
        }

        this.pattern = Pattern.compile(pattern, Pattern.UNICODE_CHARACTER_CLASS);
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    @Override
    public void setInputVerifier(InputVerifier inputVerifier) {
        throw new UnsupportedOperationException();
    }

    /**
     * Verifies the contents of the text field.
     * {@inheritDoc}
     */
    @Override
    protected void fireActionPerformed() {
        if (inputVerifier.shouldYieldFocus(this, null)) {
            super.fireActionPerformed();
        }
    }
}
