package ru.supernacho.overtime.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = SingleStateStrategy.class)
public interface TabsView extends MvpView {
    void setUserName(String userName);
    void logoutDone();
    void logoutFailed();
}
