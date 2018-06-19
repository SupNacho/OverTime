package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.repository.CompanyInfoRepository;
import ru.supernacho.overtime.view.fragments.CompanyInfoView;

@InjectViewState
public class CompanyInfoPresenter extends MvpPresenter<CompanyInfoView> {
    private Scheduler uiScheduler;

    @Inject
    CompanyInfoRepository repository;

    public CompanyInfoPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    public void getCompanyInfo(){
        repository.getCompanyInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<CompanyEntity>() {
                    @Override
                    public void onNext(CompanyEntity companyEntity) {
                        getViewState().setName(companyEntity.getName());
                        getViewState().setAddress(companyEntity.getAddress());
                        getViewState().setEmail(companyEntity.getEmail());
                        getViewState().setPhone(companyEntity.getPhone());
                        getViewState().setCEO(companyEntity.getChief());
                        getViewState().setPin(companyEntity.getPin());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
