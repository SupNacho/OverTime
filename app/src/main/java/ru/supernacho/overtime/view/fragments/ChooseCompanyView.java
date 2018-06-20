package ru.supernacho.overtime.view.fragments;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = SingleStateStrategy.class)
public interface ChooseCompanyView extends MvpView {
    void updateAdapters();
}
