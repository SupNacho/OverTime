package ru.supernacho.overtime.model.repository;

import com.google.firebase.auth.FirebaseUser;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompany;

public interface ILoginRepository {
     void registerUser(User user, String password);

    void loginIn(String userName, String password);

    Observable<Boolean> addCompanyToUser(String companyId);

    void checkLoginStatus();

    Observable<Boolean> checkUserRegistration(FirebaseUser user);

    Observable<String> getUserData();

    Observable<UserCompany> userIsAdmin();

    Observable<CompanyEntity> getCurrentCompany();

    PublishSubject<RepoEvents> getRepoEventBus();

    Observable<Boolean> logout();
}
