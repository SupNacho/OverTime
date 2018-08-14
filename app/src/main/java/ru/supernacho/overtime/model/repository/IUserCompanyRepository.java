package ru.supernacho.overtime.model.repository;

import com.google.firebase.firestore.Query;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompaniesEntity;

public interface IUserCompanyRepository {
    UserCompaniesEntity getCurrentUserCompanies();

    UserCompaniesEntity getUserCompanies(User employee);

    Observable<Boolean> addCompanyToUser(CompanyEntity company);

    Observable<Boolean> addCompanyToUser(String companyId);

    Observable<Boolean> exitFromCompany(String companyId);

    void createUserCompaniesEntity();

    Observable<Boolean> setActiveCompany(String companyId);

    Observable<Boolean> deactivateCompany();

    List<String> getUserCompaniesArray();

    String getActiveCompanyId();

    Query getUserCompaniesQuery();
}
