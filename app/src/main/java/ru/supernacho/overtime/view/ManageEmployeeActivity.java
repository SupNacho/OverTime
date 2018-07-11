package ru.supernacho.overtime.view;

import android.os.Bundle;
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
import ru.supernacho.overtime.presenter.ManageEmployeePresenter;
import ru.supernacho.overtime.view.adapters.ManageEmplyeeRvAdapter;

public class ManageEmployeeActivity extends MvpAppCompatActivity implements ManageEmployeeView{

    @InjectPresenter
    ManageEmployeePresenter presenter;
    @BindView(R.id.tv_company_manage_employee)
    TextView tvCompanyManageEmployee;
    @BindView(R.id.rv_manage_employee)
    RecyclerView rvManageEmployee;

    private ManageEmplyeeRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_employee);
        ButterKnife.bind(this);
        initRV();
        presenter.getEmployees();

    }

    private void initRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvManageEmployee.setLayoutManager(linearLayoutManager);
        adapter = new ManageEmplyeeRvAdapter(presenter);
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
}
