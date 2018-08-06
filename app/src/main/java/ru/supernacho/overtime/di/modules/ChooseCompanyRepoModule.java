package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ChooseCompanyRepository;
import ru.supernacho.overtime.model.repository.CompanyRepository;
import ru.supernacho.overtime.model.repository.UserCompanyRepository;

@Singleton
@Module(includes = {UserCompaniesRepoModule.class, CompanyRepoModule.class})
public class ChooseCompanyRepoModule {
    @Provides
    ChooseCompanyRepository chooseCompanyRepository(UserCompanyRepository userCompanyRepository,
                                                    CompanyRepository companyRepository){
        return new ChooseCompanyRepository(userCompanyRepository, companyRepository);
    }
}
