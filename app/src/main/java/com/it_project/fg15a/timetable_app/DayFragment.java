package com.it_project.fg15a.timetable_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DayFragment extends Fragment {

    public DayFragment() {
    }

    // This method creates a new instance of DayFragment and passes the given parameters to
    // it.
    public static DayFragment newInstance(@Nullable String p_sWeek, @Nullable String p_sDay){
        DayFragment dfThis = new DayFragment();

        // Pass Parameters to Fragment
        Bundle bunArguments = new Bundle();
        bunArguments.putString("p_sWeek", p_sWeek);
        bunArguments.putString("p_sDay", p_sDay);
        dfThis.setArguments(bunArguments);

        return dfThis;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get week of year
        int iThisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        // Get week of year out of bunArguments or use actual week if not given
        String sWeek = getArguments() != null ? getArguments().getString("p_sWeek") :
                "" + (iThisWeek < 10 ? "0" : "") + String.valueOf(iThisWeek);

        final View vwRoot = inflater.inflate(R.layout.fragment_day, container, false);

        final String sUri = "https://bbsovg-magdeburg.de/stundenplan/klassen/" + sWeek
                + "/c/c00042.htm";

        final dataModifier dmTimetable = new dataModifier();

        final RequestQueue rqTimetable = Volley.newRequestQueue(this.getContext());
        final StringRequest srTimetablePage = new StringRequest(Request.Method.GET, sUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // turn Array into List
                        List<String> lsHours =
                                new ArrayList<>(Arrays.asList(dmTimetable.modifyContent(response)));

                        ArrayAdapter<String> arradHours = new ArrayAdapter<>(
                                getActivity(), R.layout.list_item_hour, R.id.tvNote, lsHours);

                        // find ListView in Fragment to fill it with data
                        ListView lvHours = (ListView) vwRoot.findViewById(R.id.lv_Hours);
                        // set data of ListView respectively of the ListItems
                        lvHours.setAdapter(arradHours);

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

        return vwRoot;
    }
}
