package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.CompanyInfoRepository;
import ru.supernacho.overtime.model.repository.CompanyRepository;

@Singleton
@Module(includes = {CompanyRepoModule.class})
public class CompanyInfoRepoModule {
    @Provides
    CompanyInfoRepository companyInfoRepository(CompanyRepository companyRepository){
        return new CompanyInfoRepository(companyRepository);
    }
}
