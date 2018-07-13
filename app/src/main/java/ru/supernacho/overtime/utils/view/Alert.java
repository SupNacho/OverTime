package ru.supernacho.overtime.utils.view;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import ru.supernacho.overtime.presenter.AlertPresenter;

public class Alert {
    public static void create(String title, String message, String positiveText,
                              String negativeText, Context context, Object object, AlertPresenter presenter){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(negativeText, (dialog, which) -> dialog.cancel())
                .setPositiveButton(positiveText, (dialog, which) -> presenter.positiveAction(object))
                .show();
    }
}
