package ru.supernacho.overtime.view.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import ru.supernacho.overtime.view.TabsActivity;
import timber.log.Timber;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class TimerFragment extends MvpAppCompatFragment implements TimerView {


    private final static String MY_SHARED_PREFS = "overtime_prefs";
    private final static String PREF_IS_STARTED = "overtime_is_started";
    private SharedPreferences prefs;

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
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.pb_overtime_progress)
    ProgressBar progressBar;
    @BindView(R.id.btn_timer_control)
    Button btnTimerControl;
    @BindView(R.id.btn_timer_add_comment)
    Button btnAddComment;

    public TimerFragment() {
        // Required empty public constructor
    }

    @ProvidePresenter
    public TimerPresenter providePresenter(){
        getSharedPrefs();
        return new TimerPresenter(AndroidSchedulers.mainThread(), isStarted);
    }

    private void getSharedPrefs() {
        prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(MY_SHARED_PREFS, Context.MODE_PRIVATE);
        isStarted = prefs.getBoolean(PREF_IS_STARTED, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        unbinder = ButterKnife.bind(this, view);
        App.getInstance().getAppComponent().inject(presenter);
        view.clearFocus();
        if (prefs == null){
            getSharedPrefs();
        }
        return view;
    }

    @OnClick(R.id.btn_timer_control)
    public void onClickTimerControl(){
        presenter.switchTimer(etComment.getEditableText().toString());
        etComment.setText("");
    }

    @OnClick(R.id.btn_timer_add_comment)
    public void onClickAddComment(){
        presenter.addComment(etComment.getEditableText().toString());
        etComment.setText("");
        Objects.requireNonNull(getView()).clearFocus();
        ((TabsActivity) Objects.requireNonNull(getActivity())).hideSoftKeyboard();
    }

    @Override
    public void setTimerState(boolean isStarted) {
        this.isStarted = isStarted;
        if (isStarted) {
            btnAddComment.setVisibility(View.VISIBLE);
            btnTimerControl.setText("Finish overtime");
            progressBar.setIndeterminate(true);
        } else {
            btnAddComment.setVisibility(View.GONE);
            btnTimerControl.setText("Start overtime");
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
    public void onStop() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_IS_STARTED, isStarted);
        editor.apply();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
