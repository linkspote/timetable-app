package com.it_project.fg15a.timetable_app.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.it_project.fg15a.timetable_app.R;

import java.util.ArrayList;

public class hourAdapter extends BaseAdapter {

    private Context cContext;
    private ArrayList<hourItem> alsHourItems;

    public hourAdapter(Context p_cContext, ArrayList<hourItem> p_alsHourItems) {
        this.cContext = p_cContext;
        this.alsHourItems = p_alsHourItems;
    }

    @Override
    public int getCount() {
        return alsHourItems.size();
    }

    @Override
    public Object getItem(int p_iPosition) {
        return alsHourItems.get(p_iPosition);
    }

    @Override
    public long getItemId(int p_iPosition) {
        return p_iPosition;
    }

    @Override
    public View getView(int p_iPosition, View p_vwConvertView, ViewGroup p_vwgParent) {
        if (p_vwConvertView == null) {
            LayoutInflater laiInflater = (LayoutInflater)
                    cContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            p_vwConvertView = laiInflater.inflate(R.layout.list_item_hour, p_vwgParent, false);
        }

        TextView tvTimeFrom = (TextView) p_vwConvertView.findViewById(R.id.tvTimeFrom);
        TextView tvTimeTo = (TextView) p_vwConvertView.findViewById(R.id.tvTimeTo);
        TextView tvSubject = (TextView) p_vwConvertView.findViewById(R.id.tvSubject);
        TextView tvTeacher = (TextView) p_vwConvertView.findViewById(R.id.tvTeacher);
        TextView tvRoom = (TextView) p_vwConvertView.findViewById(R.id.tvRoom);

        tvTimeFrom.setText(alsHourItems.get(p_iPosition).getTimeFrom());
        tvTimeTo.setText(alsHourItems.get(p_iPosition).getTimeTo());
        tvSubject.setText(alsHourItems.get(p_iPosition).getSubject());
        tvTeacher.setText(alsHourItems.get(p_iPosition).getTeacher());
        tvRoom.setText(alsHourItems.get(p_iPosition).getRoom());

        if (alsHourItems.get(p_iPosition).getEntryType().equals("3")) {
            tvSubject.setTextColor(ContextCompat.getColor(cContext, R.color.colorRed));
            tvTeacher.setTextColor(ContextCompat.getColor(cContext, R.color.colorRed));
            tvRoom.setTextColor(ContextCompat.getColor(cContext, R.color.colorRed));
        }

        return p_vwConvertView;
    }
}