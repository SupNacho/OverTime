package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.RestoreRepository;

@Singleton
@Module
public class RestoreRepoModule {
    @Provides
    RestoreRepository restoreRepository(){
        return new RestoreRepository();
    }
}
