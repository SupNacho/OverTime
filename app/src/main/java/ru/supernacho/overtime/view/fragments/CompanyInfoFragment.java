package ru.supernacho.overtime.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatDialogFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.CompanyInfoPresenter;
import timber.log.Timber;

public class CompanyInfoFragment extends MvpAppCompatDialogFragment implements View.OnClickListener,
        CompanyInfoView {

    private Unbinder unbinder;
    @BindView(R.id.tv_name_comp_info_fragment)
    TextView tvName;
    @BindView(R.id.tv_address_comp_info_fragment)
    TextView tvAddress;
    @BindView(R.id.tv_email_comp_info_fragment)
    TextView tvEmail;
    @BindView(R.id.tv_phone_comp_info_fragment)
    TextView tvPhone;
    @BindView(R.id.tv_pin_comp_info_fragment)
    TextView tvPin;
    @BindView(R.id.tv_chief_comp_info_fragment)
    TextView tvCeo;
    @BindView(R.id.btn_close_comp_info_fragment)
    Button btnClose;

    @InjectPresenter
    CompanyInfoPresenter presenter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_company_info, null);
        builder.setView(view);
        unbinder = ButterKnife.bind(this, view);
        tvName.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        tvEmail.setOnClickListener(this);
        tvPhone.setOnClickListener(this);
        tvPin.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        presenter.getCompanyInfo();
        return builder.create();
    }

    @ProvidePresenter
    public CompanyInfoPresenter providePresenter(){
        CompanyInfoPresenter presenter = new CompanyInfoPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @Override
    public void onClick(View v) {
        // TODO: 19.06.2018 implement call thrid party apps for clicked info
        switch (v.getId()) {
            case R.id.tv_name_comp_info_fragment:
                Timber.d("Company Name pressed");
                break;
            case R.id.tv_address_comp_info_fragment:
                Timber.d("Company Address pressed");
                break;
            case R.id.tv_email_comp_info_fragment:
                Timber.d("Company Email pressed");
                break;
            case R.id.tv_phone_comp_info_fragment:
                Timber.d("Company Phone pressed");
                break;
            case R.id.tv_pin_comp_info_fragment:
                Timber.d("Company Pin pressed");
                break;
            case R.id.btn_close_comp_info_fragment:
                dismiss();
                break;
            default:
                Toast.makeText(getContext(), "No such view", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setName(String name) {
        tvName.setText(name);
    }

    @Override
    public void setEmail(String email) {
        tvEmail.setText(email);
    }

    @Override
    public void setPhone(String phone) {
        tvPhone.setText(phone);
    }

    @Override
    public void setAddress(String address) {
        tvAddress.setText(address);
    }

    @Override
    public void setPin(String pin) {
        tvPin.setText(pin);
    }

    @Override
    public void setCEO(String ceo) {
        tvCeo.setText(ceo);
    }

    @Override
    public void onDestroy() {
        if (unbinder != null) unbinder.unbind();
        super.onDestroy();
    }
}
