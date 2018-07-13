package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ChooseCompanyRepository;

@Singleton
@Module
public class ChooseCompanyRepoModule {
    @Provides
    ChooseCompanyRepository chooseCompanyRepository(){
        return new ChooseCompanyRepository();
    }
}
