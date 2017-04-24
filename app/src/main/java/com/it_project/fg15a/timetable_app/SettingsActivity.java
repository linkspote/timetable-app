package com.it_project.fg15a.timetable_app;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.Preference;

// PreferenceActivity just temporary because there were some issues with the PreferenceFragment
// TODO: get ActionBar somehow to work
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // deprecated but only function to get layout of activity at this time
        addPreferencesFromResource(R.xml.activity_settings);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        return false;
    }

}
