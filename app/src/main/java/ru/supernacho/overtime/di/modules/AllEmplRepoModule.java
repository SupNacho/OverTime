package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.AllEmplRepository;

@Singleton
@Module
public class AllEmplRepoModule {
    @Provides
    AllEmplRepository allEmplRepository(){
        return new AllEmplRepository();
    }
}
