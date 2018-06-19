package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.CompanyInfoRepository;

@Singleton
@Module
public class CompanyInfoRepoModule {
    @Provides
    CompanyInfoRepository companyInfoRepository(){
        return new CompanyInfoRepository();
    }
}
