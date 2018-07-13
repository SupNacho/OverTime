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
import ru.supernacho.overtime.utils.charts.DurationToStringConverter;
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

    public void getOverTimes(int month, int year, String userId){
        repository.getOverTimes(month, year, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<List<OverTimeEntity>>() {
                    @Override
                    public void onNext(List<OverTimeEntity> overTimeEntities) {
                        entities = overTimeEntities;
                        getViewState().updateChartView(overTimeEntities);
                        long overTimeSummary = 0L;
                        for (OverTimeEntity entity : entities) {
                            overTimeSummary += entity.getDuration();
                        }
                        getViewState().viewSummary(DurationToStringConverter.convert(overTimeSummary));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void sendReport(String overtimeLabel, String startDateLabel,
                           String stopDateLabel, String durationLabel,
                           String commentLabel){
        stringBuilder.setLength(0);
        for (OverTimeEntity entity : entities) {
            stringBuilder
                    .append(overtimeLabel).append("\n")
                    .append(startDateLabel).append(" ")
                    .append(entity.getStartDateTimeLabel()).append("\n").append(stopDateLabel)
                    .append(" ").append(entity.getStopDateTimeLabel()).append("\n")
                    .append(durationLabel).append(" ").append(entity.getDurationString())
                    .append("\n").append(commentLabel).append("\n").append(entity.getComment())
                    .append("\n\n");
        }
        Timber.d("Report: %s", stringBuilder.toString());
        getViewState().shareReport(stringBuilder.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
