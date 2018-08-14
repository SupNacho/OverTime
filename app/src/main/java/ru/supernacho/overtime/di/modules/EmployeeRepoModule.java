package ru.supernacho.overtime.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.IEmployeeRepository;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.firebase.FbEmployeeRepository;

@Singleton
@Module(includes = {UserCompaniesRepoModule.class, CompanyRepoModule.class})
public class EmployeeRepoModule {
    @Provides
    IEmployeeRepository employeeRepository(IUserCompanyRepository userCompanyRepository,
                                           ICompanyRepository companyRepository){
        return new FbEmployeeRepository(userCompanyRepository, companyRepository);
    }
}
