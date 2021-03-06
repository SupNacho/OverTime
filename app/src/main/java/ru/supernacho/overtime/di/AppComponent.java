package ru.supernacho.overtime.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.supernacho.overtime.di.modules.AllEmplRepoModule;
import ru.supernacho.overtime.di.modules.AppModule;
import ru.supernacho.overtime.di.modules.ChartRepoModule;
import ru.supernacho.overtime.di.modules.ChooseCompanyRepoModule;
import ru.supernacho.overtime.di.modules.CompanyInfoRepoModule;
import ru.supernacho.overtime.di.modules.CompanyRepoModule;
import ru.supernacho.overtime.di.modules.EmployeeRepoModule;
import ru.supernacho.overtime.di.modules.LogRepoModule;
import ru.supernacho.overtime.di.modules.LoginRepoModule;
import ru.supernacho.overtime.di.modules.RestoreRepoModule;
import ru.supernacho.overtime.di.modules.TimerRepoModule;
import ru.supernacho.overtime.presenter.AllEmplPresenter;
import ru.supernacho.overtime.presenter.ChartPresenter;
import ru.supernacho.overtime.presenter.ChooseCompanyPresenter;
import ru.supernacho.overtime.presenter.CompanyInfoPresenter;
import ru.supernacho.overtime.presenter.CompanyRegistrationPresenter;
import ru.supernacho.overtime.presenter.DateChooserPresenter;
import ru.supernacho.overtime.presenter.EmployeesPresenter;
import ru.supernacho.overtime.presenter.LoginPresenter;
import ru.supernacho.overtime.presenter.ManageEmployeePresenter;
import ru.supernacho.overtime.presenter.ManagerPresenter;
import ru.supernacho.overtime.presenter.RestorePresenter;
import ru.supernacho.overtime.presenter.TabsPresenter;
import ru.supernacho.overtime.presenter.TimerPresenter;
import ru.supernacho.overtime.view.LoginActivity;

@Singleton
@Component(modules = {AppModule.class, LoginRepoModule.class, TimerRepoModule.class, LogRepoModule.class,
        ChartRepoModule.class, EmployeeRepoModule.class, CompanyRepoModule.class, CompanyInfoRepoModule.class,
        ChooseCompanyRepoModule.class, AllEmplRepoModule.class, RestoreRepoModule.class})
public interface AppComponent {
    void inject(LoginActivity activity);
    void inject(LoginPresenter presenter);
    void inject(TabsPresenter presenter);
    void inject(TimerPresenter presenter);
    void inject(DateChooserPresenter presenter);
    void inject(ChartPresenter presenter);
    void inject(ManagerPresenter presenter);
    void inject(EmployeesPresenter presenter);
    void inject(CompanyRegistrationPresenter presenter);
    void inject(CompanyInfoPresenter presenter);
    void inject(ChooseCompanyPresenter presenter);
    void inject(ManageEmployeePresenter presenter);
    void inject(AllEmplPresenter presenter);
    void inject(RestorePresenter presenter);
}
