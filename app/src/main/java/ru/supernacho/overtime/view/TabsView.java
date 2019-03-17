package ru.supernacho.overtime.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.supernacho.overtime.model.Entity.CompanyEntity;

@StateStrategyType(value = SingleStateStrategy.class)
public interface TabsView extends MvpView {
    void setUserName(String userName);
    void setAdmin(boolean isAdmin);
    void logoutDone();
    void logoutFailed();
    void setCompanyId(String companyId);
    void setCompany(CompanyEntity company);

    void startEmployeeManager();
    void startCompanyChooser();
    void startCompanyRegistration();
    void openCompanyInfo();

    void showPolicy();
}
