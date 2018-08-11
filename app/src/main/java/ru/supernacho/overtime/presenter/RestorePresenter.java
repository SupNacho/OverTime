package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.repository.RestoreRepository;
import ru.supernacho.overtime.view.RestoreView;

@InjectViewState
public class RestorePresenter extends MvpPresenter<RestoreView> {
    private Scheduler uiScheduler;

    @Inject
    RestoreRepository repository;

    public RestorePresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    public void restorePassword(String email){
        repository.requestRestore(email)
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) getViewState().restoreStarted();
                        else getViewState().restoreFailed();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }
}
