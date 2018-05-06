package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.repository.LoginRepository;
import ru.supernacho.overtime.model.repository.RepoEvents;
import ru.supernacho.overtime.view.LoginView;
import timber.log.Timber;

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    private DisposableObserver<RepoEvents> repoEventObserver;

    @Inject
    LoginRepository repository;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        App.getInstance().getAppComponent().inject(this);
        repoEventObserver = new DisposableObserver<RepoEvents>() {
            @Override
            public void onNext(RepoEvents repoEvents) {
                switch (repoEvents){
                    case LOGIN_SUCCESS:
                        Timber.d("Login success");
                        getViewState().loginSuccess();
                        break;
                    case LOGIN_FAILED_WRONG_PASS:
                        getViewState().loginError("Wrong password or login");
                        break;
                    case LOGIN_FAILED_NO_CONNECTION:
                        Timber.d("Login failed with no connection");
                        break;
                    case LOGIN_NEEDED:
                        Timber.d("Login needed");
                        break;
                    case REGISTRATION_SUCCESS:
                        Timber.d("Registration success");
                        getViewState().registrationSuccess();
                        break;
                    case REGISTRATION_FAILED:
                        Timber.d("Registration failed");
                        getViewState().registrationFailed();
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                Timber.d("Error %s", e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
        repository.getRepoEventBus().subscribeOn(Schedulers.io())
                .subscribe(repoEventObserver);
        checkLoginStatus();
    }

    public void attemptLogin(String userName, String password){
        repository.loginIn(userName, password);
    }

    public void registerUser(String userName, String email, String password){
        User user = new User(userName);
        if (email != null) user.setEmail(email);
        repository.registerUser(user, password);
    }

    public void checkLoginStatus(){
        repository.checkLoginStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        repoEventObserver.dispose();
    }
}
