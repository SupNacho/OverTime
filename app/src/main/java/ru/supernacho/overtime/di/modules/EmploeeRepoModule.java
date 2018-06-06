package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.EmployeeRepository;

@Singleton
@Module
public class EmploeeRepoModule {
    @Provides
    EmployeeRepository employeeRepository(){
        return new EmployeeRepository();
    }
}
