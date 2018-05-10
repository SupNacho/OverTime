package ru.supernacho.overtime.view.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.TimerPresenter;

public class TimerFragment extends MvpAppCompatFragment implements TimerView {

    private Unbinder unbinder;
    private boolean isStarted;

    @InjectPresenter
    TimerPresenter presenter;

    @BindView(R.id.tc_current_time)
    TextClock textClock;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_counter)
    TextView tvCounter;
    @BindView(R.id.pb_overtime_progress)
    ProgressBar progressBar;
    @BindView(R.id.btn_timer_control)
    Button btnTimerControl;

    public TimerFragment() {
        // Required empty public constructor
    }

    @ProvidePresenter
    public TimerPresenter providePresenter(){
        return new TimerPresenter(AndroidSchedulers.mainThread());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        unbinder = ButterKnife.bind(this, view);
        App.getInstance().getAppComponent().inject(presenter);
        return view;
    }

    @OnClick(R.id.btn_timer_control)
    public void onClickTimerControl(){
        isStarted = !isStarted;
        if (isStarted) {
            btnTimerControl.setText("Finish overtime");
            presenter.startOverTime();
            progressBar.setIndeterminate(true);
        } else {
            btnTimerControl.setText("Start overtime");
            presenter.stopOverTime();
            progressBar.setIndeterminate(false);
        }
    }

    @Override
    public void setCounter(String countMsg) {
        tvCounter.setText(countMsg);
    }

    @Override
    public void setStartDate(String startDate) {
        tvStartTime.setText(startDate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
