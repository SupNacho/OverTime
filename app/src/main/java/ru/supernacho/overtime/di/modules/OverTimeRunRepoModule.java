package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.OverTimeRunRepository;

@Singleton
@Module
public class OverTimeRunRepoModule {
    @Provides
    OverTimeRunRepository overTimeRunRepository(){
        return new OverTimeRunRepository();
    }
}
