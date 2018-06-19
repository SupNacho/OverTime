package ru.supernacho.overtime.view.fragments;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = SingleStateStrategy.class)
public interface CompanyInfoView extends MvpView {
    void setName(String name);
    void setEmail(String email);
    void setPhone(String phone);
    void setAddress(String address);
    void setPin(String pin);
    void setCEO(String ceo);
}
