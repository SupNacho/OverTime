package ru.supernacho.overtime.model.repository;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;

public interface IEmployeeRepository {

    List<User> getEmployeesList();

    Observable<Boolean> getEmployees();

    Observable<CompanyEntity> getCompany();

    Observable<Boolean> setAdminStatus(User user);

    Observable<Boolean> fireEmployee(User employee);
}
