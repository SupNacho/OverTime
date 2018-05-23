package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ChartRepository;

@Singleton
@Module
public class ChartRepoModule {
    @Provides
    ChartRepository chartRepository(){
        return new ChartRepository();
    }
}
