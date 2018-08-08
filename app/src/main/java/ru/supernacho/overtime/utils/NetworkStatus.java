package ru.supernacho.overtime.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

import ru.supernacho.overtime.App;


public class NetworkStatus
{
    private static final String TAG = "NetworkStatus";

    public enum Status
    {
        WIFI,
        MOBILE,
        ETHERNET,
        OFFLINE
    }

    private static Status currentStatus = Status.OFFLINE;

    public static Status getStatus() {
        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                currentStatus = Status.WIFI;
            }

            if(activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET)
            {
                currentStatus = Status.ETHERNET;
            }

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                currentStatus = Status.MOBILE;
            }
        }
        else
        {
            currentStatus = Status.OFFLINE;
        }

        return currentStatus;

    }
}
