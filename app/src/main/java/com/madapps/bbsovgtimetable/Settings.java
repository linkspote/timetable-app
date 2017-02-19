package com.madapps.bbsovgtimetable;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Settings extends Activity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private Switch swShowOld;
    private Switch swRestoreWeek;
    private Switch swZoomWebView;
    private Switch swRefreshOnResume;
    private Switch swOfflineMode;
    private boolean bInitRestoreWeek;
    private boolean bInitShowOld;
    private boolean bInitZoomWebView;
    private boolean bRefreshOnResume;
    private boolean bOfflineMode;
    private LinearLayout llOldWeeks;
    private Spinner spinnerOldWeeksCount;
    private Spinner spinnerClassSelection;
    private int iInitSelectedOldWeekCount;
    private int iSelectedClassID;
    private JSONArray jsnArrClasses = new JSONArray();
    private boolean bInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefsEditor = prefs.edit();

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        loadAppSettings();

        initializeControls();
        assignListeners();

        LinearLayout llClassSelection = (LinearLayout)findViewById(R.id.linearLayoutClassSelection);

        if (TimeTable.bClassSelection){
            llClassSelection.setVisibility(View.VISIBLE);
        } else {
            llClassSelection.setVisibility(View.GONE);
        }

    }

    public void loadClasses(){
        List<String> sArrClasses =  new ArrayList<String>();
        if (bInit){
            sArrClasses.add("Bitte wählen...");
        }
        for (int i = 0; i < jsnArrClasses.length(); i++) sArrClasses.add(jsnArrClasses.optString(i, ""));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sArrClasses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassSelection.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("Einstellungen")) finish();

        switch (item.getItemId()) {
            case R.id.menu_settings_info:
                startActivity(new Intent(this, Info.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void loadAppSettings(){
        bInit = prefs.getBoolean("bInitSetup", true);
        bInitRestoreWeek = prefs.getBoolean("bRestoreWeek", false);
        bInitShowOld = prefs.getBoolean("bShowOld", false);
        bInitZoomWebView = prefs.getBoolean("bZoomWebView", false);
        iInitSelectedOldWeekCount = prefs.getInt("iSelectedOldWeekCount", 0);
        bRefreshOnResume = prefs.getBoolean("bRefreshOnResume", false);
        bOfflineMode = prefs.getBoolean("bOfflineMode", false);
        iSelectedClassID = prefs.getInt("iSelectedClassID", 0);
        try {
            jsnArrClasses = new JSONArray(prefs.getString("jsnClasses", "[]"));
        } catch (JSONException e) {
            jsnArrClasses = new JSONArray();
        }
    }

    public void initializeControls(){
        llOldWeeks = (LinearLayout)findViewById(R.id.linearLayoutShowOldCount);

        if (bInitShowOld){
            llOldWeeks.setVisibility(View.VISIBLE);
        } else {
            llOldWeeks.setVisibility(View.GONE);
        }

        swRestoreWeek = (Switch)findViewById(R.id.switchRestoreWeek);
        swShowOld = (Switch)findViewById(R.id.switchShowOld);
        swZoomWebView = (Switch)findViewById(R.id.switchWebZoom);
        swRefreshOnResume = (Switch)findViewById(R.id.switchRefreshOnResume);
        spinnerOldWeeksCount = (Spinner)findViewById(R.id.spinnerShowOldCount);
        spinnerClassSelection = (Spinner)findViewById(R.id.spinnerClassSelection);
        swOfflineMode = (Switch)findViewById(R.id.switchOfflineMode);
        swRestoreWeek.setChecked(bInitRestoreWeek);
        swShowOld.setChecked(bInitShowOld);
        swZoomWebView.setChecked(bInitZoomWebView);
        swRefreshOnResume.setChecked(bRefreshOnResume);
        spinnerOldWeeksCount.setSelection(iInitSelectedOldWeekCount);
        swOfflineMode.setChecked(bOfflineMode);

        if (TimeTable.bClassSelection) {
            loadClasses();
            spinnerClassSelection.setSelection((iSelectedClassID - 1 >= 0) ? iSelectedClassID - 1 : 0);
        }
    }

    public void assignListeners(){
        swRestoreWeek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefsEditor.putBoolean("bRestoreWeek", b);
                prefsEditor.commit();
            }
        });

        swShowOld.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefsEditor.putBoolean("bShowOld", b);
                prefsEditor.commit();

                if (b) {
                    llOldWeeks.setVisibility(View.VISIBLE);
                } else {
                    llOldWeeks.setVisibility(View.GONE);
                }
            }
        });

        swZoomWebView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefsEditor.putBoolean("bZoomWebView", b);
                prefsEditor.commit();
            }
        });

        swRefreshOnResume.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefsEditor.putBoolean("bRefreshOnResume", b);
                prefsEditor.commit();
            }
        });

        swOfflineMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefsEditor.putBoolean("bOfflineMode", b);
                prefsEditor.commit();
            }
        });

        spinnerOldWeeksCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefsEditor.putInt("iSelectedOldWeekCount", position);
                prefsEditor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (TimeTable.bClassSelection) {
            spinnerClassSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!spinnerClassSelection.getSelectedItem().toString().equals("Bitte wählen...")) {
                        prefsEditor.putInt("iSelectedClassID", position + ((bInit) ? 0 : 1));
                        prefsEditor.putString("sSelectedClass", spinnerClassSelection.getSelectedItem().toString());
                    } else {
                        prefsEditor.putInt("iSelectedClassID", -1);
                    }
                    prefsEditor.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }
}
