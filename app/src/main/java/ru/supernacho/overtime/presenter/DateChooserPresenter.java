package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.Entity.DateChooserEntry;
import ru.supernacho.overtime.model.repository.LogRepository;
import ru.supernacho.overtime.view.fragments.DateChooserView;

@InjectViewState
public class DateChooserPresenter extends MvpPresenter<DateChooserView> {
    private Scheduler uiScheduler;
    private Disposable disposableGetDate;
    private List<DateChooserEntry> dateList;

    @Inject
    LogRepository repository;

    public DateChooserPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
        this.dateList = new ArrayList<>();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

    }

    public void getDateData(){
                   disposableGetDate = repository.getMonths().subscribeOn(Schedulers.io())
                    .observeOn(uiScheduler)
                    .subscribe(objects -> {
                        for (Object object : objects) {
                            dateList.add((DateChooserEntry)object);
                        }
                        getViewState().updateAdapters();
                    });
    }

    public List<DateChooserEntry> getDateList() {
        return dateList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposableGetDate.dispose();
    }
}
