package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.IRestoreRepository;
import ru.supernacho.overtime.model.repository.firebase.FbRestoreRepository;

@Singleton
@Module
public class RestoreRepoModule {
    @Provides
    IRestoreRepository restoreRepository(){
        return new FbRestoreRepository();
    }
}
