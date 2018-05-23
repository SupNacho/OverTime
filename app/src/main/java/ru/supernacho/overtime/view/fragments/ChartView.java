package ru.supernacho.overtime.view.fragments;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.supernacho.overtime.model.Entity.OverTimeEntity;

@StateStrategyType( value = AddToEndStrategy.class)
public interface ChartView extends MvpView {
    void updateChartView(List<OverTimeEntity> overTimeEntityList);
    void shareReport(String report);
}
