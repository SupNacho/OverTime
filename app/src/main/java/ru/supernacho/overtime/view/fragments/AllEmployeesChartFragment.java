package ru.supernacho.overtime.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;
import ru.supernacho.overtime.presenter.AllEmplPresenter;
import ru.supernacho.overtime.utils.charts.DurationToStringConverter;
import ru.supernacho.overtime.utils.charts.PieChartValueFormatter;

public class AllEmployeesChartFragment extends MvpAppCompatFragment implements AllEmplView,
        OnChartValueSelectedListener{
    private static final String MONTH = "month";
    private static final String YEAR = "year";

    private int month;
    private int year;
    private Unbinder unbinder;
    private List<UserCompanyStat> stats;
    private List<PieEntry> entryList = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();

    @BindView(R.id.pie_chart_all_empl_chart_fragment)
    PieChart pieChart;
    @BindView(R.id.tv_name_all_empl_chart_fragment)
    TextView tvEmplName;
    @BindView(R.id.tv_summary_all_empl_chart_fragment)
    TextView tvSummaryDuration;
    @BindView()

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
        fillColors();
    }

    private void fillColors() {
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
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
        // TODO: 19.07.2018 add styling for pie
        this.stats = stats;
        stats.add(0, new UserCompanyStat(
                new User("","","","",false), 0));
        labels.clear();
        entryList.clear();
        for (UserCompanyStat stat : stats) {
            labels.add(stat.getUser().getFullName());
            entryList.add(new PieEntry(stats.indexOf(stat), stat.getTimeSummary()));
        }
        PieDataSet dataSet = new PieDataSet(entryList, "Summary overtime by employee");
        dataSet.setColors(colors);
        dataSet.setValueFormatter(new PieChartValueFormatter(stats));
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        UserCompanyStat userCompanyStat = stats.get((int) e.getY());
        tvEmplName.setText(userCompanyStat.getUser().getFullName());
        tvSummaryDuration.setText(DurationToStringConverter.convert(userCompanyStat.getTimeSummary()));
    }

    @Override
    public void onNothingSelected() {
        tvEmplName.setText("");
        tvSummaryDuration.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) unbinder.unbind();
    }
}
