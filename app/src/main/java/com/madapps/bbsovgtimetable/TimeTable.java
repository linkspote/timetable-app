package com.madapps.bbsovgtimetable;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeTable extends Activity implements OnRefreshListener {

    public boolean bTrial = false;
    public static boolean bClassSelection = true;
    public static String sDefaultClass = "EFI12a";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private PullToRefreshLayout mPullToRefreshLayout;
    private WebView webViewForTimeTable;

    private static String sSelectedWeek = "";
    private static String sSelectedClass = "";
    private static Integer iSelectedClassID = -1;
    private static Map<String, String> mapParsedWeeks = new LinkedHashMap<String, String>();
    private static Map<String, String> mapGoneWeeks = new LinkedHashMap<String, String>();
    private static Map<String, String> mapAllWeeks = new LinkedHashMap<String, String>();
    private static JSONArray jsnArrClasses = new JSONArray();
    private static Boolean bParseSuccess = false;
    private static boolean bHasOfflineData = false;
    private static boolean bGetWebsiteContent = false;
    private static boolean bIsOnline = false;
    private static boolean bRefreshOnResume = false;
    private static String sParseURL = "http://klassen.bbsovg-magdeburg.de/frames/navbar.htm";
    private static String sTimeTableBaseURL = "http://klassen.bbsovg-magdeburg.de/";
    private Handler mHandler = new Handler();
    private ActionBar mActionBar;
    /*
    App settings
     */
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private static boolean bZoomWebView = false;
    private static boolean bOfflineMode = false;
    private static boolean bRestoreWeek = false;
    private static boolean bShowOld = false;
    private static int iOldWeeksCount = 0;
    private static boolean bIsRotating = false;
    private static boolean bIsOnStart = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && bTrial){
            Calendar calNow = Calendar.getInstance();
            Calendar calTrial = Calendar.getInstance();

            calTrial.set(2014, Calendar.MAY, 1, 0, 0, 0);

            if (calNow.before(calTrial)){
                toastShort("Trial Version! " + String.valueOf(calTrial.get(Calendar.DAY_OF_YEAR) - calNow.get(Calendar.DAY_OF_YEAR)) + " Tage verbleibend.");
            } else {
                toastShort("Testzeit abgelaufen!");
                finish();
            }
        }

        setContentView(R.layout.activity_time_table);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefsEditor = prefs.edit();

        webViewForTimeTable = (WebView)findViewById(R.id.webViewTimeTable);
        webViewForTimeTable.setWebViewClient(new InsideWebViewClient());
        webViewForTimeTable.getSettings().setDisplayZoomControls(false);

        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout)findViewById(R.id.ptr_layout);
        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this)
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set the OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.drawer_open, // nav drawer open - description for accessibility
                R.string.app_title // nav drawer close - description for accessibility
        ) {};
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState(); // sets new toggle icon and position
        mActionBar = getActionBar();

        bIsRotating = (savedInstanceState != null);
    }

    public void refresh(boolean bIsPulledToRefresh){
        if (!bIsPulledToRefresh && !bIsRotating) mPullToRefreshLayout.setRefreshing(true);

        boolean bHideDrawer = false;

        bIsOnline = isOnline();

        if (!bIsOnline){
            if (!bOfflineMode){
                bHideDrawer = true;
            } else {
                if (!bHasOfflineData) {
                    loadOfflineData();
                    if (!bHasOfflineData) bHideDrawer = true;
                }
            }

            loadTimeTable();
        } else {
            // check if app starts (first app loading)
            if (!bParseSuccess){
                // parse timeTable website and init app (and loads time table for first week)
                new WebsiteParser().execute(sParseURL);
                // app load succeeded (app changes orientation)
            } else {
                // load timetable on rotation
                loadTimeTable();
            }
        }

        if (bHideDrawer) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setHomeButtonEnabled(false);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            setDrawerAdapter();
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // check if navigation drawer is opened
            if (mDrawerLayout.isDrawerOpen(mDrawerList)){
                // close navigation drawer
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                // kill app when back button is pressed and delete app cached data
                finish();
        	    killApp(100);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void killApp(int iWaitMilliSeconds){
        mHandler.postDelayed(new Runnable() {
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, iWaitMilliSeconds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.time_table, menu);
        return true;
    }

    // load timetable
    public void loadTimeTable(){
        mActionBar.setTitle(getDrawerTitle());

        if (bIsOnline){
            if (bClassSelection){
                try {
                    if (iSelectedClassID < 0 || iSelectedClassID > jsnArrClasses.length() - 1 || (!jsnArrClasses.getString(iSelectedClassID - 1).equalsIgnoreCase(sSelectedClass))) {
                        if (iSelectedClassID == -1) {
                            toastShort("Bitte die gewünschte Klasse wählen!");
                            runSetup(false);
                        } else {
                            toastLong("Die Klasse muss neu gewählt werden, da es Änderungen auf der Website gab!");
                            deleteObsoletePrefs();
                            runSetup(true);
                        }

                        return;
                    }
                } catch (JSONException e) {
                    toastShort("Fehler 3. Die App wurde beendet!");
                    finish();
                    killApp(1000);
                }
            }

            if (!bIsRotating){
                // clear cache
                webViewForTimeTable.clearCache(true);
            }

            String sClass = Functions.convertClassForURL(iSelectedClassID);

            class MyJavaScriptInterface
            {
                private String sCLASS, sWEEK, sDATETIME;

                public MyJavaScriptInterface(String sClass, String sWeek, String sDateTime)
                {
                    sCLASS = sClass;
                    sWEEK = sWeek;
                    sDATETIME = sDateTime;
                }

                @SuppressWarnings("unused")
                @JavascriptInterface
                public void processContent(String sContent)
                {
                    setWebsiteContentPreferences(sCLASS, sWEEK, sDATETIME, sContent);
                }
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date dateNow = new Date();
            String sDateTime = dateFormat.format(dateNow);

            bGetWebsiteContent = true;
            webViewForTimeTable.getSettings().setJavaScriptEnabled(true);
            webViewForTimeTable.addJavascriptInterface(new MyJavaScriptInterface(sClass, sSelectedWeek, sDateTime), "INTERFACE");
            webViewForTimeTable.loadUrl(sTimeTableBaseURL + sSelectedWeek + "/c/c" +sClass + ".htm");
        } else {
            if (bHasOfflineData && bOfflineMode){
                String sClass = Functions.convertClassForURL(iSelectedClassID);

                Set<String> setHTML = prefs.getStringSet("offlineContent" + sClass + sSelectedWeek, new LinkedHashSet<String>());
                if (setHTML.size() == 0){
                    loadOfflineWebsite(!bIsRotating);
                } else {
                    String[] arrHTML = setHTML.toArray(new String[setHTML.size()]);
                    java.util.Arrays.sort(arrHTML);
                    String sHTMLContent = arrHTML[3].substring(1);

                    if (!bIsRotating){
                        String sDate = arrHTML[2].substring(1);
                        Pattern ptnDate = Pattern.compile("(.+?)/(.+?)/(.+?) (.+)");
                        Matcher mtrDate = ptnDate.matcher(sDate);

                        if (mtrDate.find()){
                            String sParsedDate = String.format("%s.%s.%s %s", mtrDate.group(3), mtrDate.group(2), mtrDate.group(1), mtrDate.group(4));
                            toastShort("Offline-Modus! Daten sind vom " + sParsedDate);
                        } else {
                            toastShort("Offline-Modus");
                        }
                    }
                    // normal loadData with encoding not working (bug)
                    //webViewForTimeTable.loadDataWithBaseURL(null, sHTMLContent, "text/html", "iso-8859-1", null);
                    webViewForTimeTable.loadDataWithBaseURL(null, sHTMLContent, "text/html", "utf-8", null);
                }
            } else {
                loadOfflineWebsite(!bIsRotating);
            }
        }
    }
    
    // function to check internet access
    protected boolean isOnline(){
        // get connectivity service
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        // check if there is a active network
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle navigation drawer
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                if (iSelectedClassID > -1 || !bClassSelection)
                {
                    startActivity(new Intent(this, Settings.class));
                } else {
                    if (isOnline()){
                        refresh(false);
                    } else {
                        toastShort("Für die Erstkonfiguration wird eine aktive Verbindung zum Internet benötigt!");
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // class to run tasks asynchronous
    private class WebsiteParser extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String... sArrUrls) {
            try {
                String sUrl = sArrUrls[0];
                String sHTML = WebContentReader.getContents(sUrl);
                
                // .*? needed because page returns sometimes "var classes = " or var "classes="
                Pattern patternItems = Pattern.compile("var classes.*?=.*?(.+?);.+<select name=\"week\".+?>(.+?)</select>");
                Pattern patternOptions = Pattern.compile("<option value=\"(.+?)\">(.+?)</option>");
                Pattern patternClasses = Pattern.compile("\"(.+?)\"");
                
                Matcher matcherItems = patternItems.matcher(sHTML);
                
                //publishProgress(i);  --> onProgressUpdate
                
                // if regex found a string
                if (matcherItems.find()){
                    String sClasses = matcherItems.group(1);
                    String sWeekOptions = matcherItems.group(2);

                    Matcher matcherWeekOptions = patternOptions.matcher(sWeekOptions);
                    Matcher matcherClasses = patternClasses.matcher(sClasses);

                    mapParsedWeeks = new LinkedHashMap<String, String>();

                    // while matcher finds weeks
                    while (matcherWeekOptions.find()){
                        mapParsedWeeks.put(matcherWeekOptions.group(1), matcherWeekOptions.group(2));
                    }

                    jsnArrClasses = new JSONArray();

                    // while matcher finds classes
                    while (matcherClasses.find()){
                        jsnArrClasses.put(matcherClasses.group(1));
                    }
                }
                
                return (jsnArrClasses.length() == 0 || mapParsedWeeks.size() == 0) ? 0 : 1;
            } catch (Exception e){
                return 0;
            }
        }

        // executes when process updates (publishProgress(i) is called)
        protected void onProgressUpdate(Integer... progress) {
            // do something
        }

        // executes when async task is done
        protected void onPostExecute(Integer iState) {
            // if parsing website ended without error
            if (iState == 1){
                calculateGoneWeeks();
                combineWeeks();
                checkSelectedWeek();

                if (!bClassSelection){
                    for (int i = 0; i < jsnArrClasses.length(); i++)
                        if (jsnArrClasses.optString(i).equals(sSelectedClass))
                            iSelectedClassID = i + 1;
                }

                // check if website was parsed successfully
                if (sSelectedWeek != null && !sSelectedWeek.equals("")){
                    storeParsedData();
                    bParseSuccess = true;
                    setDrawerAdapter();
                    // load timetable on app start
                    loadTimeTable();
                } else {
                    // message for fail
                	toastShort("Fehler 1. Die App wurde beendet!");
                    finish();
                    killApp(1000);
                }
            } else {
                // message for fail
            	toastShort("Fehler 2. Die App wurde beendet!");
                finish();
                killApp(1000);
            }
        }
    }
    
    // make a long toast
    @SuppressWarnings("unused")
    public void toastLong(String sMessage){
        Toast.makeText(this, sMessage, Toast.LENGTH_LONG).show();
    }
    // make a short toast
    public void toastShort(String sMessage){
        Toast.makeText(this, sMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefreshStarted(View view){
        // true to hide loading dialog
        refresh(true);
    }

    public void loadOfflineWebsite(boolean bToast){
        try {
            InputStream streamHTML = getResources().openRawResource(R.raw.offline);
            Reader readerHTML = new BufferedReader(new InputStreamReader(streamHTML,"UTF-8"));

            String html = Functions.readFile(readerHTML);
            webViewForTimeTable.loadDataWithBaseURL("file:///android_res/raw/", html, "text/html", "UTF-8", null);
        } catch (UnsupportedEncodingException e){
            toastShort("Fehler 3 - App wird beendet!");
            finish();
        }

        if (bToast) this.toastShort("Keine Internetverbindung!");
        mPullToRefreshLayout.setRefreshComplete();
    }

    public void loadOfflineData(){
        loadParsedData();
        calculateGoneWeeks();
        combineWeeks();

        if (iSelectedClassID > -1 && jsnArrClasses.length() > 0 && mapAllWeeks.size() > 0){
            checkSelectedWeek();
            bHasOfflineData = true;
        } else {
            bHasOfflineData = false;
        }
    }

    // extended webViewClient
    protected class InsideWebViewClient extends WebViewClient{
        // fires when page loading is done
        @Override
        public void onPageFinished(WebView view, String url){
            mPullToRefreshLayout.setRefreshComplete();

            if (bGetWebsiteContent && bOfflineMode){
                view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('html')[0].outerHTML);");
                bGetWebsiteContent = false;
            }

            super.onPageFinished(view, url);
        }
    }

    public void setWebsiteContentPreferences(String sClass, String sWeek, String sDateTime, String sHtmlContent){
        Set<String> setData = new LinkedHashSet<String>();
        setData.add("0" + sClass);
        setData.add("1" + sWeek);
        setData.add("2" + sDateTime);
        setData.add("3" + sHtmlContent);

        prefsEditor.putStringSet("offlineContent" + sClass + sWeek, setData);
        prefsEditor.commit();
    }

    public void deleteObsoletePrefs(){
        Pattern ptnKey = Pattern.compile("offlineContent(.+)(..)");
        int iWeek = Functions.getCalendarWeek();

        for (String sKey : prefs.getAll().keySet()) {
            Matcher mtrKey = ptnKey.matcher(sKey);

            if (mtrKey.find()) {
                prefsEditor.remove(sKey);
//                int iKeyWeek = Integer.valueOf(mtrKey.group(2));
//
//                if (iKeyWeek < (iWeek - iOldWeeksCount)) {
//                    prefsEditor.remove(sKey);
//                }
            }
        }

        prefsEditor.commit();
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            //displayView(position);
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);

            // get date (day, month, year) from string
            Integer[] iArrDate = Functions.getDateFromString(navDrawerItems.get(position).getTitle());

            // check if selected item contains a date
            if (iArrDate != null){
                Calendar calItem = Calendar.getInstance();
                // month is based 0
                calItem.set(iArrDate[2], iArrDate[1] - 1, iArrDate[0]);
                Integer iWeekItem = calItem.get(Calendar.WEEK_OF_YEAR);
                // get week of year from selected item title
                sSelectedWeek = ((iWeekItem < 10) ? "0" : "") + String.valueOf(iWeekItem);
                refresh(false);
            }

            storeSelectedWeek();
        }
    }

    public void setDrawerAdapter(){
        TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        if (navMenuIcons != null) {
            navDrawerItems = new ArrayList<NavDrawerItem>();

            int iPos = iOldWeeksCount;
            int iCount = 0;

            for (Map.Entry<String, String> entry : mapAllWeeks.entrySet()){
                String sKey = entry.getKey();
                if (sKey.equals(sSelectedWeek)) iPos = iCount;
                int iIcon = (mapGoneWeeks.containsKey(sKey)) ? 1 : 2;
                navDrawerItems.add(new NavDrawerItem(entry.getValue(), navMenuIcons.getResourceId(iIcon, -1), true, "KW" + sKey));
                iCount++;
            }

            // Recycle the typed array
            navMenuIcons.recycle();
            mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

            // setting the nav drawer list adapter
            NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
            mDrawerList.setAdapter(adapter);

            mDrawerList.setItemChecked(iPos, true);
            mDrawerList.setSelection(iPos);
        }
    }

    public void storeSelectedWeek(){
        prefsEditor.putString("selectedWeek", sSelectedWeek);
        prefsEditor.commit();
    }

    public void storeParsedData(){
        if (!bClassSelection){
            prefsEditor.putInt("iSelectedClassID", iSelectedClassID);
        }
        prefsEditor.putString("jsnParsedWeeks", new JSONArray(mapParsedWeeks.entrySet()).toString());
        prefsEditor.putString("jsnClasses", jsnArrClasses.toString());
        prefsEditor.commit();
    }

    public void loadParsedData(){
        try {
            jsnArrClasses = new JSONArray(prefs.getString("jsnClasses", "[]"));
            JSONArray jsnParsedWeeks = new JSONArray(prefs.getString("jsnParsedWeeks", "[]"));
            mapParsedWeeks = new LinkedHashMap<String, String>();
            Pattern ptrSet = Pattern.compile("(.+)=(.+)");

            for (int i = 0; i<jsnParsedWeeks.length(); i++){
                Matcher mtrSet = ptrSet.matcher(jsnParsedWeeks.get(i).toString());
                if (mtrSet.find()) mapParsedWeeks.put(mtrSet.group(1), mtrSet.group(2));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadAppSettings(){
        bShowOld = prefs.getBoolean("bShowOld", false);
        bRefreshOnResume = prefs.getBoolean("bRefreshOnResume", false);
        bZoomWebView = prefs.getBoolean("bZoomWebView", false);
        bOfflineMode = prefs.getBoolean("bOfflineMode", false);
        bRestoreWeek = prefs.getBoolean("bRestoreWeek", false);
        int iSelectedOldWeekCount = prefs.getInt("iSelectedOldWeekCount", 0);
        iSelectedClassID = prefs.getInt("iSelectedClassID", -1);
        sSelectedClass = (bClassSelection) ? prefs.getString("sSelectedClass", "") : sDefaultClass;
        TypedArray sArrOldWeeks = getResources().obtainTypedArray(R.array.spinner_old_count);

        if (bShowOld && sArrOldWeeks != null){
            if (iSelectedOldWeekCount == 0){
                iOldWeeksCount = Functions.getCalendarWeek() - 1;
            } else {
                iOldWeeksCount = Integer.valueOf(sArrOldWeeks.getString(iSelectedOldWeekCount));
            }
        } else {
            iOldWeeksCount = 0;
        }

        if (bClassSelection) {
            prefsEditor.putBoolean("bInitSetup", iSelectedClassID == -1);
            prefsEditor.commit();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        boolean bRefresh = false;

        if (bParseSuccess || (bHasOfflineData && !bIsOnline)) {
            int iOrigClassID = iSelectedClassID;
            int iOrigOldWeeksCount = iOldWeeksCount;
            boolean bOrigShowOld = bShowOld;
            boolean bOrigOfflineMode = bOfflineMode;

            loadAppSettings();
            bRefresh = reloadGUI(iOrigClassID != iSelectedClassID || iSelectedClassID == -1, bOrigShowOld != bShowOld || iOrigOldWeeksCount != iOldWeeksCount, bOrigOfflineMode != bOfflineMode && bOfflineMode);
            /*
            * fix: after rotating in settings and resume the title does not show the "on refreshing" text when refresh is needed
            */
            if (bRefresh) bIsRotating = false;
        } else {
            loadAppSettings();
        }

        if (bRestoreWeek && sSelectedWeek.equals(""))
            sSelectedWeek = prefs.getString("selectedWeek", "");

        /* refresh when:
            bRefreshOnResume --> app come to foreground
            bRefresh --> app settings have changed
            bIsOnStart --> app starts first time
            bIsRotating --> smartphone is rotating
         */
        if (bRefreshOnResume || bRefresh || bIsOnStart || bIsRotating){
            refresh(false);
        }

        bIsOnStart = false;
        bIsRotating = false;
    }

    public boolean reloadGUI(boolean bClassChanged, boolean bShowOldChanged, boolean bOfflineModeChanged){
        boolean bRefreshTimeTable = false;

        if (!bZoomWebView){
            while (webViewForTimeTable.zoomOut()){webViewForTimeTable.zoomOut();}
        }

        webViewForTimeTable.getSettings().setBuiltInZoomControls(bZoomWebView);

        if (bClassChanged){
            bRefreshTimeTable = true;
        }

        if (bShowOldChanged){
            calculateGoneWeeks();
            combineWeeks();

            if (!mapAllWeeks.containsKey(sSelectedWeek)){
                sSelectedWeek = "";
                checkSelectedWeek();
                toastShort("Aktueller Stundeplan wird geladen, da die Auswahl veraltet ist!");
                bRefreshTimeTable = true;
            }

            setDrawerAdapter();
        }

        if (bOfflineModeChanged) {
            bRefreshTimeTable = true;
        }

        return bRefreshTimeTable;
    }

    public void calculateGoneWeeks() {
        mapGoneWeeks = new LinkedHashMap<String, String>();

        if (mapParsedWeeks.size() > 0 && iOldWeeksCount > 0) {
            String sFirstDate = mapParsedWeeks.get(mapParsedWeeks.keySet().iterator().next());

            Pattern ptnWeek = Pattern.compile("(.+?)\\.(.+?)\\.(.+)");
            Matcher mtrWeek = ptnWeek.matcher(sFirstDate);

            if (mtrWeek.find()) {
                Calendar cal = Calendar.getInstance();
                Integer iDay = Integer.valueOf(mtrWeek.group(1));
                Integer iMonth = Integer.valueOf(mtrWeek.group(2)) - 1;
                Integer iYear = Integer.valueOf(mtrWeek.group(3));
                cal.set(iYear, iMonth, iDay);
                cal.add(Calendar.DAY_OF_MONTH, -(7 * iOldWeeksCount));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                for (int i = 0; i < iOldWeeksCount; i++) {
                    String sDateCaption = dateFormat.format(cal.getTime());
                    Integer iCalWeek = cal.get(Calendar.WEEK_OF_YEAR);
                    String sCalWeek = ((iCalWeek < 10) ? "0" : "") + String.valueOf(iCalWeek);
                    mapGoneWeeks.put(sCalWeek, sDateCaption);
                    cal.add(Calendar.DAY_OF_MONTH, 7);
                }
            }
        }
    }

    public void combineWeeks(){
        mapAllWeeks = new LinkedHashMap<String, String>();
        mapAllWeeks.putAll(mapGoneWeeks);
        mapAllWeeks.putAll(mapParsedWeeks);
    }

    public void checkSelectedWeek(){
        if (sSelectedWeek.equals("")) sSelectedWeek = Functions.getStandardActiveWeek();

        if (!mapAllWeeks.containsKey(sSelectedWeek)){
            sSelectedWeek = mapParsedWeeks.keySet().iterator().next();
        }
    }

    public CharSequence getDrawerTitle(){
        List<CharSequence> lTitle = new LinkedList<CharSequence>();

        if (jsnArrClasses.length() > 0 && (bIsOnline || !bIsOnline && bOfflineMode && bHasOfflineData)) {
            String sClass = jsnArrClasses.optString(iSelectedClassID - 1, "");

            if (!sClass.equals("")) {
                lTitle.add(sClass);
            }

            if (!sSelectedWeek.equals(""))
            {
                lTitle.add("KW" + sSelectedWeek);
            }
        }

        if (!bIsOnline && bHasOfflineData){
            lTitle.add("offline");
        }

        if (lTitle.size() > 0) {
            return TextUtils.join(" - ", lTitle);
        } else {
            return "Keine Verbindung möglich!";
        }
    }

    public void runSetup(boolean bResetClass){
        if (bResetClass) prefsEditor.putInt("iSelectedClassID", -1);
        prefsEditor.putBoolean("bInitSetup", true);
        prefsEditor.commit();
        startActivity(new Intent(getApplicationContext(), Settings.class));
    }
}
