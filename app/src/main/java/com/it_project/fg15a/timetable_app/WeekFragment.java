package com.it_project.fg15a.timetable_app;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.it_project.fg15a.timetable_app.helpers.utilities;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeekFragment extends Fragment {


    public WeekFragment() {
        // Required empty public constructor
    }

    // This method creates a new instance of WeekFragment and passes the given parameters to
    // it.
    public static WeekFragment newInstance(String p_sWeek) {
        WeekFragment wfThis = new WeekFragment();

        // Pass parameters to Fragment
        Bundle bunArguments = new Bundle();
        bunArguments.putString("p_sWeek", p_sWeek);
        wfThis.setArguments(bunArguments);

        return wfThis;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get week of year
        int iThisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        // Get week of year out of bunArguments or use actual week if not given
        String sWeek = getArguments() != null ? getArguments().getString("p_sWeek") :
                "" + (iThisWeek < 10 ? "0" : "") + String.valueOf(iThisWeek);

        String sUri = "https://bbsovg-magdeburg.de/stundenplan/klassen/" + sWeek
                + "/c/c00042.htm";

        // Get the WebView for the timetable
        // TODO: implement Software acceleration as setting for this WebView / improve performance
        View vwRoot = inflater.inflate(R.layout.fragment_week, container, false);
        WebView wvFragmentWeek = (WebView) vwRoot.findViewById(R.id.wvFragmentWeek);

        // Initialize new object of utilities class
        utilities util = new utilities();

        // if user is connected to the internet
        if (util.isOnline(getContext())) {
            // load timetable directly from website
            wvFragmentWeek.loadUrl(sUri);
        } else {
            // show message to inform user why timetable doesn't load
            Snackbar.make(vwRoot, "Please establish an internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        // Inflate the layout for this fragment
        return vwRoot;
    }

}
