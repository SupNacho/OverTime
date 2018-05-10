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
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.repository.TimerRepository;
import ru.supernacho.overtime.view.fragments.TimerView;
import timber.log.Timber;

@InjectViewState
public class TimerPresenter extends MvpPresenter<TimerView> {
    private Scheduler uiScheduler;
    private Observable<Long> overTimeCounter;
    private DisposableObserver<Long> counterObserver;
    private Disposable restoreDisposable;
    private boolean isStarted;
    private int seconds;

    @Inject
    TimerRepository repository;

    public TimerPresenter(Scheduler uiScheduler, boolean isStarted, int secs) {
        this.uiScheduler = uiScheduler;
        this.seconds = secs;
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
        if (restoreDisposable != null) restoreDisposable.dispose();
    }

    public void startOverTime(String comment){
        Timber.d("Start overTime");
        repository.startOverTime(comment);
        String startDate = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());
        getViewState().setStartDate(startDate);
        counterObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                seconds++;
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.US,"%d:%02d:%02d",
                        hours, minutes, secs);
                getViewState().setCounter(time, seconds);
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
                .doOnDispose(() -> Timber.d("CounterDisposed"))
                .subscribe(counterObserver);
    }

    private void restoreOverTime(){
        Timber.d("restore overTime");
        restoreDisposable = repository.restoreTimerState()
                .subscribeOn(Schedulers.io())
        .observeOn(uiScheduler)
        .subscribe(getViewState()::setStartDate);

        counterObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                seconds++;
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.US,"%d:%02d:%02d",
                        hours, minutes, secs);
                getViewState().setCounter(time, seconds);
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
                .doOnDispose(() -> Timber.d("CounterDisposed"))
                .subscribe(counterObserver);
    }

    public void stopOverTime(String comment){
        Timber.d("Finish overTime");
        repository.stopOverTime(comment);
        counterObserver.dispose();
        seconds = 0;
    }

    public void switchTimer(String comment){
        isStarted = !isStarted;
        if (isStarted) {
            startOverTime(comment);
        } else {
            stopOverTime(comment);
        }
        getViewState().setTimerState(isStarted);
    }

}
