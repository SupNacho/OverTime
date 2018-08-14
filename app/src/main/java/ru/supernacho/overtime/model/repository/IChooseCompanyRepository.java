package ru.supernacho.overtime.model.repository;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;

public interface IChooseCompanyRepository {
    Observable<List<CompanyEntity>> getCompanies();

    Observable<Boolean> setActiveCompany(String companyId);

    Observable<Boolean> deactivateCompany();

    Observable<Boolean> joinCompany(String pin);

    Observable<Boolean> exitFromCompany(String companyId);
}
