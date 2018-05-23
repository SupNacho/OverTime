package ru.supernacho.overtime.model.Entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OverTimeEntity {
    private final Date startDate;
    private final Date stopDate;
    private final long duration;
    private final String comment;
    private final String durationString;
    private final String startDateLabel;
    private final String startDateTimeLabel;
    private final String stopDateTimeLabel;

    public OverTimeEntity(Date startDate, Date stopDate, long duration, String comment) {
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.duration = duration;
        this.comment = comment;
        this.durationString = new SimpleDateFormat("HH:mm:ss", Locale.US).format(duration);
        this.startDateLabel = new SimpleDateFormat("dd MMM", Locale.US).format(startDate);
        this.startDateTimeLabel = new SimpleDateFormat("dd MMM HH:mm", Locale.US).format(startDate);
        this.stopDateTimeLabel = new SimpleDateFormat("dd MMM HH:mm", Locale.US).format(stopDate);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }


    public long getDuration() {
        return duration;
    }

    public String getComment() {
        return comment;
    }

    public String getDurationString() {
        return durationString;
    }

    public String getStartDateLabel() {
        return startDateLabel;
    }

    public String getStartDateTimeLabel() {
        return startDateTimeLabel;
    }

    public String getStopDateTimeLabel() {
        return stopDateTimeLabel;
    }
}
