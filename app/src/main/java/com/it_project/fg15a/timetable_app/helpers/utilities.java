package com.it_project.fg15a.timetable_app.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

public class utilities {

    // function to check internet access
    // Origin:
    // https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
    public boolean isOnline(Context context){
        // get connectivity service
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // check if there is a active network
        return cm.getActiveNetworkInfo() != null &&
                                            cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
