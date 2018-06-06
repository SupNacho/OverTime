package ru.supernacho.overtime.view.fragments;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.repository.ParseFields;
import ru.supernacho.overtime.presenter.DateChooserPresenter;
import ru.supernacho.overtime.view.adapters.DateLogRvAdapter;

public class DateChooserFragment extends MvpAppCompatFragment implements DateChooserView {

    private static final String ARG_PARAM_1 = "userId";
    private String userId;
    private Unbinder unbinder;
    private DateLogRvAdapter adapter;

    @BindView(R.id.rv_month_logs_chooser)
    RecyclerView recyclerView;
    @BindView(R.id.srl_refresh_months)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectPresenter
    DateChooserPresenter presenter;


    public DateChooserFragment() {
        // Required empty public constructor
    }

    public static DateChooserFragment newInstance(String userId){
        DateChooserFragment fragment = new DateChooserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_1, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @ProvidePresenter
    public DateChooserPresenter providePresenter(){
        String userId;
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_PARAM_1);
        } else {
            userId = ParseFields.userZero;
        }
        DateChooserPresenter presenter = new DateChooserPresenter(AndroidSchedulers.mainThread(), userId);
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_PARAM_1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_chooser, container, false);
        unbinder = ButterKnife.bind(this, view);
        initUI();
        updateData();
        return view;
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DateLogRvAdapter(presenter);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this::updateData);
    }

    private void updateData(){
        presenter.getDateData();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateAdapters() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void viewChart(int month, int year) {
        ((LogsFragment) Objects.requireNonNull(getParentFragment())).startChartFragment(month, year);
    }

    public void backToParent(){
        ((LogsFragment) Objects.requireNonNull(getParentFragment())).callDateChooser();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
