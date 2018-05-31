package ru.supernacho.overtime.view.fragments;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndStrategy.class)
public interface LogsView extends MvpView{
    void callDateChooser();
    void openChartFragment(int month, int year);
}
