package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.CompanyRepository;
import ru.supernacho.overtime.model.repository.LoginRepository;
import ru.supernacho.overtime.model.repository.UserCompanyRepository;

@Singleton
@Module(includes = {UserCompaniesRepoModule.class, CompanyRepoModule.class})
public class LoginRepoModule {

    @Provides
    LoginRepository loginRepository(UserCompanyRepository userCompanyRepository,
                                    CompanyRepository companyRepository) {
        return new LoginRepository(userCompanyRepository, companyRepository);
    }
}
