package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ChooseCompanyRepository;
import ru.supernacho.overtime.model.repository.IChooseCompanyRepository;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.firebase.FbChooseCompanyRepository;

@Singleton
@Module(includes = {UserCompaniesRepoModule.class, CompanyRepoModule.class})
public class ChooseCompanyRepoModule {
    @Provides
    IChooseCompanyRepository chooseCompanyRepository(IUserCompanyRepository userCompanyRepository,
                                                     ICompanyRepository companyRepository){
        return new ChooseCompanyRepository(userCompanyRepository, companyRepository);
    }
}
