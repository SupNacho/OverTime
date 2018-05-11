package ru.supernacho.overtime.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.supernacho.overtime.di.modules.AppModule;
import ru.supernacho.overtime.di.modules.LoginRepoModule;
import ru.supernacho.overtime.di.modules.TimerRepoModule;
import ru.supernacho.overtime.model.repository.LoginRepository;
import ru.supernacho.overtime.model.repository.TimerRepository;
import ru.supernacho.overtime.presenter.LoginPresenter;
import ru.supernacho.overtime.presenter.TabsPresenter;
import ru.supernacho.overtime.presenter.TimerPresenter;
import ru.supernacho.overtime.view.LoginActivity;

@Singleton
@Component(modules = {AppModule.class, LoginRepoModule.class, TimerRepoModule.class})
public interface AppComponent {
    void inject(LoginActivity activity);
    void inject(LoginPresenter presenter);
    void inject(TabsPresenter presenter);
    void inject(TimerPresenter presenter);
}
