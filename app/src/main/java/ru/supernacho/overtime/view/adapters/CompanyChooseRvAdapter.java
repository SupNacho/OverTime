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

import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.presenter.ChooseCompanyPresenter;
import timber.log.Timber;

public class CompanyChooseRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ChooseCompanyPresenter presenter;
    private List<CompanyEntity> companies;

    public CompanyChooseRvAdapter(ChooseCompanyPresenter presenter) {
        this.presenter = presenter;
        this.companies = presenter.getCompanies();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.company_chooser_view, parent,false);
        return new CompanyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CompanyEntity company = companies.get(position);
        CompanyView companyView = (CompanyView) holder;
        companyView.tvName.setText(company.getName());
        companyView.swtActive.setChecked(company.isActive());
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    class CompanyView extends RecyclerView.ViewHolder{
        private TextView tvName;
        private SwitchCompat swtActive;
        private ImageButton iBtnExitCompany;

        public CompanyView(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name_comp_choose_view);
            swtActive = itemView.findViewById(R.id.swt_active_comp_choose_view);
            iBtnExitCompany = itemView.findViewById(R.id.ibtn_exit_company_comp_choose_view);
            swtActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                CompanyEntity chosenCompany = companies.get(getLayoutPosition());
                if (isChecked && !chosenCompany.isActive()){
                    Timber.d("Company Activated: %s", companies.get(getLayoutPosition()).getName());
                    for (CompanyEntity company : companies) {
                        if (company.isActive() && !company.equals(chosenCompany)) company.setActive(false);
                    }
                    chosenCompany.setActive(true);
                    presenter.setActiveCompany(companies.get(getLayoutPosition()).getObjectId());
                    notifyDataSetChanged();
                } else if (!isChecked && chosenCompany.isActive()) {
                    Timber.d("Company Deactivated: %s", companies.get(getLayoutPosition()).getName());
                    presenter.deactivateCompany();
                    chosenCompany.setActive(false);
                    notifyDataSetChanged();
                }
            });
            iBtnExitCompany.setOnClickListener(v -> {
                presenter.InitExitFromCompany(companies.get(getLayoutPosition()).getObjectId());
            });
        }
    }
}
