package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.model.Entity.UserCompany;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.view.CompanyRegistrationView;

@InjectViewState
public class CompanyRegistrationPresenter extends MvpPresenter<CompanyRegistrationView> {
    private Scheduler uiScheduler;
    private DisposableObserver<UserCompany> disposableObserver;

    @Inject
    ICompanyRepository repository;

    public CompanyRegistrationPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        disposableObserver = new DisposableObserver<UserCompany>() {
            @Override
            public void onNext(UserCompany company) {
                if (company != null) {
                    repository.addCompanyToUser(company.getCompanyId());
                    getViewState().registrationSuccess(company.getCompanyId());
                } else {
                    getViewState().registrationFail();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void registerCompany(String name, String address, String email, String phone, String chief){
        repository.registerCompany(name,address,email,phone,chief)
                .subscribeOn(App.getFbThread())
                .observeOn(uiScheduler)
                .subscribe(disposableObserver);
    }
}
