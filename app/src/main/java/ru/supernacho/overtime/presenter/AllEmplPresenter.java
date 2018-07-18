package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import ru.supernacho.overtime.model.repository.AllEmplRepository;
import ru.supernacho.overtime.view.fragments.AllEmplView;

@InjectViewState
public class AllEmplPresenter extends MvpPresenter<AllEmplView> {
    private Scheduler uiScheduler;
    @Inject
    AllEmplRepository repository;

    public AllEmplPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }
}
