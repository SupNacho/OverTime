package ru.supernacho.overtime.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = SingleStateStrategy.class)
public interface ChooseCompanyView extends MvpView {
    void updateAdapters();
    void activationSuccess();
    void activationFail();
    void deactivationSuccess();
    void deactivationFail();
    void joinFail();
    void joinSuccess();
    void exitError();
    void initExitFromCompany(String companyId);

}
