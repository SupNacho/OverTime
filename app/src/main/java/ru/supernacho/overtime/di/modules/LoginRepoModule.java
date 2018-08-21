package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.ILoginRepository;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.firebase.FbLoginRepository;

@Singleton
@Module(includes = {UserCompaniesRepoModule.class, CompanyRepoModule.class})
public class LoginRepoModule {

    @Provides
    ILoginRepository loginRepository(IUserCompanyRepository userCompanyRepository,
                                     ICompanyRepository companyRepository) {
        return new FbLoginRepository(userCompanyRepository, companyRepository);
    }
}
