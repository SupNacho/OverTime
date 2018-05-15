package ru.supernacho.overtime.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.DateChooserEntry;
import ru.supernacho.overtime.presenter.DateChooserPresenter;
import timber.log.Timber;

public class DateLogRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private DateChooserPresenter presenter;
    private List<DateChooserEntry> datesList;
    private Disposable disposableDateList;

    public DateLogRvAdapter(DateChooserPresenter presenter) {
        this.presenter = presenter;
        this.datesList = presenter.getDateList();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dates_chooser_view, parent, false);
        return new MonthView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DateChooserEntry dateEntry = datesList.get(position);
        ((MonthView)holder).month.setText(String.valueOf(dateEntry.getMonth()));
        ((MonthView)holder).year.setText(String.valueOf(dateEntry.getYear()));
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    class MonthView extends RecyclerView.ViewHolder{
        private TextView month;
        private TextView year;

        public MonthView(View itemView) {
            super(itemView);
            month = itemView.findViewById(R.id.tv_date_chooser_month);
            year = itemView.findViewById(R.id.tv_date_chooser_year);
            itemView.setOnClickListener(e-> Timber.d("Position: %d", getLayoutPosition()));
        }
    }
}
