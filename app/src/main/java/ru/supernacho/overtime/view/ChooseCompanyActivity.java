package ru.supernacho.overtime.view;

import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.ChooseCompanyPresenter;
import ru.supernacho.overtime.utils.view.Alert;
import ru.supernacho.overtime.view.adapters.CompanyChooseRvAdapter;

public class ChooseCompanyActivity extends MvpAppCompatActivity implements ChooseCompanyView, View.OnKeyListener {

    private CompanyChooseRvAdapter adapter;

    @InjectPresenter
    ChooseCompanyPresenter presenter;

    @BindView(R.id.ll_join_comp_choose_comp)
    LinearLayout linearLayout;
    @BindView(R.id.et_join_pin_choose_comp)
    EditText etPin;
    @BindView(R.id.btn_join_comp_choose_comp)
    Button btnJoin;
    @BindView(R.id.rv_joined_companies_choose_comp)
    RecyclerView rvCompanies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_company);
        ButterKnife.bind(this);
        initRv();
        initEditText();
        presenter.getUserCompanies();
    }

    private void initEditText() {
        etPin.clearFocus();
        etPin.setOnKeyListener(this);
    }

    private void initRv() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new CompanyChooseRvAdapter(presenter);
        rvCompanies.setLayoutManager(layoutManager);
        rvCompanies.setAdapter(adapter);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER  && event.getAction() == KeyEvent.ACTION_DOWN){
            if (view.getId() == R.id.et_join_pin_choose_comp) {
                presenter.joinCompany(etPin.getText().toString());
                etPin.setText(null);
                etPin.clearFocus();
                hideKeyboard();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void initExitFromCompany(String companyId) {
        Alert.create(getResources().getString(R.string.alert_leave_company_header),
                getResources().getString(R.string.alert_leave_company_message),
                getResources().getString(R.string.alert_btn_positive),
                getResources().getString(R.string.alert_btn_negative),
                this, companyId, presenter);
    }

    @ProvidePresenter
    public ChooseCompanyPresenter providePresenter() {
        ChooseCompanyPresenter presenter = new ChooseCompanyPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @OnClick(R.id.ll_join_comp_choose_comp)
    public void onClickBody() {
        hideKeyboard();
    }

    @OnClick(R.id.btn_join_comp_choose_comp)
    public void onClickJoin() {
        presenter.joinCompany(etPin.getText().toString());
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
    }

    @Override
    public void updateAdapters() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void activationSuccess() {
        Snackbar.make(etPin, getResources().getString(R.string.snack_bar_activation_success), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void activationFail() {
        Snackbar.make(etPin, getResources().getString(R.string.snack_bar_activation_fail), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void deactivationSuccess() {
        Snackbar.make(etPin, getResources().getString(R.string.snack_bar_deactivation_success), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void deactivationFail() {
        Snackbar.make(etPin, getResources().getString(R.string.snack_bar_deactivation_fail), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void joinFail() {
        Snackbar.make(etPin, getResources().getString(R.string.snack_bar_join_fail), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void joinSuccess() {
        Snackbar.make(etPin, getResources().getString(R.string.snack_bar_join_success), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void exitError() {
        Snackbar.make(etPin, getResources().getString(R.string.snack_bar_exit_err), Snackbar.LENGTH_SHORT).show();
    }
}
