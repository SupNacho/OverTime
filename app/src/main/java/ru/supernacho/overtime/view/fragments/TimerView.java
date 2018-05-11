package ru.supernacho.overtime.view.fragments;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface TimerView extends MvpView {
    void setCounter(String countMsg);
    void setStartDate(String startDate);
    void setTimerState(boolean isStarted);
}
