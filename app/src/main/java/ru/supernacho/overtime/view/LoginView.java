package ru.supernacho.overtime.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = SingleStateStrategy.class)
public interface LoginView extends MvpView {
    void loginError(String msg);
    void loginSuccess();
    void registrationSuccess();
    void registrationFailed();
}
