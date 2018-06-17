package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.repository.CompanyRepository;
import ru.supernacho.overtime.view.CompanyRegistrationView;

@InjectViewState
public class CompanyRegistrationPresenter extends MvpPresenter<CompanyRegistrationView> {
    private Scheduler uiScheduler;
    private DisposableObserver<Boolean> disposableObserver;

    @Inject
    CompanyRepository repository;

    public CompanyRegistrationPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        disposableObserver = new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    getViewState().registrationSuccess();
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
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(disposableObserver);
    }
}
