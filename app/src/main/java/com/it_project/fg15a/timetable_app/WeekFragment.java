package com.it_project.fg15a.timetable_app;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.it_project.fg15a.timetable_app.helpers.utilities;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeekFragment extends Fragment {


    public WeekFragment() {
        // Required empty public constructor
    }

    // This method creates a new instance of WeekFragment and passes the given parameter to it.
    public static WeekFragment newInstance(String p_sWeek) {
        WeekFragment wfThis = new WeekFragment();

        // Pass parameter to Fragment
        Bundle bunArguments = new Bundle();
        bunArguments.putString("p_sWeek", p_sWeek);
        wfThis.setArguments(bunArguments);

        return wfThis;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the WebView for the timetable
        final View vwRoot = inflater.inflate(R.layout.fragment_week, container, false);

        // set content of webView for timetable
        setWebViewContent(vwRoot);

//        // get swipeRefreshLayout
//        SwipeRefreshLayout srlFragmentWeek =
//                (SwipeRefreshLayout) vwRoot.findViewById(R.id.srlFragmentWeek);
//        // set refresh functionality
//        srlFragmentWeek.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // set content of webview for timetable
//                setWebViewContent(vwRoot);
//            }
//        });

        // Inflate the layout for this fragment
        return vwRoot;
    }

    public void setWebViewContent (View p_vwRoot) {
        // Get week of year
        int iThisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        // Get week of year out of bunArguments or use actual week if not given
        String sWeek = getArguments() != null ? getArguments().getString("p_sWeek") :
                "" + (iThisWeek < 10 ? "0" : "") + String.valueOf(iThisWeek);

        String sUri = "https://bbsovg-magdeburg.de/stundenplan/klassen/" + sWeek
                + "/c/c00042.htm";

        // get preferences
        SharedPreferences spThis = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sImprovePerformance = spThis.getString(getString(R.string.key_ImprovePerformance),
                "1");

        // TODO: implement Software acceleration as setting for this WebView / improve performance
        // Get the WebView for the timetable
        WebView wvFragmentWeek = (WebView) p_vwRoot.findViewById(R.id.wvFragmentWeek);

        // switch layer type on base of improvement setting
        switch (sImprovePerformance) {
            case "1": // improvement disabled
                wvFragmentWeek.setLayerType(View.LAYER_TYPE_NONE, null);
                break;
            case "2": // hardware acceleration
                wvFragmentWeek.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                break;
            case "3": // software acceleration
                wvFragmentWeek.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                break;
        }

        // Initialize new object of utilities class
        utilities util = new utilities();

        // if user is connected to the internet
        if (util.isOnline(getContext())) {
            // load timetable directly from website
            wvFragmentWeek.loadUrl(sUri);
        } else {
            // show message to inform user why timetable doesn't load
            Snackbar.make(p_vwRoot, "Please establish an internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
