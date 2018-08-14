package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ILogRepository;
import ru.supernacho.overtime.model.repository.IOverTimeStatRepository;
import ru.supernacho.overtime.model.repository.LogRepository;

@Singleton
@Module(includes = {OverTimeRepoModule.class})
public class LogRepoModule {

    @Provides
    ILogRepository logRepository(IOverTimeStatRepository overTimeStatRepository) {
        return new LogRepository(overTimeStatRepository);
    }
}
