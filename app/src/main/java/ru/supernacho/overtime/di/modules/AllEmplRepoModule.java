package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.IAllEmplRepository;
import ru.supernacho.overtime.model.repository.IOverTimeStatRepository;
import ru.supernacho.overtime.model.repository.AllEmplRepository;

@Singleton
@Module(includes = {OverTimeRepoModule.class})
public class AllEmplRepoModule {
    @Provides
    IAllEmplRepository allEmplRepository(IOverTimeStatRepository overTimeStatRepository){
        return new AllEmplRepository(overTimeStatRepository);
    }
}
