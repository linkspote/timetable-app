package com.it_project.fg15a.timetable_app;

import android.os.Bundle;
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
import com.it_project.fg15a.timetable_app.helpers.dataReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimetableFragment extends Fragment {

    public TimetableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View vwRoot = inflater.inflate(R.layout.fragment_timetable, container, false);

        String sUri = "https://bbsovg-magdeburg.de/stundenplan/klassen/09/c/c00042.htm";

        final RequestQueue rqTimetable = Volley.newRequestQueue(this.getContext());
        StringRequest srTimetablePage = new StringRequest(Request.Method.GET, sUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Snackbar.make(vwRoot, "Erfolg!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                        rqTimetable.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(vwRoot, "Misserfolg!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        rqTimetable.stop();
                    }
                }
        );

        rqTimetable.add(srTimetablePage);

        String [] arrHours = {
                "Deutsch",
                "Mathe",
                "Englisch",
                "Spanisch",
                "Sport",
                "Deutsch",
                "Mathe",
                "Englisch",
                "Spanisch",
                "Sport"
        }; // array with example data for ListView fragment

        List<String> lsHours = new ArrayList<>(Arrays.asList(arrHours)); // turn Array into List

        ArrayAdapter<String> arradHours = new ArrayAdapter<>(
                getActivity(), R.layout.list_item_hours, R.id.tv_Hours, lsHours);

        ListView lvHours = (ListView) vwRoot.findViewById(R.id.lv_Hours); // find ListView in Fragment to fill it with data
        lvHours.setAdapter(arradHours); // set data of ListView respectively of the ListItems

        return vwRoot;
    }
}
