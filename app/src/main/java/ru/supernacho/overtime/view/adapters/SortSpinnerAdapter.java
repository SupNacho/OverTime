package ru.supernacho.overtime.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.CompanyEntity;

public class SortSpinnerAdapter extends ArrayAdapter<CompanyEntity>{
    private Activity context;
    private List<CompanyEntity> companies;

    public SortSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<CompanyEntity> objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        this.companies = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent){
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View item = layoutInflater.inflate(R.layout.spinner_sort_by_company_item, parent, false);
        TextView name = item.findViewById(R.id.tv_company_name_spinner_item);
        name.setText(companies.get(position).getName());
        return item;
    }
}
