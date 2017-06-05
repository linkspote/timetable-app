package com.it_project.fg15a.timetable_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.it_project.fg15a.timetable_app.helpers.hourAdapter;
import com.it_project.fg15a.timetable_app.helpers.hourItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class DayFragment extends Fragment {

    public DayFragment() {
    }

    // This method creates a new instance of DayFragment and passes the given parameters to
    // it.
    public static DayFragment newInstance(int p_iDay, Map<String, String[]> p_mData){
        DayFragment dfThis = new DayFragment();

        // Pass Parameters to Fragment
        Bundle bunArguments = new Bundle();
        bunArguments.putInt("p_iDay", p_iDay);
        bunArguments.putSerializable("p_mData", (Serializable) p_mData);
        dfThis.setArguments(bunArguments);

        return dfThis;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and get the inflated view
        View vwRoot = inflater.inflate(R.layout.fragment_day, container, false);

        // find ListView in Fragment to fill it with data
        ListView lvHours = (ListView) vwRoot.findViewById(R.id.lv_Hours);


        String sRowIndex, sTimeFrom, sTimeTo, sSubject, sTeacher, sRoom;
        int iDayColumn = getArguments().getInt("p_iDay");
        int iTimeColumn = 0;
        ArrayList<hourItem> alsHours = new ArrayList<hourItem>();
        Map<String, String[]> mData = (Map<String, String[]>)
                getArguments().getSerializable("p_mData");

        if (mData != null) {
            for (Map.Entry<String, String[]> meHour : mData.entrySet()) {
                if (meHour.getKey().startsWith(String.valueOf(iDayColumn))) {
                    sRowIndex = meHour.getKey().substring(2);
                    sTimeFrom = mData.get(iTimeColumn + "_" + sRowIndex)[0];
                    sTimeTo = mData.get(iTimeColumn + "_" + sRowIndex)[1];
                    sSubject = meHour.getValue()[2];
                    sTeacher = meHour.getValue()[3];
                    sRoom = meHour.getValue()[4];

                    alsHours.add(new hourItem(sTimeFrom, sTimeTo, meHour.getValue()));
                }
            }
        }

        hourAdapter haHours = new hourAdapter(getContext(), alsHours);
        lvHours.setAdapter(haHours);

        return vwRoot;
    }
}
