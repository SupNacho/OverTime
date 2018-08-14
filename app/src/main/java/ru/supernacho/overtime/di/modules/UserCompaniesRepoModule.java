package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.firebase.core.FbUserCompanyRepository;
import ru.supernacho.overtime.model.repository.parseplatform.UserCompanyRepository;

@Singleton
@Module
public class UserCompaniesRepoModule {
    @Provides
    IUserCompanyRepository userCompanyRepository(){
        return new FbUserCompanyRepository();
    }
}
