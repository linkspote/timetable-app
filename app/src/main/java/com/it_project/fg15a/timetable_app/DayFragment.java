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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.it_project.fg15a.timetable_app.helpers.dataModifier;
import com.it_project.fg15a.timetable_app.helpers.hourAdapter;
import com.it_project.fg15a.timetable_app.helpers.hourItem;
import com.it_project.fg15a.timetable_app.helpers.utilities;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
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


        String sTimeFrom = "", sTimeTo = "", sSubject = "", sTeacher = "", sRoom = "";
        int iDayColumn = getArguments().getInt("p_iDay");
        int iTimeColumn = 0;
        ArrayList<hourItem> alsHours = new ArrayList<hourItem>();
        Map<String, String[]> mData = (Map<String, String[]>)
                getArguments().getSerializable("p_mData");

        if (mData != null) {
            for (Map.Entry<String, String[]> meHour : mData.entrySet()) {
                if (meHour.getKey().startsWith(String.valueOf(iTimeColumn))) {
                    sTimeFrom = meHour.getValue()[0];
                    sTimeTo = meHour.getValue()[1];
                }
                else if (meHour.getKey().endsWith(String.valueOf(iDayColumn))) {
                    sSubject = meHour.getValue()[2];
                    sTeacher = meHour.getValue()[3];
                    sRoom = meHour.getValue()[4];
                }

                alsHours.add(new hourItem(sTimeFrom, sTimeTo, sSubject, sTeacher, sRoom));
            }
        }

        hourAdapter haHours = new hourAdapter(getContext(), alsHours);
        lvHours.setAdapter(haHours);

        return vwRoot;
    }
}
