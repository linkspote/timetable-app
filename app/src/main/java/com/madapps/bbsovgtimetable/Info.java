package com.madapps.bbsovgtimetable;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;


public class Info extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // get instance of calendar for displaying actual copyright range
        Calendar cYear = Calendar.getInstance();
        int iYear = cYear.get(Calendar.YEAR);

        // set text of textView for copyright
        final TextView tvCopyright = (TextView) findViewById(R.id.textView5);
        tvCopyright.setText(Html.fromHtml((String) "&copy;").toString() + " 2014 - " + iYear
                + " MadApps");

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("Info")) finish();
        return true;
    }

}
