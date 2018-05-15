package ru.supernacho.overtime.model.Entity;

public class DateChooserEntry {
    private final int month;
    private final int year;

    public DateChooserEntry(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
