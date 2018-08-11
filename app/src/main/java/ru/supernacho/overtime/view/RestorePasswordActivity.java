package ru.supernacho.overtime.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.EditText;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.RestorePresenter;

public class RestorePasswordActivity extends MvpAppCompatActivity implements RestoreView {

    @InjectPresenter
    RestorePresenter presenter;

    @BindView(R.id.et_email_restore_actvt)
    EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);
        ButterKnife.bind(this);
    }

    @ProvidePresenter
    RestorePresenter providePresenter(){
        RestorePresenter presenter = new RestorePresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @OnClick(R.id.btn_positive_restore_actvt)
    public void onClickRestore(){
        presenter.restorePassword(etEmail.getText().toString());
    }

    @OnClick(R.id.btn_negative_restore_actvt)
    public void onClickCancel(){
        finish();
    }

    @Override
    public void restoreStarted() {
        showRecoveryResult("Check your email for password recovery link");
    }

    @Override
    public void restoreFailed() {
        showRecoveryResult("No user with this email");
    }

    private void showRecoveryResult(String msg) {
        Snackbar.make(etEmail, msg, Snackbar.LENGTH_LONG).show();
    }
}
