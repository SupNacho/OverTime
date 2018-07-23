package ru.supernacho.overtime.utils.charts;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

import ru.supernacho.overtime.model.Entity.UserCompanyStat;

public class PieChartValueFormatter implements IValueFormatter {
    private List<UserCompanyStat> stats;

    public PieChartValueFormatter(List<UserCompanyStat> stats) {
        this.stats = stats;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return stats.get((int) value).getUser().getFullName();
    }
}
