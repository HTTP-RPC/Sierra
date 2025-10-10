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

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListDataListener;
import java.awt.Component;
import java.time.LocalTime;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Text field that supports local time entry.
 */
public class TimePicker extends TemporalPicker {
    private class TimePickerListModel implements ListModel<LocalTime> {
        @Override
        public int getSize() {
            return 24 * (60 / minuteInterval);
        }

        @Override
        public LocalTime getElementAt(int index) {
            return getTimeAt(index);
        }

        @Override
        public void addListDataListener(ListDataListener listener) {
            // No-op
        }

        @Override
        public void removeListDataListener(ListDataListener listener) {
            // No-op
        }
    }

    private class TimePickerListSelectionModel extends DefaultListSelectionModel {
        TimePickerListSelectionModel() {
            setSelectionMode(SINGLE_SELECTION);
        }

        @Override
        public void setSelectionInterval(int index0, int index1) {
            if ((minimumTime != null && getTimeAt(index0).isBefore(minimumTime))
                || (maximumTime != null && getTimeAt(index1).isAfter(maximumTime))) {
                return;
            }

            super.setSelectionInterval(index0, index1);
        }

        @Override
        public void removeSelectionInterval(int index0, int index1) {
            // No-op
        }
    }

    private class TimePickerListCellRenderer extends DefaultListCellRenderer {
        TimePickerListCellRenderer() {
            setHorizontalAlignment(SwingConstants.TRAILING);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean cellHasFocus) {
            var time = (LocalTime)value;

            super.getListCellRendererComponent(list, timeFormatter.format(time), index, selected, cellHasFocus);

            if ((minimumTime == null || !time.isBefore(minimumTime))
                && (maximumTime == null || !time.isAfter(maximumTime))) {
                setForeground(list.getSelectionForeground());
            } else {
                setForeground(getDisabledTextColor());
            }

            return this;
        }
    }

    private LocalTime time;

    private int minuteInterval = 1;
    private boolean strict = false;

    private LocalTime minimumTime = null;
    private LocalTime maximumTime = null;

