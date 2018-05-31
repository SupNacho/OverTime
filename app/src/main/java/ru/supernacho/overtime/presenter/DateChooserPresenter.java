package ru.supernacho.overtime.presenter;

import android.os.Build;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
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

    public void getDateData() {
        disposableGetDate = repository.getMonths().subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(objects -> {
                    dateList.clear();
                    for (Object object : objects) {
                        dateList.add((DateChooserEntry) object);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        dateList.sort((o1, o2) -> {
                            int year = o2.getYear() - o1.getYear();
                            if (year == 0) {
                                return o2.getMonth() - o1.getMonth();
                            } else {
                                return year;
                            }
                        });
                    }
                    getViewState().updateAdapters();
                });
    }

    public List<DateChooserEntry> getDateList() {
        return dateList;
    }

    public void viewChart(int month, int year){
        getViewState().viewChart(month, year);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposableGetDate.dispose();
    }
}
