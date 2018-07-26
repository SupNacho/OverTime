package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.LogRepository;
import ru.supernacho.overtime.model.repository.OverTimeStatRepository;

@Singleton
@Module(includes = {OverTimeRepoModule.class})
public class LogRepoModule {

    @Provides
    LogRepository logRepository(OverTimeStatRepository overTimeStatRepository) {
        return new LogRepository(overTimeStatRepository);
    }
}
