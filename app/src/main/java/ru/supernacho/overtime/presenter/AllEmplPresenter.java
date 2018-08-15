package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;
import ru.supernacho.overtime.model.repository.IAllEmplRepository;
import ru.supernacho.overtime.view.fragments.AllEmplView;

@InjectViewState
public class AllEmplPresenter extends MvpPresenter<AllEmplView> {
    private Scheduler uiScheduler;

    @Inject
    IAllEmplRepository repository;

    public AllEmplPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    public void getEmployeesStat(int month, int year){
        repository.getStats(month, year)
                .subscribeOn(App.getFbThread())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<List<UserCompanyStat>>() {
                    @Override
                    public void onNext(List<UserCompanyStat> stats) {
                        getViewState().updateChartView(stats);
                    }

                    @Override
                    public void onError(Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    public void getStatsForShare(){
        repository.getFullStatForShare()
                .subscribeOn(App.getFbThread())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        getViewState().shareFullStat(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }
}
