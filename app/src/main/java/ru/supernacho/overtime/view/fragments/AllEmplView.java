package ru.supernacho.overtime.view.fragments;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.supernacho.overtime.model.Entity.UserCompanyStat;

@StateStrategyType(value = SingleStateStrategy.class)
public interface AllEmplView extends MvpView {
    void updateChartView(List<UserCompanyStat> stats);
}
