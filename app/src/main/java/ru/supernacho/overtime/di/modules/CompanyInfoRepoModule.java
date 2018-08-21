package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ICompanyInfoRepository;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.CompanyInfoRepository;

@Singleton
@Module(includes = {CompanyRepoModule.class})
public class CompanyInfoRepoModule {
    @Provides
    ICompanyInfoRepository companyInfoRepository(ICompanyRepository companyRepository){
        return new CompanyInfoRepository(companyRepository);
    }
}
