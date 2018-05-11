package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.TimerRepository;

@Singleton
@Module
public class TimerRepoModule {

    @Provides
    TimerRepository timerRepository(){
        return new TimerRepository();
    }
}
