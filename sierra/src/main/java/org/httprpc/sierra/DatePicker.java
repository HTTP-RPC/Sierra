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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

/**
 * Text field that supports local date entry.
 */
public class DatePicker extends JTextField {
    private LocalDate date = null;

    private LocalDate minimumDate;
    private LocalDate maximumDate;

    private final InputVerifier inputVerifier = new InputVerifier() {
        @Override
        public boolean verify(JComponent input) {
            try {
                var date = LocalDate.parse(getText(), dateFormatter);

                if (date.equals(DatePicker.this.date)) {
                    return true;
                }

                if (!validate(date)) {
                    return false;
                }

                DatePicker.this.date = date;

                DatePicker.super.fireActionPerformed();

                return true;
            } catch (DateTimeParseException exception) {
                return false;
            }
        }
    };

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    /**
     * Constructs a new date picker.
     */
    public DatePicker() {
        super(8);

        setInputVerifier(inputVerifier);
    }

    /**
     * Returns the selected date.
     *
     * @return
     * The selected date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the selected date.
     *
     * @param date
     * The selected date.
     */
    public void setDate(LocalDate date) {
        if (date == null) {
            super.setText(null);
        } else {
            if (!validate(date)) {
                throw new IllegalArgumentException();
            }

            super.setText(dateFormatter.format(date));
        }

        this.date = date;
    }

    private boolean validate(LocalDate date) {
        return (minimumDate == null || !date.isBefore(minimumDate))
            && (maximumDate == null || !date.isAfter(maximumDate));
    }

    /**
     * Returns the minimum value allowed by this date picker.
     *
     * @return
     * The minimum date, or {@code null} if no minimum date is set.
     */
    public LocalDate getMinimumDate() {
        return minimumDate;
    }

    /**
     * Sets the minimum value allowed by this date picker.
     *
     * @param minimumDate
     * The minimum date, or {@code null} for no minimum date.
     */
    public void setMinimumDate(LocalDate minimumDate) {
        if (minimumDate != null) {
            if (maximumDate != null && minimumDate.isAfter(maximumDate)) {
                throw new IllegalStateException();
            }

            if (date != null && date.isBefore(minimumDate)) {
                setDate(minimumDate);
            }
        }

        this.minimumDate = minimumDate;
    }

    /**
     * Returns the maximum value allowed by this date picker.
     *
     * @return
     * The maximum date, or {@code null} if no maximum date is set.
     */
    public LocalDate getMaximumDate() {
        return maximumDate;
    }

    /**
     * Sets the maximum value allowed by this date picker.
     *
     * @param maximumDate
     * The maximum date, or {@code null} for no maximum date.
     */
    public void setMaximumDate(LocalDate maximumDate) {
        if (maximumDate != null) {
            if (minimumDate != null && maximumDate.isBefore(minimumDate)) {
                throw new IllegalStateException();
            }

            if (date != null && date.isAfter(maximumDate)) {
                setDate(maximumDate);
            }
        }

        this.maximumDate = maximumDate;
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    @Override
    public void setText(String text) {
        throw new UnsupportedOperationException();
    }

    /**
     * Verifies the contents of the text field.
     * {@inheritDoc}
     */
    @Override
    protected void fireActionPerformed() {
        inputVerifier.verify(this);
    }
}
