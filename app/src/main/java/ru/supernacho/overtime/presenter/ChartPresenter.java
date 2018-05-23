package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.model.repository.ChartRepository;
import ru.supernacho.overtime.view.fragments.ChartView;

@InjectViewState
public class ChartPresenter extends MvpPresenter<ChartView> {
    private Scheduler uiScheduler;
    private DisposableObserver<List<OverTimeEntity>> disposableObserver;

    @Inject
    ChartRepository repository;

    public ChartPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    public void getOverTimes(int month, int year){
        repository.getOverTimes(month, year)
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<List<OverTimeEntity>>() {
                    @Override
                    public void onNext(List<OverTimeEntity> overTimeEntities) {
                        getViewState().updateChartView(overTimeEntities);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposableObserver != null) disposableObserver.dispose();
    }
}
