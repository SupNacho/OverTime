package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.model.repository.ChartRepository;
import ru.supernacho.overtime.view.fragments.ChartView;
import timber.log.Timber;

@InjectViewState
public class ChartPresenter extends MvpPresenter<ChartView> {
    private Scheduler uiScheduler;
    private List<OverTimeEntity> entities;
    private StringBuilder stringBuilder;

    @Inject
    ChartRepository repository;

    public ChartPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
        stringBuilder = new StringBuilder();
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
                        entities = overTimeEntities;
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

    public void sendReport(){
        stringBuilder.setLength(0);
        for (OverTimeEntity entity : entities) {
            stringBuilder.append(entity.getStartDateTimeLabel()).append(" -> ").append(entity.getStopDateTimeLabel())
                    .append(" : ").append(entity.getDurationString()).append(" - ").append(entity.getComment())
                    .append("\n");
        }
        Timber.d("Report: %s", stringBuilder.toString());
        getViewState().shareReport(stringBuilder.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
