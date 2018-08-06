package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.CompanyRepository;
import ru.supernacho.overtime.model.repository.OverTimeStatRepository;
import ru.supernacho.overtime.model.repository.UserCompanyRepository;

@Singleton
@Module(includes = {UserCompaniesRepoModule.class, CompanyRepoModule.class})
public class OverTimeRepoModule {
    @Provides
    OverTimeStatRepository overTimeRepository(UserCompanyRepository userCompanyRepository,
                                              CompanyRepository companyRepository){
        return new OverTimeStatRepository(userCompanyRepository, companyRepository);
    }
}
