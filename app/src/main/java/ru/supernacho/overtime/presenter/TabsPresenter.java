package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.UserCompany;
import ru.supernacho.overtime.model.repository.ILoginRepository;
import ru.supernacho.overtime.view.TabsView;

@InjectViewState
public class TabsPresenter extends MvpPresenter<TabsView> {
    private DisposableObserver<Boolean> logoutObserver;
    private Scheduler uiScheduler;
    @Inject
    ILoginRepository repository;

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
                .subscribeOn(App.getFbThread())
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
                .subscribeOn(App.getFbThread())
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

    public void getCurrentCompany(){
        repository.getCurrentCompany()
                .subscribeOn(App.getFbThread())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<CompanyEntity>() {
                    @Override
                    public void onNext(CompanyEntity companyEntity) {
                        getViewState().setCompany(companyEntity);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void startEmployeeManager(){
        getViewState().startEmployeeManager();
    }

    public void startCompanyChooser(){
        getViewState().startCompanyChooser();
    }

    public void startCompanyRegistration(){
        getViewState().startCompanyRegistration();
    }

    public void openCompanyInfo(){
        getViewState().openCompanyInfo();
    }

    public void logout(){
        repository.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(logoutObserver);
    }

    public void viewPolicy(){
        getViewState().showPolicy();
    }
}
