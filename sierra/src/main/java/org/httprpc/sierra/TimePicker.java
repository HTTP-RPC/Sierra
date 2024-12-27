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
import javax.swing.event.ListDataListener;
import java.awt.Component;
import java.time.LocalTime;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Text field that supports local time entry.
 */
public class TimePicker extends Picker {
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
        public void addListDataListener(ListDataListener l) {
            // No-op
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
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

    private int minuteInterval;

    private LocalTime time;

    private LocalTime minimumTime = null;
    private LocalTime maximumTime = null;

    private final InputVerifier inputVerifier = new InputVerifier() {
        @Override
        public boolean verify(JComponent input) {
            try {
                var time = LocalTime.parse(getText(), timeFormatter);

                if (time.equals(TimePicker.this.time)) {
                    return true;
                }

                if (!validate(time)) {
                    selectAll();

                    return false;
                }

                TimePicker.this.time = time;

                TimePicker.super.fireActionPerformed();
            } catch (DateTimeParseException exception) {
                setText(timeFormatter.format(time));
            }

            return true;
        }
    };

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
        this(1);
    }

    /**
     * Constructs a new time picker.
     *
     * @param minuteInterval
     * The minute interval. Must be a value that evenly divides into 60.
     */
    public TimePicker(int minuteInterval) {
        super(6);

        setMinuteInterval(minuteInterval);

        setInputVerifier(inputVerifier);

        putClientProperty("JTextField.placeholderText", pattern);
    }

    /**
     * Returns the minute interval.
     *
     * @return
     * The minute interval.
     */
    public int getMinuteInterval() {
        return minuteInterval;
    }

    /**
     * Sets the minute interval.
     *
     * @param minuteInterval
     * The minute interval.
     */
    public void setMinuteInterval(int minuteInterval) {
        if (60 % minuteInterval != 0) {
            throw new IllegalArgumentException();
        }

        this.minuteInterval = minuteInterval;

        var now = LocalTime.now();

        setTime(LocalTime.of(now.getHour(), (now.getMinute() / minuteInterval) * minuteInterval));
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

        setText(timeFormatter.format(time));

        this.time = truncate(time);
    }

    private boolean validate(LocalTime time) {
        return (time.getMinute() % minuteInterval == 0
            && (minimumTime == null || !time.isBefore(minimumTime))
            && (maximumTime == null || !time.isAfter(maximumTime)));
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
                throw new IllegalStateException();
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
                throw new IllegalStateException();
            }

            if (time != null && time.isAfter(maximumTime)) {
                setTime(maximumTime);
            }
        }

        this.maximumTime = truncate(maximumTime);
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
     * Returns {@code true} if the minute interval is 15 minutes or greater;
     * {@code false}, otherwise.
     * {@inheritDoc}
     */
    @Override
    protected boolean isPopupEnabled() {
        return minuteInterval >= 15;
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

        list.setSelectedValue(time, true);

        list.addListSelectionListener(event -> {
            setTime(list.getSelectedValue());

            super.fireActionPerformed();

            hidePopup();
        });

        return scrollPane;
    }

    private LocalTime getTimeAt(int index) {
        var minutes = index * minuteInterval;

        return LocalTime.of(minutes / 60, minutes % 60);
    }

    private static LocalTime truncate(LocalTime time) {
        return time.withSecond(0).withNano(0);
    }
}
