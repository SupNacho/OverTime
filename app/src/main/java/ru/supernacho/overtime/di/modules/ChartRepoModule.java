package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ChartRepository;
import ru.supernacho.overtime.model.repository.OverTimeStatRepository;

@Singleton
@Module(includes = {OverTimeRepoModule.class})
public class ChartRepoModule {
    @Provides
    ChartRepository chartRepository(OverTimeStatRepository overTimeStatRepository){
        return new ChartRepository(overTimeStatRepository);
    }
}
