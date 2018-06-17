package ru.supernacho.overtime.view.fragments;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.supernacho.overtime.model.Entity.User;

@StateStrategyType(value = AddToEndStrategy.class)
public interface ManagerView extends MvpView {
    void callEmployeesChooser();
    void openDateFragment(String userId);
    void startChartFragment(int month, int year);
}
