package ru.supernacho.overtime.view.custom;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import ru.supernacho.overtime.model.Entity.OverTimeEntity;

public class ColoredBarDataSet extends BarDataSet {

    private List<OverTimeEntity> overTimeEntityList;
    private List<String> companyNames;

    public ColoredBarDataSet(List<BarEntry> yVals, List<OverTimeEntity> overTimeEntityList, String label) {
        super(yVals, label);
        this.overTimeEntityList = overTimeEntityList;
        companyNames = new ArrayList<>();
        for (OverTimeEntity overTimeEntity : overTimeEntityList) {
            String name = overTimeEntity.getCompany().getName();
            if (!companyNames.contains(name)) companyNames.add(name);
        }
    }

    @Override
    public int getColor(int index) {
        for (String cName : companyNames) {
            String name = overTimeEntityList.get(index).getCompany().getName();
            if (cName.equals(name)) return mColors.get(companyNames.indexOf(cName));
        }
        return super.getColor();
    }
}
