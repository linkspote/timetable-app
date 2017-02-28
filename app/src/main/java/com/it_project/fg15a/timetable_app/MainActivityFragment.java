package com.it_project.fg15a.timetable_app;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        View vwRoot = inflater.inflate(R.layout.fragment_main, container, false);


        ListView lvHours = (ListView) vwRoot.findViewById(R.id.lv_Hours); // find ListView in Fragment to fill it with data
        lvHours.setAdapter(arradHours); // set data of ListView respectively of the ListItems

        return vwRoot;
    }
}
