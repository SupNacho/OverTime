package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.firebase.core.FbCompanyRepository;
import ru.supernacho.overtime.model.repository.parseplatform.CompanyRepository;
import ru.supernacho.overtime.model.repository.parseplatform.UserCompanyRepository;

@Singleton
@Module(includes = {UserCompaniesRepoModule.class})
public class CompanyRepoModule {
    @Provides
    ICompanyRepository companyRepository(IUserCompanyRepository userCompanyRepository){
        return new FbCompanyRepository(userCompanyRepository);
    }
}
