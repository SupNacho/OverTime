package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ChartRepository;
import ru.supernacho.overtime.model.repository.IChartRepository;
import ru.supernacho.overtime.model.repository.IOverTimeStatRepository;

@Singleton
@Module(includes = {OverTimeRepoModule.class})
public class ChartRepoModule {
    @Provides
    IChartRepository chartRepository(IOverTimeStatRepository overTimeStatRepository){
        return new ChartRepository(overTimeStatRepository);
    }
}
