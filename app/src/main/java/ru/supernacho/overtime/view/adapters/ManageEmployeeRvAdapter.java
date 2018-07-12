package ru.supernacho.overtime.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.OnClick;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.presenter.ManageEmployeePresenter;
import timber.log.Timber;

public class ManageEmployeeRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> employeesList;
    private ManageEmployeePresenter presenter;

    public ManageEmployeeRvAdapter(ManageEmployeePresenter presenter) {
        this.presenter = presenter;
        this.employeesList = presenter.getEmployeesList();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_employee_view, parent, false);
        return new Employee(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Employee employee = (Employee) holder;
        employee.tvName.setText(employeesList.get(position).getFullName());
        employee.swtAdmin.setChecked(employeesList.get(position).isAdmin());
    }

    @Override
    public int getItemCount() {
        return employeesList.size();
    }

    class Employee extends RecyclerView.ViewHolder{

        private ImageButton ibFire;
        private TextView tvName;
        private SwitchCompat swtAdmin;

        public Employee(View itemView) {
            super(itemView);
            ibFire = itemView.findViewById(R.id.ibtn_fire_employee_comp_manage_view);
            swtAdmin = itemView.findViewById(R.id.swt_admin_employee_manage_view);
            tvName = itemView.findViewById(R.id.tv_name_employee_manage_view);
            swtAdmin.setOnClickListener(v -> presenter.grantAdmin(employeesList.get(getLayoutPosition())));
            ibFire.setOnClickListener(v -> {
                Timber.d("Fired user: %s", employeesList.get(getLayoutPosition()).getFullName());
                presenter.initFireEmployee(employeesList.get(getLayoutPosition()));
            });
        }
    }
}
