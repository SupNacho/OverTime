package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.AllEmplRepository;
import ru.supernacho.overtime.model.repository.OverTimeStatRepository;

@Singleton
@Module(includes = {OverTimeRepoModule.class})
public class AllEmplRepoModule {
    @Provides
    AllEmplRepository allEmplRepository(OverTimeStatRepository overTimeStatRepository){
        return new AllEmplRepository(overTimeStatRepository);
    }
}
