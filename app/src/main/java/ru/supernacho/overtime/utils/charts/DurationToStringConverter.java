package ru.supernacho.overtime.utils.charts;

import java.util.Locale;

public class DurationToStringConverter {
    public static String convert(long duration){
        long hours = duration / 3600000;
        long minutes = (duration % 3600000) / 60000;
        long secs = (duration /1000) % 60;
        return String.format(Locale.US,"%d:%02d:%02d",
                hours, minutes, secs);
    }
}
