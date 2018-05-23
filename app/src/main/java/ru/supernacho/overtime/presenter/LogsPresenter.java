package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import ru.supernacho.overtime.view.fragments.LogsView;

@InjectViewState
public class LogsPresenter extends MvpPresenter<LogsView> {
    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().callDateChooser();
    }

    public void openChart(int month, int year){
        getViewState().openChartFragment(month, year);
    }

    public void openDateChooser(){
        getViewState().callDateChooser();
    }
}
