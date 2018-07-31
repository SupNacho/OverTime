package ru.supernacho.overtime.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
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
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.presenter.CompanyInfoPresenter;

public class CompanyInfoFragment extends MvpAppCompatDialogFragment implements View.OnClickListener,
        CompanyInfoView {

    private CompanyEntity company;
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
        btnClose.setOnClickListener(this);
        if (company == null) {
            presenter.getCompanyInfo();
        } else {
            tvName.setText(company.getName());
            tvAddress.setText(company.getAddress());
            tvPhone.setText(company.getPhone());
            tvEmail.setText(company.getEmail());
            tvCeo.setText(company.getChief());
            tvPin.setText(company.getPin());
        }
        return builder.create();
    }

    @ProvidePresenter
    public CompanyInfoPresenter providePresenter() {
        CompanyInfoPresenter presenter = new CompanyInfoPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_name_comp_info_fragment:
                webSearch(tvName.getText().toString());
                break;
            case R.id.tv_address_comp_info_fragment:
                webSearch(tvAddress.getText().toString());
                break;
            case R.id.tv_email_comp_info_fragment:
                emailToCompany(tvEmail.getText().toString());
                break;
            case R.id.tv_phone_comp_info_fragment:
                dialToCompany();
                break;
            case R.id.btn_close_comp_info_fragment:
                dismiss();
                break;
            default:
                Toast.makeText(getContext(), getResources().getString(R.string.toast_view_not_found),
                        Toast.LENGTH_SHORT).show();
        }
    }

    private void emailToCompany(String query) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, query);
        if (emailIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null)
            startActivity(emailIntent);
    }

    private void dialToCompany() {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + tvPhone.getText().toString()));
        if (dialIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null)
            startActivity(dialIntent);
    }

    private void webSearch(String query) {
        Intent webSearch = new Intent(Intent.ACTION_WEB_SEARCH);
        webSearch.putExtra(SearchManager.QUERY, query);
        if (webSearch.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null)
            startActivity(webSearch);
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
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

    @OnClick(R.id.iv_copy_company_info)
    public void onCopyInfoClicked() {
        String info = String.valueOf(tvName.getText()) + "\n" +
                tvAddress.getText() + "\n" +
                tvPhone.getText() + "\n" +
                tvEmail.getText() + "\n" +
                tvCeo.getText();
        shareIntent(info, getResources().getString(R.string.share_company_info));
    }

    @OnClick(R.id.iv_pin_company_info)
    public void onCopyPinClicked() {
        String info = String.valueOf(tvPin.getText());
        shareIntent(info, getResources().getString(R.string.share_pin));
    }

    private void shareIntent(String info, String title) {
        Intent copyInfo = new Intent(Intent.ACTION_SEND);
        copyInfo.setType("text/plain");
        copyInfo.putExtra(Intent.EXTRA_TEXT, info);
        startActivity(Intent.createChooser(copyInfo, title));
    }
}
