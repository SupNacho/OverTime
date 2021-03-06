package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.repository.ILoginRepository;
import ru.supernacho.overtime.model.repository.RepoEvents;
import ru.supernacho.overtime.view.LoginView;
import timber.log.Timber;

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    private DisposableObserver<RepoEvents> repoEventObserver;
    private Scheduler uiScheduler;

    @Inject
    ILoginRepository repository;

    public LoginPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        repoEventObserver = new DisposableObserver<RepoEvents>() {
            @Override
            public void onNext(RepoEvents repoEvents) {
                switch (repoEvents){
                    case LOGIN_SUCCESS:
                        Timber.d("Login success");
                        getViewState().loginSuccess();
                        break;
                    case LOGIN_FAILED_WRONG_PASS:
                        getViewState().loginError("Wrong login/password");
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
                    case REGISTRATION_FAILED_USERNAME:
                        Timber.d("Registration failed");
                        getViewState().registrationFailedUserName();
                        break;
                    case REGISTRATION_FAILED_EMAIL:
                        Timber.d("Registration failed");
                        getViewState().registrationFailedEmail();
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
        repository.getRepoEventBus()
                .subscribeOn(Schedulers.io())
                .subscribe(repoEventObserver);
        checkLoginStatus();
    }

    public void checkUserRegistration(FirebaseUser user){
        repository.checkUserRegistration(user)
                .subscribeOn(App.getFbThread())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        getViewState().loginSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("Err: %s", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    public void addUserToCompanies(String companyId){
        repository.addCompanyToUser(companyId)
                .subscribeOn(App.getFbThread())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean){
                            Timber.d("DONE");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void attemptLogin(String userName, String password){
        repository.loginIn(userName, password);
    }

    public void registerUser(String userName, String fullName, String email, String password){
        User user = new User(userName, fullName);
        if (email != null) user.setUserEmail(email);
        repository.registerUser(user, password);
    }

    public void checkLoginStatus(){
        repository.checkLoginStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (repoEventObserver != null) repoEventObserver.dispose();
    }
}
