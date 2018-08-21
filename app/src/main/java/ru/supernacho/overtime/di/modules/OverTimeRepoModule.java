package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.IOverTimeStatRepository;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.firebase.core.FbOverTimeStatRepository;

@Singleton
@Module(includes = {UserCompaniesRepoModule.class, CompanyRepoModule.class})
public class OverTimeRepoModule {
    @Provides
    IOverTimeStatRepository overTimeRepository(IUserCompanyRepository userCompanyRepository,
                                               ICompanyRepository companyRepository){
        return new FbOverTimeStatRepository(userCompanyRepository, companyRepository);
    }
}
