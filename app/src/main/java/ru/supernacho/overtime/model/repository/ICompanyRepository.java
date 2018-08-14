package ru.supernacho.overtime.model.repository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.parse.ParseObject;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompany;

public interface ICompanyRepository {

    Observable<UserCompany> registerCompany(String name, String address, String email, String phone, String chief);

    Observable<UserCompany> userIsAdmin();

    CompanyEntity getCompanyById(String id);

    CompanyEntity getCompanyByPin(String pin);

    Observable<CompanyEntity> getCurrentCompany();

    CompanyEntity getCompanyByOverTime(DocumentSnapshot overTime);

    CompanyEntity getCompanyAdmins(DocumentSnapshot employee, String companyId);

    Observable<Boolean> setAdminStatus(User user, CompanyEntity currentCompany);

    void addCompanyToUser(String companyId);
}
