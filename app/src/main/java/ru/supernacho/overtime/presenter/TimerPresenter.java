package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.model.repository.TimerRepository;
import ru.supernacho.overtime.view.fragments.TimerView;
import timber.log.Timber;

@InjectViewState
public class TimerPresenter extends MvpPresenter<TimerView> {
    private Scheduler uiScheduler;
    private Observable<Long> overTimeCounter;
    private DisposableObserver<Long> counterObserver;
    private boolean isStarted;
    private int seconds;

    @Inject
    TimerRepository repository;

    public TimerPresenter(Scheduler uiScheduler, boolean isStarted) {
        this.uiScheduler = uiScheduler;
        this.seconds = 0;
        this.isStarted = isStarted;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        overTimeCounter = Observable.interval(1, TimeUnit.SECONDS);
        if (isStarted) {
            restoreOverTime();
            getViewState().setTimerState(isStarted);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startOverTime(String comment, String companyId) {
        Timber.d("Start overTime");
        repository.startOverTime(comment, companyId);
        String startDate = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());
        getViewState().setStartDate(startDate);
        counterObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                seconds++;
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.US, "%d:%02d:%02d",
                        hours, minutes, secs);
                getViewState().setCounter(time);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        overTimeCounter
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(counterObserver);
    }

    private void restoreOverTime() {
        repository.restoreTimerState()
                .subscribeOn(App.getFbThread())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        long currTime = new Date().getTime();
                        seconds = (int) ((currTime - aLong) / 1000);
                        String startDate = new SimpleDateFormat("HH:mm:ss", Locale.US)
                                .format(aLong);
                        getViewState().setStartDate(startDate);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });

        counterObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                seconds++;
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.US, "%d:%02d:%02d",
                        hours, minutes, secs);
                getViewState().setCounter(time);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        overTimeCounter
                .subscribeOn(App.getFbThread())
                .observeOn(uiScheduler)
                .subscribe(counterObserver);
    }

    public void addComment(String comment){
        Timber.d("Adding comment");
        repository.addComment(comment);
    }

    public void stopOverTime(String comment) {
        Timber.d("Finish overTime");
        repository.stopOverTime(comment);
        counterObserver.dispose();
        seconds = 0;
    }

    public void switchTimer(String comment, String companyId) {
        isStarted = !isStarted;
        if (isStarted) {
            startOverTime(comment, companyId);
        } else {
            stopOverTime(comment);
        }
        getViewState().setTimerState(isStarted);
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
