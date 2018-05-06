package ru.supernacho.overtime.model.repository;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import ru.supernacho.overtime.model.Entity.User;
import timber.log.Timber;

public class LoginRepository {
    private PublishSubject<RepoEvents> repoEventBus = PublishSubject.create();

    public void registerUser(User user, String password) {
        ParseUser puser = new ParseUser();
        puser.setUsername(user.getUserName());
        if (user.getEmail() != null) puser.setEmail(user.getEmail());
        puser.setPassword(password);

        puser.signUpInBackground(e -> {
            if (e == null) {
                repoEventBus.onNext(RepoEvents.REGISTRATION_SUCCESS);
            } else {
                repoEventBus.onNext(RepoEvents.REGISTRATION_FAILED);
            }
        });
    }

    public void loginIn(String userName, String password) {
        ParseUser.logInInBackground(userName, password, (user, e) -> {
            if (user != null) {
                repoEventBus.onNext(RepoEvents.LOGIN_SUCCESS);
            } else {
                repoEventBus.onNext(RepoEvents.LOGIN_FAILED_WRONG_PASS);
            }
        });
    }

    public Observable<Boolean> logout() {
        return Observable.create( emit -> {
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
        return Observable.create( emit ->{
            String username = ParseUser.getCurrentUser().getUsername();
            emit.onNext(username);
        });
    }

    public PublishSubject<RepoEvents> getRepoEventBus() {
        return repoEventBus;
    }
}
