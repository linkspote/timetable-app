package com.it_project.fg15a.timetable_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.it_project.fg15a.timetable_app.helpers.dataModifier;

import java.util.Calendar;
import java.util.Map;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drwlActivityNavigation;
    NavigationView nvwActivityNavigation;
    FragmentManager fmActivityNavigation;
    FragmentTransaction ftActivityNavigation;

    String sWebsiteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Find the DrawerLayout and the NavigationView
        drwlActivityNavigation = (DrawerLayout) findViewById(R.id.drwlActivityNavigation);
        nvwActivityNavigation = (NavigationView) findViewById(R.id.nvwActivityNavigation);

        // Inflate FragmentTabHost at first
        fmActivityNavigation = getSupportFragmentManager();
        ftActivityNavigation = fmActivityNavigation.beginTransaction();
        ftActivityNavigation.replace(
                R.id.flActivityNavigation,
                TabHostFragment.newInstance(getMapDayData())
        ).commit();

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
        if (drwlActivityNavigation.isDrawerOpen(GravityCompat.START)) {
            drwlActivityNavigation.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        if (id == R.id.action_switchView) {

            Toast.makeText(this, "Switch pressed!", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_refresh) {

            Toast.makeText(this, "Refresh pressed!", Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle menu_options view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_item_demo_day) {

            // Show DayFragment by replacing the actual screen, just for demonstration
            ftActivityNavigation = fmActivityNavigation.beginTransaction();
            ftActivityNavigation.replace(R.id.flActivityNavigation, new DayFragment()).commit();

        } else if (id == R.id.nav_item_demo_week) {

            // Show WeekFragment by replacing the actual screen, just for demonstration
            ftActivityNavigation = fmActivityNavigation.beginTransaction();
            ftActivityNavigation.replace(R.id.flActivityNavigation, new WeekFragment()).commit();

        } else if (id == R.id.nav_item_settings) {

            // Start SettingsActivity
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_bugreport) {

            // Use browser window to open issues page on github
            Uri uriBugReport = Uri.parse("https://github.com/webnews2/timetable-app/issues");
            startActivity(new Intent(Intent.ACTION_VIEW, uriBugReport));

        } else {

            Toast.makeText(this, item.getTitle() + " pressed!", Toast.LENGTH_SHORT).show();
        }

        drwlActivityNavigation.closeDrawers();
        return true;
    }

    // This method returns the map that is necessary for the day view
    public Map<String, String[]> getMapDayData () {
        // Get week of year
        int iThisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        // Get actual week of year
        String sWeek = (iThisWeek < 10 ? "0" : "") + String.valueOf(iThisWeek);

        sWeek = "20";

        String sUri = "https://bbsovg-magdeburg.de/stundenplan/klassen/" + sWeek
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
}
