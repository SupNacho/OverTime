package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.LogRepository;

@Singleton
@Module
public class LogRepoModule {

    @Provides
    LogRepository logRepository() {
        return new LogRepository();
    }
}
