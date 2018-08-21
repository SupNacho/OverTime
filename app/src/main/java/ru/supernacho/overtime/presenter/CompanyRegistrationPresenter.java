package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.model.Entity.UserCompany;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.view.CompanyRegistrationView;

@InjectViewState
public class CompanyRegistrationPresenter extends MvpPresenter<CompanyRegistrationView> {
    private Scheduler uiScheduler;

    @Inject
    ICompanyRepository repository;

    public CompanyRegistrationPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    public void registerCompany(String name, String address, String email, String phone, String chief) {
        repository.subscribeToEBus()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<UserCompany>() {
                    @Override
                    public void onNext(UserCompany company) {
                        if (company != null) {
                            getViewState().registrationSuccess(company.getCompanyId());
                            repository.addCompanyToUser(company.getCompanyId());
                        } else {
                            getViewState().registrationFail();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
        repository.registerCompany(name, address, email, phone, chief)
                .subscribeOn(App.getFbThread())
                .observeOn(uiScheduler)
                .subscribe();
    }
}
