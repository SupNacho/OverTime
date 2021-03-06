package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.IOverTimeRunRepository;
import ru.supernacho.overtime.model.repository.TimerRepository;

@Singleton
@Module(includes = {OverTimeRunRepoModule.class})
public class TimerRepoModule {

    @Provides
    TimerRepository timerRepository(IOverTimeRunRepository overTimeRunRepository){
        return new TimerRepository(overTimeRunRepository);
    }
}
