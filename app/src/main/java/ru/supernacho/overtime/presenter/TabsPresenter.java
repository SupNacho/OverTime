package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.model.Entity.UserCompany;
import ru.supernacho.overtime.model.repository.LoginRepository;
import ru.supernacho.overtime.view.TabsView;
import timber.log.Timber;

@InjectViewState
public class TabsPresenter extends MvpPresenter<TabsView> {
    private DisposableObserver<Boolean> logoutObserver;
    private Scheduler uiScheduler;
    @Inject
    LoginRepository repository;

    public TabsPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        App.getInstance().getAppComponent().inject(this);
        logoutObserver = new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    getViewState().logoutDone();
                } else {
                    getViewState().logoutFailed();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        repository.getUserData()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        getViewState().setUserName(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void userIsAdmin(){
        repository.userIsAdmin()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<UserCompany>() {
                    @Override
                    public void onNext(UserCompany userCompany) {
                        getViewState().setAdmin(userCompany.isAdmin());
                        getViewState().setCompanyId(userCompany.getCompanyId());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //Temp method for test
    public void addArrToCompanies(){
        repository.addCompanyToUser()
                .subscribeOn(Schedulers.io())
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

    public void logout(){
        repository.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(logoutObserver);
    }
}
