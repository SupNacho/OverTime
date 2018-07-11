package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.CompanyInfoRepository;
import ru.supernacho.overtime.model.repository.EmployeeRepository;

@Singleton
@Module(includes = {CompanyInfoRepoModule.class})
public class EmployeeRepoModule {
    @Provides
    EmployeeRepository employeeRepository(CompanyInfoRepository companyInfoRepository){
        return new EmployeeRepository(companyInfoRepository);
    }
}
