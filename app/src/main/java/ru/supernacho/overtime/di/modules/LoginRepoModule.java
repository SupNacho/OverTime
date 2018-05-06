package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.LoginRepository;

@Singleton
@Module
public class LoginRepoModule {

    @Provides
    LoginRepository loginRepository() {
        return new LoginRepository();
    }
}