    private static final String pattern;
    private static final DateTimeFormatter timeFormatter;
    static {
        var locale = Locale.getDefault();

        pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.SHORT, Chronology.ofLocale(locale), locale);
        timeFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * Constructs a new time picker.
     */
    public TimePicker() {
        setTime(LocalTime.now());

        setInputVerifier(new InputVerifier() {
            LocalTime time = null;

            @Override
            public boolean verify(JComponent input) {
                if (input != TimePicker.this) {
                    throw new IllegalArgumentException();
                }

                try {
                    time = LocalTime.parse(getText(), timeFormatter);

                    return true;
                } catch (DateTimeParseException exception) {
                    return false;
                }
            }

            @Override
            public boolean shouldYieldFocus(JComponent source, JComponent target) {
                if (verify(source)) {
                    if (validate(time)) {
                        if (!time.equals(TimePicker.this.time)) {
                            TimePicker.this.time = time;

                            fireStateChanged();
                        }
                    }

                    applyValue();

                    time = null;

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
     * Applies the current time value.
     * {@inheritDoc}
     */
    @Override
    protected void applyValue() {
        setText(timeFormatter.format(TimePicker.this.time));
    }

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
        if (time == null || !validate(time)) {
            throw new IllegalArgumentException();
        }

        if (!time.equals(this.time)) {
            this.time = truncate(time);

            applyValue();

            fireStateChanged();
        }
    }

    private boolean validate(LocalTime time) {
        return ((minuteInterval == 1 || !strict || time.getMinute() % minuteInterval == 0)
            && (minimumTime == null || !time.isBefore(minimumTime))
            && (maximumTime == null || !time.isAfter(maximumTime)));
    }

    /**
     * Returns the minute interval. The default value is 1.
     *
     * @return
     * The minute interval.
     */
    public int getMinuteInterval() {
        return minuteInterval;
    }

    /**
     * Sets the minute interval. The value must be between 1 and 30 and must
     * divide evently into 60. If strict mode is enabled, input will be limited
     * to this interval.
     *
     * @param minuteInterval
     * The minute interval.
     */
    public void setMinuteInterval(int minuteInterval) {
        if (minuteInterval <= 0 || minuteInterval > 30 || 60 % minuteInterval != 0) {
            throw new IllegalArgumentException();
        }

        this.minuteInterval = minuteInterval;

        adjustTime();
    }

    private LocalTime getTimeAt(int index) {
        var minutes = index * minuteInterval;

        return LocalTime.of(minutes / 60, minutes % 60);
    }

    /**
     * Indicates that strict mode is enabled. The default value is
     * {@code false}.
     *
     * @return
     * {@code true} if strict mode is enabled; {@code false}, otherwise.
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Toggles strict mode.
     *
     * @param strict
     * {@code true} to enable strict mode; {@code false} to disable it.
     */
    public void setStrict(boolean strict) {
        this.strict = strict;

        adjustTime();
    }

    private void adjustTime() {
        if (minuteInterval > 1 && strict) {
            setTime(LocalTime.of(time.getHour(), (time.getMinute() / minuteInterval) * minuteInterval));
        }
    }

    /**
     * Returns the minimum value allowed by this time picker.
     *
     * @return
     * The minimum time, or {@code null} if no minimum time is set.
     */
    public LocalTime getMinimumTime() {
        return minimumTime;
    }

    /**
     * Sets the minimum value allowed by this time picker.
     *
     * @param minimumTime
     * The minimum time, or {@code null} for no minimum time.
     */
    public void setMinimumTime(LocalTime minimumTime) {
        if (minimumTime != null) {
            if (maximumTime != null && minimumTime.isAfter(maximumTime)) {
                throw new IllegalArgumentException();
            }

            if (time != null && time.isBefore(minimumTime)) {
                setTime(minimumTime);
            }
        }

        this.minimumTime = truncate(minimumTime);
    }

    /**
     * Returns the maximum value allowed by this time picker.
     *
     * @return
     * The maximum time, or {@code null} if no maximum time is set.
     */
    public LocalTime getMaximumTime() {
        return maximumTime;
    }

    /**
     * Sets the maximum value allowed by this time picker.
     *
     * @param maximumTime
     * The maximum time, or {@code null} for no maximum time.
     */
    public void setMaximumTime(LocalTime maximumTime) {
        if (maximumTime != null) {
            if (minimumTime != null && maximumTime.isBefore(minimumTime)) {
                throw new IllegalArgumentException();
            }

            if (time != null && time.isAfter(maximumTime)) {
                setTime(maximumTime);
            }
        }

        this.maximumTime = truncate(maximumTime);
    }

    /**
     * Returns {@code true} if the minute interval is greater than 1;
     * {@code false}, otherwise.
     * {@inheritDoc}
     */
    @Override
    protected boolean isPopupEnabled() {
        return minuteInterval > 1;
    }

    /**
     * Returns a time picker popup component.
     * {@inheritDoc}
     */
    @Override
    protected JComponent getPopupComponent() {
        var list = new JList<LocalTime>();

        list.setModel(new TimePicker.TimePickerListModel());
        list.setSelectionModel(new TimePicker.TimePickerListSelectionModel());
        list.setCellRenderer(new TimePicker.TimePickerListCellRenderer());
        list.setVisibleRowCount(8);
        list.setFocusable(false);

        var scrollPane = new JScrollPane(list);

        scrollPane.setBorder(null);

        if (minuteInterval > 1 && time.getMinute() % minuteInterval == 0) {
            list.setSelectedValue(time, true);
        }

        list.addListSelectionListener(event -> {
            setTime(list.getSelectedValue());

            hidePopup();
        });

        return scrollPane;
    }

    private static LocalTime truncate(LocalTime time) {
        return time.truncatedTo(ChronoUnit.MINUTES);
    }
}
