package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.UserCompanyRepository;

@Singleton
@Module
public class UserCompaniesRepoModule {
    @Provides
    UserCompanyRepository userCompanyRepository(){
        return new UserCompanyRepository();
    }
}
