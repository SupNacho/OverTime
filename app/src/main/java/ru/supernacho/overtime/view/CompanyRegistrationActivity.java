package ru.supernacho.overtime.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.CompanyRegistrationPresenter;
import timber.log.Timber;

public class CompanyRegistrationActivity extends MvpAppCompatActivity implements CompanyRegistrationView {

    private final String COMPANY_NAME = "companyName";
    private final String COMPANY_ADDRESS = "companyAddress";
    private final String COMPANY_EMAIL = "companyEmail";
    private final String COMPANY_PHONE = "companyPhone";
    private final String COMPANY_CHIEF = "companyChief";

    @BindView(R.id.et_company_name_registration)
    EditText etCompanyName;
    @BindView(R.id.et_company_address_registration)
    EditText etCompanyAddress;
    @BindView(R.id.et_company_phone_registration)
    EditText etCompanyPhone;
    @BindView(R.id.et_company_email_registration)
    EditText etCompanyEmail;
    @BindView(R.id.et_company_chief_registration)
    EditText etCompanyChief;

    @InjectPresenter
    CompanyRegistrationPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_registration);
        ButterKnife.bind(this);
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(COMPANY_NAME))
                etCompanyName.setText(savedInstanceState.getString(COMPANY_NAME));
            if (savedInstanceState.containsKey(COMPANY_ADDRESS))
                etCompanyAddress.setText(savedInstanceState.getString(COMPANY_ADDRESS));
            if (savedInstanceState.containsKey(COMPANY_EMAIL))
                etCompanyEmail.setText(savedInstanceState.getString(COMPANY_EMAIL));
            if (savedInstanceState.containsKey(COMPANY_PHONE))
                etCompanyPhone.setText(savedInstanceState.getString(COMPANY_PHONE));
            if (savedInstanceState.containsKey(COMPANY_CHIEF))
                etCompanyChief.setText(savedInstanceState.getString(COMPANY_CHIEF));
        }
    }

    @ProvidePresenter
    public CompanyRegistrationPresenter providePresenter() {
        CompanyRegistrationPresenter presenter = new CompanyRegistrationPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @OnClick({R.id.btn_register_company_registration, R.id.btn_cancel_company_registration})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_company_registration:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_register_company_registration:
                presenter.registerCompany(etCompanyName.getText().toString(), etCompanyAddress.getText().toString(),
                        etCompanyEmail.getText().toString(), etCompanyPhone.getText().toString(),
                        etCompanyChief.getText().toString());
                break;
            default:
                Toast.makeText(this, "No such button", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void registrationSuccess(String companyId) {
        Timber.d("Register Successfully");
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ActivityResultExtra.COMPANY_ID, companyId);
        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, "Registration success", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void registrationFail() {
        Timber.d("Register FAILED");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(COMPANY_NAME, etCompanyName.getText().toString());
        outState.putString(COMPANY_ADDRESS, etCompanyAddress.getText().toString());
        outState.putString(COMPANY_PHONE, etCompanyPhone.getText().toString());
        outState.putString(COMPANY_EMAIL, etCompanyEmail.getText().toString());
        outState.putString(COMPANY_CHIEF, etCompanyChief.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
