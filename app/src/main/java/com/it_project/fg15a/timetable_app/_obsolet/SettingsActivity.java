package com.it_project.fg15a.timetable_app._obsolet;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.preference.Preference;

import com.it_project.fg15a.timetable_app.R;

import java.util.prefs.PreferenceChangeListener;

// FIXME: Get ActionBar to work
// TODO: Create a central point to get the preferences from!
// TODO: Change PreferenceActivity to PreferenceFragment
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private AppCompatDelegate mDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getDelegate().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // deprecated but only function to get layout of activity at this time
        addPreferencesFromResource(R.xml.activity_settings);

        // set change listener for performance preference
        Preference pImprovePerformance = findPreference(getString(R.string.key_ImprovePerformance));
        pImprovePerformance.setOnPreferenceChangeListener(this);

        // set change listener for show x plans preference
        Preference pShowXPlans = findPreference(getString(R.string.key_showXPlans));
        pShowXPlans.setOnPreferenceChangeListener(this);

        // get the default preference file
        SharedPreferences spThis = PreferenceManager.getDefaultSharedPreferences(this);
        // get preferences
        String sPKImprovePerformance = spThis.getString(pImprovePerformance.getKey(), "1");
        String sPKShowXPlans = spThis.getString(pShowXPlans.getKey(), "1");
        // call change method of the preferences to update their summaries
        onPreferenceChange(pImprovePerformance, sPKImprovePerformance);
        onPreferenceChange(pShowXPlans, sPKShowXPlans);

        Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_useBackButton,
                Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals(getString(R.string.key_ImprovePerformance))) {
            preference.setSummary(getString(R.string.summary_improvePerformance, getResources()
                    .getStringArray(R.array.entries_ImprovePerformance)[Integer.valueOf(o.toString()) - 1]));
            return true;
        } else if (preference.getKey().equals(getString(R.string.key_showXPlans))) {
            preference.setSummary(getString(R.string.summary_showXPlans, getResources()
                    .getStringArray(R.array.entries_ShowXPlans)[Integer.valueOf(o.toString()) - 1]));
            return true;
        } else {
            return false;
        }
    }

    private AppCompatDelegate getDelegate() {
        if(mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
}
