package ru.supernacho.overtime.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

public class ChartValuesFormatter implements IAxisValueFormatter {
    private List<String> list;

    public ChartValuesFormatter(List<String> list) {
        this.list = list;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return list.get((int)value);
    }
}
