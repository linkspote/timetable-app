package com.it_project.fg15a.timetable_app.helpers;

import android.content.Context;
import android.webkit.WebView;

public class dataReceiver {

    protected String sClass, sWeek;
    protected Context cContext;

    // constructor for class dataReceiver
    public dataReceiver(String p_sClass, String p_sWeek, Context p_cContext) {
        this.sClass = p_sClass;
        this.sWeek = p_sWeek;
        this.cContext = p_cContext;
    }

    // TODO: create function to get timetable for chosen class
}
