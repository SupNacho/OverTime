package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompany;
import ru.supernacho.overtime.utils.NetworkStatus;

public class LoginRepository {
    private PublishSubject<RepoEvents> repoEventBus = PublishSubject.create();

    public void registerUser(User user, String password) {
        ParseUser pUser = new ParseUser();
        pUser.setUsername(user.getUserName());
        pUser.put(ParseFields.fullName, user.getFullName());
        if (user.getEmail() != null) pUser.setEmail(user.getEmail());
        pUser.setPassword(password);

        pUser.signUpInBackground(e -> {
            if (e == null) {
                repoEventBus.onNext(RepoEvents.REGISTRATION_SUCCESS);

            } else {
                switch (e.getCode()) {
                    case 202:
                        repoEventBus.onNext(RepoEvents.REGISTRATION_FAILED_USERNAME);
                        break;
                    case 203:
                        repoEventBus.onNext(RepoEvents.REGISTRATION_FAILED_EMAIL);
                        break;
                    default:
                        repoEventBus.onNext(RepoEvents.REGISTRATION_FAILED);
                }
            }
        });
    }

    public void loginIn(String userName, String password) {
        if (NetworkStatus.getStatus() == NetworkStatus.Status.OFFLINE) {
            repoEventBus.onNext(RepoEvents.LOGIN_SUCCESS);
        } else {
            ParseUser.logInInBackground(userName, password, (user, e) -> {
                if (user != null) {
                    repoEventBus.onNext(RepoEvents.LOGIN_SUCCESS);
                } else {
                    repoEventBus.onNext(RepoEvents.LOGIN_FAILED_WRONG_PASS);
                }
            });
        }
    }

    public Observable<Boolean> addCompanyToUser(String companyId) {
        return Observable.create(e -> {
            ParseObject companies = new ParseObject(ParseClass.USER_COMPANIES);
            companies.put(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());
            companies.put(ParseFields.userCompaniesActiveCompany, companyId);
            companies.addUnique(ParseFields.userCompaniesCompanies, companyId);
            companies.saveEventually(e1 -> {
                if (e1 == null) {
                    e.onNext(true);
                } else {
                    e.onNext(false);
                }
            });
        });
    }

    public Observable<Boolean> logout() {
        return Observable.create(emit -> {
            ParseUser.logOutInBackground(e -> {
                if (e == null) {
                    emit.onNext(true);
                } else {
                    emit.onNext(false);
                }
            });
        });
    }

    public void checkLoginStatus() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            repoEventBus.onNext(RepoEvents.LOGIN_SUCCESS);
        } else {
            repoEventBus.onNext(RepoEvents.LOGIN_NEEDED);
        }
    }

    public Observable<String> getUserData() {
        return Observable.create(emit -> {
            String username = ParseUser.getCurrentUser().getString(ParseFields.fullName);
            emit.onNext(username);
        });
    }

    public Observable<UserCompany> userIsAdmin() {
        return Observable.create(emit -> {
            ParseQuery<ParseObject> userCompaniesQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            userCompaniesQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId())
                    .findInBackground((objects, e) -> {
                        if (objects != null && objects.size() > 0) {
                            String companyId = null;
                            for (ParseObject object : objects) {
                                companyId = object.getString(ParseFields.userCompaniesActiveCompany);
                            }
                            ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
                            String finalCompanyId = companyId;
                            companyQuery.whereEqualTo(ParseFields.companyId, companyId)
                                    .whereEqualTo(ParseFields.companyAdmins, ParseUser.getCurrentUser().getObjectId())
                                    .findInBackground((objects1, e1) -> {
                                        if (objects1 != null && objects1.size() > 0) {
                                            emit.onNext(new UserCompany(finalCompanyId, true));
                                        } else {
                                            emit.onNext(new UserCompany(finalCompanyId, false));
                                        }
                                    });
                        }
                    });
        });
    }

    public PublishSubject<RepoEvents> getRepoEventBus() {
        return repoEventBus;
    }
}
