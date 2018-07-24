package ru.supernacho.overtime.utils.charts;

import android.support.v4.app.Fragment;

import java.util.Objects;

import ru.supernacho.overtime.view.fragments.LogsFragment;
import ru.supernacho.overtime.view.fragments.ManagerFragment;

public class ChartChooser {
    public static void startChart(Fragment fragment, int month, int year, boolean isAllEmployeesStat){
        if (fragment instanceof LogsFragment) {
            ((LogsFragment) Objects.requireNonNull(fragment)).startChartFragment(month, year);
        } else if (fragment instanceof ManagerFragment && !isAllEmployeesStat) {
            ((ManagerFragment) Objects.requireNonNull(fragment)).startChartFragment(month, year);
        } else if (fragment instanceof ManagerFragment) {
            ((ManagerFragment) Objects.requireNonNull(fragment)).startAllStatChartFragment(month, year);
        }
    }
}
