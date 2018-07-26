package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.CompanyRepository;
import ru.supernacho.overtime.model.repository.UserCompanyRepository;

@Singleton
@Module(includes = {UserCompaniesRepoModule.class})
public class CompanyRepoModule {
    @Provides
    CompanyRepository companyRepository(UserCompanyRepository userCompanyRepository){
        return new CompanyRepository(userCompanyRepository);
    }
}
