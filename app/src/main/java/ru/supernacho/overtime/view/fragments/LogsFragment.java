package ru.supernacho.overtime.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.repository.LogRepository;
import ru.supernacho.overtime.model.repository.LoginRepository;

public class LogsFragment extends Fragment {

    private Unbinder unbinder;
    private FragmentManager fragmentManager;

    @BindView(R.id.fl_logs_fragments_container)
    FrameLayout fragmentContainer;

    public LogsFragment() {
        // Required empty public constructor
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

    public void backToDateChooser(){
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
