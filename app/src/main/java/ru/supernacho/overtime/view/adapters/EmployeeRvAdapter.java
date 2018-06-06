package ru.supernacho.overtime.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.presenter.ManagerPresenter;
import timber.log.Timber;

public class EmployeeRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> employs;

    public EmployeeRvAdapter(ManagerPresenter presenter) {
        this.employs = presenter.getEmploysDataLink();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emploee_view, parent, false);
        return new EmployeeView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User employee = employs.get(position);
        ((EmployeeView)holder).tvFullName.setText(employee.getFullName());
        ((EmployeeView)holder).tvEmail.setText(employee.getEmail());
    }

    @Override
    public int getItemCount() {
        return employs.size();
    }

    class EmployeeView extends RecyclerView.ViewHolder{
        TextView tvFullName;
        TextView tvEmail;
        public EmployeeView(View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tv_fullname_employees_view);
            tvEmail = itemView.findViewById(R.id.tv_email_employees_view);
            itemView.setOnClickListener(v -> {
                Timber.d("User %s", employs.get(getLayoutPosition()).getFullName());
            });
        }
    }
}
