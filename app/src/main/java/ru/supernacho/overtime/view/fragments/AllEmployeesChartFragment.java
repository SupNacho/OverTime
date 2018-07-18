package ru.supernacho.overtime.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.github.mikephil.charting.charts.PieChart;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;
import ru.supernacho.overtime.presenter.AllEmplPresenter;

public class AllEmployeesChartFragment extends MvpAppCompatFragment implements AllEmplView {
    private static final String MONTH = "month";
    private static final String YEAR = "year";

    private int month;
    private int year;
    private Unbinder unbinder;

    @BindView(R.id.pie_chart_all_empl_chart_fragment)
    PieChart pieChart;

    @InjectPresenter
    AllEmplPresenter presenter;

    public AllEmployeesChartFragment() {
        // Required empty public constructor
    }

    public static AllEmployeesChartFragment newInstance(int param1, int param2) {
        AllEmployeesChartFragment fragment = new AllEmployeesChartFragment();
        Bundle args = new Bundle();
        args.putInt(MONTH, param1);
        args.putInt(YEAR, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @ProvidePresenter
    public AllEmplPresenter providePresenter(){
        AllEmplPresenter presenter = new AllEmplPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            month = getArguments().getInt(MONTH);
            year = getArguments().getInt(YEAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_eployees_chart, container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter.getEmployeesStat(month, year);
        return view;
    }

    @Override
    public void updateChartView(List<UserCompanyStat> stats) {
// TODO: 18.07.2018 piechart implementation
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) unbinder.unbind();
    }
}
