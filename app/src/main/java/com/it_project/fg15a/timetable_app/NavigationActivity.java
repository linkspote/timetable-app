package com.it_project.fg15a.timetable_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.it_project.fg15a.timetable_app.helpers.dataModifier;
import com.it_project.fg15a.timetable_app.helpers.utilities;

import java.util.Calendar;
import java.util.Map;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drwlActivityNavigation;
    NavigationView nvwActivityNavigation;
    FragmentManager fmActivityNavigation;
    FragmentTransaction ftActivityNavigation;

    String sWebsiteContent;
    String sActualWeek = getWeek(false);
    String sChosenWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // set default settings
        PreferenceManager.setDefaultValues(this, R.xml.activity_settings, false);

        // TODO: find good spots for using the preference values
        // get preferences
        //SharedPreferences spThis = PreferenceManager.getDefaultSharedPreferences(this);
        //boolean bPKRememberPlan = spThis.getBoolean(getString(R.string.key_rememberPlan), false);

        // Find the DrawerLayout and the NavigationView
        drwlActivityNavigation = (DrawerLayout) findViewById(R.id.drwlActivityNavigation);
        nvwActivityNavigation = (NavigationView) findViewById(R.id.nvwActivityNavigation);

        // Initialize new object of utilities class
        utilities util = new utilities();

        // if user is not connected to the internet
        if (!util.isOnline(this)) {
            View vwRoot = findViewById(android.R.id.content);
            // show message to inform user why timetable doesn't load
            if (vwRoot != null) {
                Snackbar.make(vwRoot, "Please establish an internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        sChosenWeek = sActualWeek;

        // Inflate FragmentTabHost at first
        fmActivityNavigation = getSupportFragmentManager();
        ftActivityNavigation = fmActivityNavigation.beginTransaction();
        ftActivityNavigation.replace(
                R.id.flActivityNavigation,
                TabHostFragment.newInstance(getMapDayData(sChosenWeek)),
                "FRAGMENT_DAY_VIEW"
        ).commit();

        createMenuItemsOldWeeks();

        nvwActivityNavigation.setCheckedItem(R.id.nav_item_actual_week);
        nvwActivityNavigation.getMenu().findItem(R.id.nav_item_actual_week).setTitle(
                getString(R.string.navigation_drawer_actualWeek, sActualWeek));
        nvwActivityNavigation.getMenu().findItem(R.id.nav_item_next_week).setTitle(getString(
                R.string.navigation_drawer_nextWeek, getWeek(true)));

        // Setup click events for Navigation Drawer items
        nvwActivityNavigation.setNavigationItemSelectedListener(this);

        // Setup toggle of Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drwlActivityNavigation, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drwlActivityNavigation.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        drwlActivityNavigation = (DrawerLayout) findViewById(R.id.drwlActivityNavigation);
        if (drwlActivityNavigation != null) {
            if (drwlActivityNavigation.isDrawerOpen(GravityCompat.START)) {
                drwlActivityNavigation.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {

            getDayOrWeekViewContent(sChosenWeek);

            return true;

        } else if (id == R.id.action_switchView) {

            // Initialize new object of utilities class
            utilities util = new utilities();

            // if user is not connected to the internet
            if (!util.isOnline(this)) {
                View vwRoot = findViewById(android.R.id.content);
                // show message to inform user why timetable doesn't load
                if (vwRoot != null) {
                    Snackbar.make(vwRoot, "Please establish an internet connection!",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
            // user is connected to the internet
            else {
                // begin transaction
                ftActivityNavigation = fmActivityNavigation.beginTransaction();

                // if current fragment is day view
                if (fmActivityNavigation.findFragmentByTag("FRAGMENT_DAY_VIEW") != null &&
                        fmActivityNavigation.findFragmentByTag("FRAGMENT_DAY_VIEW").isVisible()) {
                    // create new instance of week view fragment
                    ftActivityNavigation.replace(
                            R.id.flActivityNavigation,
                            WeekFragment.newInstance(sChosenWeek),
                            "FRAGMENT_WEEK_VIEW"
                    ).commit();
                }
                // if current fragment is week view
                else if (fmActivityNavigation.findFragmentByTag("FRAGMENT_WEEK_VIEW") != null &&
                        fmActivityNavigation.findFragmentByTag("FRAGMENT_WEEK_VIEW").isVisible()) {
                    // create new instance of day view fragment
                    ftActivityNavigation.replace(
                            R.id.flActivityNavigation,
                            TabHostFragment.newInstance(getMapDayData(sChosenWeek)),
                            "FRAGMENT_DAY_VIEW"
                    ).commit();
                }
            }

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle menu_options view item clicks here.
        int id = item.getItemId();
        boolean bReturnValue;

        if (id == R.id.nav_item_actual_week) {

            sChosenWeek = getWeek(false);
            getDayOrWeekViewContent(sChosenWeek);
            bReturnValue = true;

        } else if (id == R.id.nav_item_next_week) {

            sChosenWeek = getWeek(true);
            getDayOrWeekViewContent(sChosenWeek);
            bReturnValue = true;

        } /*else if (id == R.id.nav_item_marks) {

            ftActivityNavigation = fmActivityNavigation.beginTransaction();
            ftActivityNavigation.replace(
                    R.id.flActivityNavigation,
                    new MarkFragment(),
                    "FRAGMENT_MARK"
            ).commit();

            Toast.makeText(this, "Das " + item.getTitle() + "-Feature ist demnächst verfügbar!",
                    Toast.LENGTH_SHORT).show();
            bReturnValue = false;

        }*/ else if (id == R.id.nav_item_settings) {

            // Start SettingsActivity
            startActivity(new Intent(this, SettingsActivity.class));
            bReturnValue = false;

        } else if (id == R.id.nav_bugreport) {

            // Use browser window to open issues page on github
            Uri uriBugReport = Uri.parse("https://github.com/webnews2/timetable-app/issues");
            startActivity(new Intent(Intent.ACTION_VIEW, uriBugReport));
            bReturnValue = false;

        } else {

            sChosenWeek = String.valueOf(id);
            getDayOrWeekViewContent(sChosenWeek);
            bReturnValue = true;

        }

        drwlActivityNavigation.closeDrawers();
        return bReturnValue;
    }

    // This method returns the map that is necessary for the day view
    public Map<String, String[]> getMapDayData (String p_sWeek) {
        // Uri for plan to load
        String sUri = "https://bbsovg-magdeburg.de/stundenplan/klassen/" + p_sWeek
                + "/c/c00042.htm";

        try {
            sWebsiteContent = new getWebContentTask().execute(sUri).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new dataModifier().preModifyContent(sWebsiteContent);
    }

    private class getWebContentTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pdWebContentTask;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdWebContentTask = ProgressDialog.show(
                    NavigationActivity.this,
                    getString(R.string.dialogTitle_webContentTask),
                    getString(R.string.dialogMessage_webContentTask),
                    true
            );
        }

        @Override
        protected String doInBackground(String... p_sParameters) {
            return new dataModifier().getWebContent(p_sParameters[0]);
        }

        @Override
        protected void onPostExecute(String p_sResult) {
            super.onPostExecute(p_sResult);
            pdWebContentTask.dismiss();
        }
    }

    /**
     * This method returns a string containing the actual/next week of year with leading zero.
     * @param p_bNextWeek true to get next week of year
     * @return The string containing the week of year value
     */
    public String getWeek (boolean p_bNextWeek) {
        // Get week of year
        int iThisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        // return week of year with leading zero
        return (iThisWeek < 10 ? "0" : "") + String.valueOf((p_bNextWeek) ?
                iThisWeek + 1 : iThisWeek);
    }

    public void getDayOrWeekViewContent(String p_sWeek) {
        // Initialize new object of utilities class
        utilities util = new utilities();

        // if user is not connected to the internet
        if (!util.isOnline(this)) {
            View vwRoot = findViewById(android.R.id.content);
            // show message to inform user why timetable doesn't load
            if (vwRoot != null) {
                Snackbar.make(vwRoot, "Please establish an internet connection!",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
        // user is connected to the internet
        else {
            // begin transaction
            ftActivityNavigation = fmActivityNavigation.beginTransaction();

            // if current fragment is day view
            if (fmActivityNavigation.findFragmentByTag("FRAGMENT_DAY_VIEW") != null &&
                    fmActivityNavigation.findFragmentByTag("FRAGMENT_DAY_VIEW").isVisible()) {
                // create new instance of day view fragment
                ftActivityNavigation.replace(
                        R.id.flActivityNavigation,
                        TabHostFragment.newInstance(getMapDayData(p_sWeek)),
                        "FRAGMENT_DAY_VIEW"
                ).commit();
            }
            // if current fragment is week view
            else if (fmActivityNavigation.findFragmentByTag("FRAGMENT_WEEK_VIEW") != null &&
                    fmActivityNavigation.findFragmentByTag("FRAGMENT_WEEK_VIEW").isVisible()) {
                // create new instance of week view fragment
                ftActivityNavigation.replace(
                        R.id.flActivityNavigation,
                        WeekFragment.newInstance(p_sWeek),
                        "FRAGMENT_WEEK_VIEW"
                ).commit();
            }
        }
    }

    /**
     * This method creates the menu items for the old calendar weeks sub menu of the navigation.
     */
    public void createMenuItemsOldWeeks() {
        // get preferences
        SharedPreferences spThis = PreferenceManager.getDefaultSharedPreferences(this);
        boolean bPKShowOldPlans = spThis.getBoolean(getString(R.string.key_showOldPlans), false);
        String sPKShowXPlans = spThis.getString(getString(R.string.key_showXPlans), "1");

        // if old plans are activated
        if (bPKShowOldPlans) {
            // get the current navigation drawer menu
            Menu mOldWeeks = nvwActivityNavigation.getMenu();

            // set the sub menu for the old plans to visible and enabled
            mOldWeeks.setGroupVisible(R.id.nav_group_timetables_old, true);
            mOldWeeks.setGroupEnabled(R.id.nav_group_timetables_old, true);

            // iterate as often as old plans have to be shown
            for (int i = Integer.valueOf(sPKShowXPlans); 1 <= i; i--) {
                // add a new menu item to the sub menu
                MenuItem miOldWeek = mOldWeeks.add(
                        R.id.nav_group_timetables_old,
                        Integer.valueOf(sActualWeek) - i,
                        Menu.NONE,
                        getString(R.string.navigation_drawer_weekX,
                                String.valueOf(Integer.valueOf(sActualWeek) - i))
                );

                // set checkable true to make highlighting possible
                miOldWeek.setCheckable(true);

                // set the icon of the menu item
                miOldWeek.setIcon(R.drawable.ic_event_note_black_24dp);
            }
        }
    }
}
