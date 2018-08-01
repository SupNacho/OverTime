package ru.supernacho.overtime.view.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.presenter.ChartPresenter;
import ru.supernacho.overtime.utils.ColorLib;
import ru.supernacho.overtime.utils.charts.XAxisValuesFormatter;
import ru.supernacho.overtime.utils.charts.DataSetValueFormatter;
import ru.supernacho.overtime.utils.charts.YAxisValueFormatter;
import ru.supernacho.overtime.utils.view.CompanyInfo;
import ru.supernacho.overtime.view.custom.ColoredBarDataSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends MvpAppCompatFragment implements ChartView,
        OnChartValueSelectedListener{
    private static final String ARG_PARAM1 = "month";
    private static final String ARG_PARAM2 = "year";
    private static final String ARG_PARAM3 = "userId";

    private int month;
    private int year;
    private String userId;
    private List<OverTimeEntity> overTimesList;
    private Unbinder unbinder;
    private CompanyEntity company;

    @InjectPresenter
    ChartPresenter presenter;

    @BindView(R.id.bc_chart_fragment)
    BarChart barChart;
    @BindView(R.id.tv_company_name_chart_fragment)
    TextView tvCompanyName;
    @BindView(R.id.tv_comment_chart_fragment)
    TextView tvComment;
    @BindView(R.id.tv_month_summary_chart_fragment)
    TextView tvSummary;
    @BindView(R.id.fab_chart_fragment)
    FloatingActionButton fab;

    private List<BarEntry> yVals = new ArrayList<>();
    private List<String> labels = new ArrayList<>();


    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance(int month, int year) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, month);
        args.putInt(ARG_PARAM2, year);
        fragment.setArguments(args);
        return fragment;
    }
    public static ChartFragment newInstance(int month, int year, String userId) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, month);
        args.putInt(ARG_PARAM2, year);
        args.putString(ARG_PARAM3, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            month = getArguments().getInt(ARG_PARAM1);
            year = getArguments().getInt(ARG_PARAM2);
            userId = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter.getOverTimes(month, year, userId);
        fab.setOnClickListener(v -> presenter.sendReport(
                getResources().getString(R.string.text_rep_new_rec),
                getResources().getString(R.string.text_rep_start_date),
                getResources().getString(R.string.text_rep_end_date),
                getResources().getString(R.string.text_rep_duration),
                getResources().getString(R.string.text_rep_comment)));
        return view;
    }

    @ProvidePresenter
    public ChartPresenter providePresenter(){
        ChartPresenter presenter = new ChartPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @OnClick(R.id.ll_company_chart_fragment)
    public void onClickCompany(){
        if (getActivity() != null) CompanyInfo.viewChosen((AppCompatActivity) getActivity(), company);
    }

    @Override
    public void updateChartView(List<OverTimeEntity> overTimeEntityList) {
        overTimesList = overTimeEntityList;
        labels.clear();
        yVals.clear();
        for (OverTimeEntity overTimeEntity : overTimeEntityList) {
            labels.add(overTimeEntity.getStartDateLabel());
            yVals.add(new BarEntry(overTimeEntityList.indexOf(overTimeEntity), overTimeEntity.getDuration()));
        }
        ColoredBarDataSet dataSet = new ColoredBarDataSet(yVals, overTimesList, getResources().getString(R.string.chart_label));
        dataSet.setColors(ColorLib.getColors());
        dataSet.setValueFormatter(new DataSetValueFormatter());
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawValues(true);
        BarData barData = new BarData(dataSet);
        barData.setDrawValues(true);
        barData.setValueTextSize(15f);
        barData.setValueTextColor(R.color.colorAccent);
        barData.setBarWidth(0.6f);
        barChart.setMaxVisibleValueCount(31); //Max days in month
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setValueFormatter(new YAxisValueFormatter());
        XAxis xAxisBarChart = barChart.getXAxis();
        xAxisBarChart.setLabelCount(labels.size() > 3 ? 4 : labels.size());
        xAxisBarChart.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisBarChart.setValueFormatter(new XAxisValuesFormatter(labels));
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.setVisibleXRangeMinimum(1);
        barChart.setVisibleXRangeMaximum(4);
        barChart.setOnChartValueSelectedListener(this);
        barChart.animateXY(1000,3000);
        barChart.invalidate();
    }

    @Override
    public void shareReport(String report){
        Intent shareIntent = new Intent((Intent.ACTION_SEND));
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subj_extra_personal_stat));
        shareIntent.putExtra(Intent.EXTRA_TEXT, report);
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.chooser_title_personal_stat)));
    }

    @Override
    public void viewSummary(String summary) {
        tvSummary.setText(summary);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        OverTimeEntity overTime = overTimesList.get((int) e.getX());
        this.company = overTime.getCompany();
        tvComment.setText(overTime.getComment());
        tvCompanyName.setText(overTime.getCompany().getName());
    }

    @Override
    public void onNothingSelected() {
        tvComment.setText(getResources().getString(R.string.overtime_chart_hint));
        tvCompanyName.setText(getResources().getString(R.string.overtime_chart_no_selected));
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
