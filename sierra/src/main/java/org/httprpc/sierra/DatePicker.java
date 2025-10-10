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
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Text field that supports local date entry.
 */
public class DatePicker extends TemporalPicker {
    private class CalendarPanel extends ColumnPanel {
        private class DateButton extends JButton {
            LocalDate date = null;

            DateButton() {
                super("AA");

                putClientProperty("JButton.buttonType", "toolBarButton");

                setFocusable(false);
                setPreferredSize(getPreferredSize());
            }

            void update(LocalDate date) {
                this.date = date;

                setText(String.valueOf(date.getDayOfMonth()));
                setSelected(date.equals(DatePicker.this.date));
            }

            @Override
            protected void fireActionPerformed(ActionEvent event) {
                setDate(date);

                hidePopup();
            }
        }

        JSpinner monthSpinner;

        DateButton[][] dateButtons;

        CalendarPanel() {
            setSpacing(6);

            var zoneId = ZoneId.systemDefault();

            var value = Date.from(date.atStartOfDay(zoneId).toInstant());

            var start = (minimumDate == null) ? null : Date.from(minimumDate.withDayOfMonth(1).atStartOfDay(zoneId).toInstant());
            var end = (maximumDate == null) ? null : Date.from(maximumDate.atStartOfDay(zoneId).toInstant());

            monthSpinner = new JSpinner();

            monthSpinner.setModel(new SpinnerDateModel(value, start, end, Calendar.MONTH));
            monthSpinner.setFocusable(false);

            var dateEditor = new JSpinner.DateEditor(monthSpinner, "MMMM yyyy");

            dateEditor.setFocusable(false);

            monthSpinner.setEditor(dateEditor);

            monthSpinner.addChangeListener(event -> updateMonth());

            add(monthSpinner);

            add(createDateButtonPanel());

            setBorder(new EmptyBorder(4, 4, 4, 4));

            updateMonth();
        }

        private ColumnPanel createDateButtonPanel() {
            var daysColumnPanel = new ColumnPanel();

            daysColumnPanel.setAlignToGrid(true);

            var dayOfWeekRow = new RowPanel();

            dayOfWeekRow.setSpacing(2);

            var locale = Locale.getDefault();

            var dayOfWeek = WeekFields.of(locale).getFirstDayOfWeek();

            for (var i = 0; i < 7; i++) {
                var label = new JLabel(dayOfWeek.getDisplayName(TextStyle.SHORT, locale));

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setAlignmentX(0.5f);
                label.putClientProperty("FlatLaf.styleClass", "small");

                dayOfWeekRow.add(label);

                dayOfWeek = DayOfWeek.of(dayOfWeek.getValue() % 7 + 1);
            }

            daysColumnPanel.add(dayOfWeekRow);

            daysColumnPanel.add(new JSeparator());

            dateButtons = new DateButton[6][];

            for (var i = 0; i < 6; i++) {
                var row = new RowPanel();

                dateButtons[i] = new DateButton[7];

                for (var j = 0; j < 7; j++) {
                    var button = new DateButton();

                    row.add(button);

                    dateButtons[i][j] = button;
                }

                daysColumnPanel.add(row);
            }

            return daysColumnPanel;
        }

        void updateMonth() {
            var month = (Date)monthSpinner.getValue();

            var firstOfMonth = LocalDate.ofInstant(month.toInstant(), ZoneId.systemDefault()).withDayOfMonth(1);
            var firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();

            var index = Math.floorMod(firstOfMonth.getDayOfWeek().ordinal() - firstDayOfWeek.ordinal(), 7);

            var date = firstOfMonth.minusDays(index);

            for (var i = 0; i < 6; i++) {
                for (var j = 0; j < 7; j++) {
                    var button = dateButtons[i][j];

                    button.update(date);
                    button.setEnabled(date.getMonth() == firstOfMonth.getMonth()
                        && date.getYear() == firstOfMonth.getYear()
                        && DatePicker.this.validate(date));

                    date = date.plusDays(1);
                }
            }
        }
    }

    private LocalDate date;

    private LocalDate minimumDate = null;
    private LocalDate maximumDate = null;

    private static final String pattern;
    private static final DateTimeFormatter dateFormatter;
    static {
        var locale = Locale.getDefault();

        pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, Chronology.ofLocale(locale), locale);
        dateFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * Constructs a new date picker.
     */
    public DatePicker() {
        super(6);

        setDate(LocalDate.now());

        setInputVerifier(new InputVerifier() {
            LocalDate date = null;

            @Override
            public boolean verify(JComponent input) {
                if (input != DatePicker.this) {
                    throw new IllegalArgumentException();
                }

                try {
                    date = LocalDate.parse(getText(), dateFormatter);

                    return true;
                } catch (DateTimeParseException exception) {
                    return false;
                }
            }

            @Override
            public boolean shouldYieldFocus(JComponent source, JComponent target) {
                if (verify(source)) {
                    if (validate(date)) {
                        if (!date.equals(DatePicker.this.date)) {
                            DatePicker.this.date = date;

                            fireStateChanged();
                        }
                    } else {
                        setText(dateFormatter.format(DatePicker.this.date));
                    }

                    date = null;

                    return true;
                } else {
                    UIManager.getLookAndFeel().provideErrorFeedback(source);

                    return false;
                }
            }
        });

        putClientProperty("JTextField.placeholderText", pattern);
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
        if (date == null || !validate(date)) {
            throw new IllegalArgumentException();
        }

        if (!date.equals(this.date)) {
            setText(dateFormatter.format(date));

            this.date = date;

            fireStateChanged();
        }
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
                throw new IllegalArgumentException();
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
                throw new IllegalArgumentException();
            }

            if (date != null && date.isAfter(maximumDate)) {
                setDate(maximumDate);
            }
        }

        this.maximumDate = maximumDate;
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
