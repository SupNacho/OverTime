package ru.supernacho.overtime.model.Entity;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateChooserEntry)) return false;
        DateChooserEntry that = (DateChooserEntry) o;
        return getMonth() == that.getMonth() &&
                getYear() == that.getYear();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMonth(), getYear());
    }
}
