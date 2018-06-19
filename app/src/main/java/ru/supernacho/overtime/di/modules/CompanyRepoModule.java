package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.CompanyRepository;

@Singleton
@Module
public class CompanyRepoModule {
    @Provides
    CompanyRepository companyRepository(){
        return new CompanyRepository();
    }
}
