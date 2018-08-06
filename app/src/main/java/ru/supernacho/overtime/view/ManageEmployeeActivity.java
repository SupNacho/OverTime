package ru.supernacho.overtime.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.presenter.ManageEmployeePresenter;
import ru.supernacho.overtime.utils.view.Alert;
import ru.supernacho.overtime.view.adapters.ManageEmployeeRvAdapter;

public class ManageEmployeeActivity extends MvpAppCompatActivity implements ManageEmployeeView{

    @InjectPresenter
    ManageEmployeePresenter presenter;
    @BindView(R.id.tv_company_manage_employee)
    TextView tvCompanyManageEmployee;
    @BindView(R.id.rv_manage_employee)
    RecyclerView rvManageEmployee;

    private ManageEmployeeRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_employee);
        ButterKnife.bind(this);
        initRV();
        requestData();

    }

    private void requestData() {
        presenter.getCompany();
        presenter.getEmployees();
    }

    private void initRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvManageEmployee.setLayoutManager(linearLayoutManager);
        adapter = new ManageEmployeeRvAdapter(presenter);
        rvManageEmployee.setAdapter(adapter);
    }

    @ProvidePresenter
    ManageEmployeePresenter providePresenter() {
        ManageEmployeePresenter presenter = new ManageEmployeePresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @Override
    public void update() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setCompany(CompanyEntity companyEntity) {
        tvCompanyManageEmployee.setText(companyEntity.getName());
    }

    @Override
    public void initFireEmployee(User employee) {
        Alert.create(getResources().getString(R.string.alert_fire_employee_header),
                getResources().getString(R.string.alert_fire_employee_message),
                getResources().getString(R.string.alert_btn_positive),
                getResources().getString(R.string.alert_btn_negative), this, employee, presenter);
    }

    @Override
    public void fireSuccess(User employee) {
        Snackbar.make(tvCompanyManageEmployee, employee.getFullName() +
                getResources().getString(R.string.snack_bar_fire_success), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void fireFailed(User employee) {
        Snackbar.make(tvCompanyManageEmployee, employee.getFullName() +
                getResources().getString(R.string.snack_bar_fire_fail), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void grantAdminFailed() {
        Snackbar.make(tvCompanyManageEmployee, getResources().getString(R.string.snack_bar_set_admin_fail),
                Snackbar.LENGTH_SHORT).show();
    }
}
