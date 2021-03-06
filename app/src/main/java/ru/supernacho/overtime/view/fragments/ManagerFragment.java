package ru.supernacho.overtime.view.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.ManagerPresenter;
import ru.supernacho.overtime.view.TabsActivity;

public class ManagerFragment extends MvpAppCompatFragment implements ManagerView {

    private Unbinder unbinder;
    private FragmentManager fragmentManager;
    private String userId;

    @InjectPresenter
    ManagerPresenter presenter;

    public ManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager, container, false);
        unbinder = ButterKnife.bind(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        fragmentManager = getChildFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_manager_frag_container, new EmployeesFragment(), FragmentTag.EMPLOYEES)
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void callEmployeesChooser() {
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_manager_frag_container, new EmployeesFragment(), FragmentTag.EMPLOYEES)
                .commit();
    }

    public void openAllStatDateFragment(){
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_manager_frag_container, DateChooserFragment.newInstance(true), FragmentTag.EMP_DATE_CHOOSER)
                .commit();
    }

    @Override
    public void openDateFragment(String userId) {
        this.userId = userId;
        ((TabsActivity) Objects.requireNonNull(getActivity())).setUserId(userId);
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_manager_frag_container, DateChooserFragment.newInstance(userId), FragmentTag.EMP_DATE_CHOOSER)
                .commit();
    }

    @Override
    public void startChartFragment(int month, int year) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_manager_frag_container, ChartFragment.newInstance(month, year, userId), FragmentTag.EMPL_CHART)
                .commit();
    }

    public void startAllStatChartFragment(int month, int year) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_manager_frag_container, AllEmployeesChartFragment.newInstance(month, year), FragmentTag.ALL_EMPLOYEES_STAT)
                .commit();
    }

    @ProvidePresenter
    public ManagerPresenter providePresenter(){
        ManagerPresenter presenter = new ManagerPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }
}
