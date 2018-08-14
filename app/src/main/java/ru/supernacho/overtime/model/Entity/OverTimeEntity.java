package ru.supernacho.overtime.model.Entity;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.Date;

import ru.supernacho.overtime.utils.charts.DurationToStringConverter;

public class OverTimeEntity {
    private Date startDate;
    private Date stopDate;
    private long duration;
    private String comment;
    private String durationString;
    private String startDateLabel;
    private String startDateTimeLabel;
    private String stopDateTimeLabel;
    private CompanyEntity company;

    public OverTimeEntity() {
    }

    public OverTimeEntity(Date startDate, Date stopDate, String timeZoneID, long duration,
                          String comment, CompanyEntity company) {
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.duration = duration;
        this.comment = comment;
        this.company = company;
        DateTime sdt = new DateTime(startDate);
        DateTime fdt = new DateTime(stopDate);
        sdt.withZone(DateTimeZone.forID(timeZoneID));
        fdt.withZone(DateTimeZone.forID(timeZoneID));
        DateTimeFormatter startDateLabelFormatter = DateTimeFormat.forPattern("dd MMM");
        DateTimeFormatter startDateTimeLabelFormatter = DateTimeFormat.forPattern("dd MMM HH:mm");
        DateTimeFormatter stopDateTimeLabelFormatter = DateTimeFormat.forPattern("dd MMM HH:mm");
        this.durationString = DurationToStringConverter.convert(duration);
        this.startDateLabel = startDateLabelFormatter.print(sdt);
        this.startDateTimeLabel = startDateTimeLabelFormatter.print(sdt);
        this.stopDateTimeLabel = stopDateTimeLabelFormatter.print(fdt);
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

    public CompanyEntity getCompany() {
        return company;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    public void setStartDateLabel(String startDateLabel) {
        this.startDateLabel = startDateLabel;
    }

    public void setStartDateTimeLabel(String startDateTimeLabel) {
        this.startDateTimeLabel = startDateTimeLabel;
    }

    public void setStopDateTimeLabel(String stopDateTimeLabel) {
        this.stopDateTimeLabel = stopDateTimeLabel;
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
    }
}
