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
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Text field that validates input against a regular expression.
 */
public class ValidatedTextField extends JTextField {
    private String value = null;

    private Pattern pattern = Pattern.compile("");

    private InputVerifier inputVerifier = new InputVerifier() {
        String value = null;

        @Override
        public boolean verify(JComponent input) {
            var text = getText();

            if (pattern.matcher(text).matches()) {
                value = text;

                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean shouldYieldFocus(JComponent source, JComponent target) {
            if (verify(source)) {
                if (!Objects.equals(value, ValidatedTextField.this.value)) {
                    setValue(value);

                    ValidatedTextField.super.fireActionPerformed();
                } else if (ValidatedTextField.this.value != null) {
                    setText(ValidatedTextField.this.value);
                } else {
                    setText(null);
                }

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
        setInputVerifier(inputVerifier);
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
     * Returns the regular expression pattern.
     *
     * @return
     * The regular expression pattern.
     */
    public String getPattern() {
        return pattern.toString();
    }

    /**
     * Sets the regular expression pattern.
     *
     * @param pattern
     * The regular expression pattern.
     */
    public void setPattern(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException();
        }

        this.pattern = Pattern.compile(pattern);
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
