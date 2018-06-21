package ru.supernacho.overtime.view.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import ru.supernacho.overtime.presenter.ChooseCompanyPresenter;
import ru.supernacho.overtime.view.TabsActivity;
import ru.supernacho.overtime.view.adapters.CompanyChooseRvAdapter;
import timber.log.Timber;

public class ChooseCompanyFragment extends MvpAppCompatDialogFragment implements ChooseCompanyView {

    private Unbinder unbinder;
    private CompanyChooseRvAdapter adapter;

    @InjectPresenter
    ChooseCompanyPresenter presenter;

    @BindView(R.id.et_join_pin_choose_comp)
    EditText etPin;
    @BindView(R.id.btn_join_comp_choose_comp)
    Button btnJoin;
    @BindView(R.id.rv_joined_companies_choose_comp)
    RecyclerView rvCompanies;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_choose_company, null);
        builder.setView(view);
        unbinder = ButterKnife.bind(this, view);
        initRv();
        presenter.getUserCompanies();
        return builder.create();
    }

    private void initRv() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new CompanyChooseRvAdapter(presenter);
        rvCompanies.setLayoutManager(layoutManager);
        rvCompanies.setAdapter(adapter);
    }

    @ProvidePresenter
    public ChooseCompanyPresenter providePresenter() {
        ChooseCompanyPresenter presenter = new ChooseCompanyPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @OnClick({R.id.btn_close_choose_comp, R.id.btn_join_comp_choose_comp})
    public void onClickClose(View view) {
        switch (view.getId()) {
            case R.id.btn_join_comp_choose_comp:
                presenter.joinCompany(etPin.getText().toString());
                break;
            case R.id.btn_close_choose_comp:
                dismiss();
                break;
            default:
                Timber.d("no such btn");
                break;
        }
    }

    @Override
    public void updateUser() {
        ((TabsActivity) Objects.requireNonNull(getActivity())).checkUserIsAdmin();

    }

    @Override
    public void updateAdapters() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void activationSuccess() {
        Snackbar.make(etPin, "Activation Success", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void activationFail() {
        Snackbar.make(etPin, "Activation fail, probably there some trouble with connection", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void deactivationSuccess() {
        Snackbar.make(etPin, "Deactivation Success", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void deactivationFail() {
        Snackbar.make(etPin, "Deactivation fail, probably there some trouble with connection", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void joinFail() {
        Snackbar.make(etPin, "Join company fail, probably there some trouble with connection", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void joinSuccess() {
        Snackbar.make(etPin, "Join company success", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        if (unbinder != null) unbinder.unbind();
        super.onDestroy();
    }
}
