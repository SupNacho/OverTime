package ru.supernacho.overtime.utils.view;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.view.fragments.CompanyInfoFragment;
import ru.supernacho.overtime.view.fragments.FragmentTag;

public class CompanyInfo {
    public static void viewChosen(@NonNull @NotNull AppCompatActivity activity, CompanyEntity company){
        CompanyInfoFragment infoFragment = new CompanyInfoFragment();
        infoFragment.setCompany(company);
        infoFragment.show(activity.getSupportFragmentManager(), FragmentTag.COMPANY_INFO_DIALOG);
    }
    public static void viewCurrent(@NonNull @NotNull AppCompatActivity activity){
        CompanyInfoFragment infoFragment = new CompanyInfoFragment();
        infoFragment.show(activity.getSupportFragmentManager(), FragmentTag.COMPANY_INFO_DIALOG);
    }
}
