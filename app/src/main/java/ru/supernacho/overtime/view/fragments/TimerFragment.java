package ru.supernacho.overtime.view.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.TimerPresenter;

public class TimerFragment extends MvpAppCompatFragment implements TimerView {


    private final static String MY_SHARED_PREFS = "overtime_prefs";
    private final static String PREF_IS_STARTED = "overtime_is_started";
    private final static String PREF_SECS = "overtime_seconds";
    private SharedPreferences prefs;

    private Unbinder unbinder;
    private boolean isStarted;
    private int secs;

    @InjectPresenter
    TimerPresenter presenter;

    @BindView(R.id.tc_current_time)
    TextClock textClock;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_counter)
    TextView tvCounter;
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.pb_overtime_progress)
    ProgressBar progressBar;
    @BindView(R.id.btn_timer_control)
    Button btnTimerControl;

    public TimerFragment() {
        // Required empty public constructor
    }

    @ProvidePresenter
    public TimerPresenter providePresenter(){
        prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(MY_SHARED_PREFS, Context.MODE_PRIVATE);
        isStarted = prefs.getBoolean(PREF_IS_STARTED, false);
        secs = prefs.getInt(PREF_SECS, 0);
        return new TimerPresenter(AndroidSchedulers.mainThread(), isStarted, secs);
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
        presenter.switchTimer(etComment.getEditableText().toString());
        etComment.setText("");
    }

    @Override
    public void setTimerState(boolean isStarted) {
        this.isStarted = isStarted;
        if (isStarted) {
            btnTimerControl.setText("Finish overtime");
            progressBar.setIndeterminate(true);
        } else {
            btnTimerControl.setText("Start overtime");
            progressBar.setIndeterminate(false);
        }
    }

    @Override
    public void setCounter(String countMsg, int sec) {
        tvCounter.setText(countMsg);
        this.secs = sec;
    }

    @Override
    public void setStartDate(String startDate) {
        tvStartTime.setText(startDate);
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_IS_STARTED, isStarted);
        editor.putInt(PREF_SECS, secs);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
