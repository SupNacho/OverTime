package ru.supernacho.overtime.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.LogsPresenter;

public class LogsFragment extends MvpAppCompatFragment implements LogsView {

    private Unbinder unbinder;
    private FragmentManager fragmentManager;

    @BindView(R.id.fl_logs_fragments_container)
    FrameLayout fragmentContainer;
    @InjectPresenter
    LogsPresenter presenter;

    public LogsFragment() {
        // Required empty public constructor
    }

    public static LogsFragment newInstance(){
        return new LogsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);
        unbinder = ButterKnife.bind(this, view);
        fragmentManager = getChildFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.fl_logs_fragments_container, new DateChooserFragment(), FragmentTag.DATE_CHOOSER)
                .commit();
        return view;
    }

    public void callDateChooser(){
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_logs_fragments_container, new DateChooserFragment(), FragmentTag.DATE_CHOOSER)
                .commit();
    }

    public void openChartFragment(int month, int year){
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_logs_fragments_container, ChartFragment.newInstance(month, year),
                        FragmentTag.WORKER_CHART)
                .commit();
    }

    public void startChartFragment(int month, int year){
        presenter.openChart(month,year);
    }

    public void startDateChooser(){
        presenter.openDateChooser();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
