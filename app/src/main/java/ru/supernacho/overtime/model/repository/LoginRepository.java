package ru.supernacho.overtime.model.repository;

import com.parse.ParseUser;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.utils.NetworkStatus;
import timber.log.Timber;

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
        if (NetworkStatus.getStatus() == NetworkStatus.Status.OFFLINE){
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
    public Observable<Boolean> userIsAdmin() {
        return Observable.create(emit -> {
            boolean isAdmin = ParseUser.getCurrentUser().getBoolean(ParseFields.isAdmin);
            emit.onNext(isAdmin);
        });
    }

    public PublishSubject<RepoEvents> getRepoEventBus() {
        return repoEventBus;
    }
}
