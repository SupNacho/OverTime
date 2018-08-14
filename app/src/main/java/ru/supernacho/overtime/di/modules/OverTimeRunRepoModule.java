package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.IOverTimeRunRepository;
import ru.supernacho.overtime.model.repository.firebase.core.FbOverTimeRunRepository;
import ru.supernacho.overtime.model.repository.parseplatform.OverTimeRunRepository;

@Singleton
@Module
public class OverTimeRunRepoModule {
    @Provides
    IOverTimeRunRepository overTimeRunRepository(){
        return new FbOverTimeRunRepository();
    }
}
