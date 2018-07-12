package ru.supernacho.overtime.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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

    private void alertFireEmployee(User employee){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fire this employee?")
                .setMessage("Are you sure want fire this employee from company?")
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Fire", (dialog, which) -> presenter.fireEmployee(employee))
                .show();
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
        alertFireEmployee(employee);
    }

    @Override
    public void fireSuccess(User employee) {
        Snackbar.make(tvCompanyManageEmployee, employee.getFullName() + " successfully fired", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void fireFailed(User employee) {
        Snackbar.make(tvCompanyManageEmployee, "Fire " + employee.getFullName() + " is failed", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void grantAdminFailed() {
        Snackbar.make(tvCompanyManageEmployee, "Grant Admin status failed!", Snackbar.LENGTH_SHORT).show();
    }
}
