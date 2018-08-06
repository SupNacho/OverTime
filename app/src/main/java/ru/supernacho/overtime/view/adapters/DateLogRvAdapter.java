package ru.supernacho.overtime.view.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.DateChooserEntry;
import ru.supernacho.overtime.presenter.DateChooserPresenter;

public class DateLogRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private DateChooserPresenter presenter;
    private List<DateChooserEntry> datesList;
    private Context context;

    public DateLogRvAdapter(DateChooserPresenter presenter) {
        this.presenter = presenter;
        this.datesList = presenter.getDateList();
        this.context = App.getInstance().getApplicationContext();
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

        switch (dateEntry.getMonth()){
            case 1:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_jan));
                break;
            case 2:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_feb));
                break;
            case 3:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_mar));
                break;
            case 4:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_apr));
                break;
            case 5:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_may));
                break;
            case 6:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_jun));
                break;
            case 7:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_jul));
                break;
            case 8:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_aug));
                break;
            case 9:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_sep));
                break;
            case 10:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_oct));
                break;
            case 11:
                ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_nov));
                break;
            case 12: ((MonthView)holder).month.setText(context.getResources().getString(R.string.month_dec));
                break;
                default:
                    Toast.makeText(context, context.getResources().getString(R.string.toast_month_not_found),
                            Toast.LENGTH_SHORT).show();
        }
        ((MonthView)holder).year.setText(String.valueOf(dateEntry.getYear()));
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    class MonthView extends RecyclerView.ViewHolder{
        private TextView monthInt;
        private TextView month;
        private TextView year;

        public MonthView(View itemView) {
            super(itemView);
            month = itemView.findViewById(R.id.tv_date_chooser_month);
            year = itemView.findViewById(R.id.tv_date_chooser_year);
            itemView.setOnClickListener(e-> presenter.viewChart(datesList.get(getLayoutPosition()).getMonth(),
                    datesList.get(getLayoutPosition()).getYear()));
        }
    }
}
