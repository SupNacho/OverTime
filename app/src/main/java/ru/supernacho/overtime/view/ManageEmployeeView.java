package ru.supernacho.overtime.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.supernacho.overtime.model.Entity.CompanyEntity;

@StateStrategyType(value = SingleStateStrategy.class)
public interface ManageEmployeeView extends MvpView {
    void update();
    void setCompany(CompanyEntity companyEntity);
    void grantAdminFailed();
}
