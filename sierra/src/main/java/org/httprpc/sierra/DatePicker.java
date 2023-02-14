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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

import static org.httprpc.sierra.UIBuilder.cell;
import static org.httprpc.sierra.UIBuilder.row;

/**
 * Text field that supports local date entry.
 */
public class DatePicker extends Picker {
    private static class CalendarPanel extends ColumnPanel {
        CalendarPanel() {
            setSpacing(6);

            var spinner = new JSpinner();

            // TODO Use first day of month for current date
            // TODO Pass minimum and maximum values

            spinner.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.MONTH));

            // TODO Set editor

            spinner.setFocusable(false);

            spinner.addChangeListener(event -> {
                // TODO
            });

            add(spinner);

            var daysColumnPanel = new ColumnPanel();

            daysColumnPanel.setAlignToGrid(true);

            Consumer<JLabel> labelStyle = label -> {
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setAlignmentX(0.5f);
                label.putClientProperty("FlatLaf.styleClass", "small");
            };

            // TODO Use localized names and first day of week
            daysColumnPanel.add(row(2,
                cell(new JLabel("Mon")).with(labelStyle),
                cell(new JLabel("Tue")).with(labelStyle),
                cell(new JLabel("Wed")).with(labelStyle),
                cell(new JLabel("Thu")).with(labelStyle),
                cell(new JLabel("Fri")).with(labelStyle),
                cell(new JLabel("Sat")).with(labelStyle),
                cell(new JLabel("Sun")).with(labelStyle)
            ).getComponent());

            daysColumnPanel.add(new JSeparator());

            for (var i = 0; i < 6; i++) {
                var row = new RowPanel();

                for (var j = 0; j < 7; j++) {
                    var button = new JButton("00");

                    button.putClientProperty("JButton.buttonType", "toolBarButton");

                    button.setFocusable(false);
                    button.setPreferredSize(button.getPreferredSize());

                    row.add(button);
                }

                daysColumnPanel.add(row);
            }

            add(daysColumnPanel);

            setBorder(new EmptyBorder(4, 4, 4, 4));
        }
    }

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

    /**
     * Returns {@code true}.
     * {@inheritDoc}
     */
    @Override
    protected boolean isPopupEnabled() {
        return true;
    }

    /**
     * Returns a date picker popup component.
     * {@inheritDoc}
     */
    @Override
    protected JComponent getPopupComponent() {
        return new CalendarPanel();
    }
}
