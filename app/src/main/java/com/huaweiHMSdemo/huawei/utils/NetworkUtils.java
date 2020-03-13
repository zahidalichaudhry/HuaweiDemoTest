package com.huaweiHMSdemo.huawei.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkUtils {

    private Context context;

    public NetworkUtils(Context pContext) {
        this.context = pContext;
    }

    public boolean isConnectedToInternet(){

        boolean connected = false;
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        }
        return connected;
    }
}
