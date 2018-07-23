package ru.supernacho.overtime.utils.charts;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

import ru.supernacho.overtime.model.Entity.UserCompanyStat;

public class PieChartValueFormatter implements IValueFormatter {
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return DurationToStringConverter.convert((long) value);
    }
}
