package com.it_project.fg15a.timetable_app;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.it_project.fg15a.timetable_app.helpers.dataModifier;
import com.it_project.fg15a.timetable_app.helpers.utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabHostFragment extends Fragment {

    public TabLayout tlFragmentTabHost;
    public ViewPager vwpFragmentTabHost;
    public static int iTabs = 6;
    public View vwRoot;

    public TabHostFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate fragment_tab_host and setup views
        vwRoot = inflater.inflate(R.layout.fragment_tab_host, null);
        tlFragmentTabHost = (TabLayout) vwRoot.findViewById(R.id.tlFragmentTabHost);
        vwpFragmentTabHost = (ViewPager) vwRoot.findViewById(R.id.vwpFragmentTabHost);

        // Set adapter of the view pager
        vwpFragmentTabHost.setAdapter(new SectionsPagerAdapter(getChildFragmentManager()));

        /*
         * This is a workaround!
         * The function setupWithViewPager doesn't work without the Runnable.
         * Could be caused by a Support Library Bug.
         * TODO: Test if still necessary
         */
        tlFragmentTabHost.post(new Runnable() {
            @Override
            public void run() {
                tlFragmentTabHost.setupWithViewPager(vwpFragmentTabHost);
            }
        });

        // Inflate the layout for given fragment
        return vwRoot;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public Map<String, String[]> mDayData;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Returns the fragment for the specified position.
        // getItem is called to instantiate the fragment for the given page.
        @Override
        public Fragment getItem(int position) {
            // TODO: Create new method to get html of website!
            // Get week of year
            int iThisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

            // Get actual week of year
            String sWeek = (iThisWeek < 10 ? "0" : "") + String.valueOf(iThisWeek);

            String sUri = "https://bbsovg-magdeburg.de/stundenplan/klassen/" + sWeek
                    + "/c/c00042.htm";

            final dataModifier dmTimetable = new dataModifier();
            final RequestQueue rqTimetable = Volley.newRequestQueue(getContext());
            StringRequest srTimetablePage = new StringRequest(Request.Method.GET, sUri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // process day view data
                            mDayData = dmTimetable.modifyContent(response);

                            // stop all network activities
                            rqTimetable.stop();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // initialize a new object of utilities
                            utilities util = new utilities();

                            // if user is connected to the internet
                            if(util.isOnline(getContext())) {
                                // Throw a short information about what happened
                                Snackbar.make(vwRoot, "Something went really wrong!",
                                        Snackbar.LENGTH_LONG).setAction("Action", null).show();

                                // Show the error message for support issues
                                Toast.makeText(getContext(), error.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                // show message to inform user why timetable doesn't load
                                Snackbar.make(vwRoot, "Please establish an internet connection!",
                                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }

                            // stop all network activities
                            rqTimetable.stop();
                        }
                    }
            );

            rqTimetable.add(srTimetablePage);

            switch (position){
                case 0 : return DayFragment.newInstance(position + 1, mDayData);
                case 1 : return new BlankFragment();
                case 2 : return new BlankFragment();
                case 3 : return new BlankFragment();
                case 4 : return new BlankFragment();
                case 5 : return new BlankFragment();
            }
            return null;
        }

        // Show 6 total pages.
        @Override
        public int getCount() {
            return iTabs;
        }

        // This method returns the title of the tab according to its position.
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.section_monday);
                case 1:
                    return getString(R.string.section_tuesday);
                case 2:
                    return getString(R.string.section_wednesday);
                case 3:
                    return getString(R.string.section_thursday);
                case 4:
                    return getString(R.string.section_friday);
                case 5:
                    return getString(R.string.section_saturday);
            }
            return null;
        }
    }

}
