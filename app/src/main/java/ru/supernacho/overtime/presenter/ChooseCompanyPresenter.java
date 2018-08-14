package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.repository.ChooseCompanyRepository;
import ru.supernacho.overtime.model.repository.IChooseCompanyRepository;
import ru.supernacho.overtime.view.ChooseCompanyView;

@InjectViewState
public class ChooseCompanyPresenter extends MvpPresenter<ChooseCompanyView> implements AlertPresenter {
    private Scheduler uiScheduler;
    private List<CompanyEntity> companies;

    @Inject
    IChooseCompanyRepository repository;

    public ChooseCompanyPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
        this.companies = new ArrayList<>();
    }

    public void getUserCompanies(){
        repository.getCompanies()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<List<CompanyEntity>>() {
                    @Override
                    public void onNext(List<CompanyEntity> companyEntities) {
                        companies.clear();
                        companies.addAll(companyEntities);
                        getViewState().updateAdapters();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void setActiveCompany(String companyId){
        repository.setActiveCompany(companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            getViewState().activationSuccess();
                        } else {
                            getViewState().activationFail();
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

    public void deactivateCompany(){
        repository.deactivateCompany()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            getViewState().deactivationSuccess();
                        } else {
                            getViewState().deactivationFail();
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

    public void joinCompany(String pin){
        repository.joinCompany(pin)
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            getUserCompanies();
                            getViewState().joinSuccess();
                        } else {
                            getViewState().joinFail();
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

    public void InitExitFromCompany(String companyId){
        getViewState().initExitFromCompany(companyId);
    }

    @Override
    public void positiveAction(Object object) {
        if (object instanceof String){
            String companyId = (String) object;
            repository.exitFromCompany(companyId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(uiScheduler)
                    .subscribe(new DisposableObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean exited) {
                            if (exited){
                                getUserCompanies();
                                getViewState().updateAdapters();
                            } else {
                                getViewState().exitError();
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
        } else {
            throw new RuntimeException("Object not instance of String");
        }
    }

    public List<CompanyEntity> getCompanies() {
        return companies;
    }
}
