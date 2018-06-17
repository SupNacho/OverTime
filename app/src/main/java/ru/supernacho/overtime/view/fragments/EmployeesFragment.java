package ru.supernacho.overtime.view.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import ru.supernacho.overtime.presenter.EmployeesPresenter;
import ru.supernacho.overtime.presenter.ManagerPresenter;
import ru.supernacho.overtime.view.adapters.EmployeeRvAdapter;

public class EmployeesFragment extends MvpAppCompatFragment implements EmployeesView {

    private Unbinder unbinder;

    @BindView(R.id.cl_manager_fragment)
    ConstraintLayout constraintLayout;
    @BindView(R.id.srl_manager_fragment)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_manager_fragment_emp_chooser)
    RecyclerView recyclerView;

    private EmployeeRvAdapter adapter;

    @InjectPresenter
    EmployeesPresenter presenter;

    public EmployeesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employees, container, false);
        unbinder = ButterKnife.bind(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new EmployeeRvAdapter(presenter);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(presenter::getEmploysList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void viewEmployees() {
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void selectEmployee(String userId) {
        ((ManagerFragment) Objects.requireNonNull(getParentFragment())).openDateFragment(userId);
    }

    @ProvidePresenter
    public EmployeesPresenter providePresenter(){
        EmployeesPresenter presenter = new EmployeesPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }
}
