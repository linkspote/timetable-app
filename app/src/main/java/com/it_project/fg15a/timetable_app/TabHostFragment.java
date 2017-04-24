package com.it_project.fg15a.timetable_app;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabHostFragment extends Fragment {

    public TabLayout tlFragmentTabHost;
    public ViewPager vwpFragmentTabHost;
    public static int iTabs = 6;

    public TabHostFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate fragment_tab_host and setup views
        View vwRoot = inflater.inflate(R.layout.fragment_tab_host, null);
        tlFragmentTabHost = (TabLayout) vwRoot.findViewById(R.id.tlFragmentTabHost);
        vwpFragmentTabHost = (ViewPager) vwRoot.findViewById(R.id.vwpFragmentTabHost);

        // Set adapter of the view pager
        vwpFragmentTabHost.setAdapter(new SectionsPagerAdapter(getChildFragmentManager()));

        /*
         * This is a workaround!
         * The function setupWithViewPager doesn't work without the Runnable.
         * Could be caused by a Support Library Bug.
         * TODO: Test if still necessary
         */
        tlFragmentTabHost.post(new Runnable() {
            @Override
            public void run() {
                tlFragmentTabHost.setupWithViewPager(vwpFragmentTabHost);
            }
        });

        // Inflate the layout for given fragment
        return vwRoot;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Returns the fragment for the specified position.
        // getItem is called to instantiate the fragment for the given page.
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 : return new BlankFragment();
                case 1 : return new BlankFragment();
                case 2 : return new BlankFragment();
                case 3 : return new BlankFragment();
                case 4 : return new BlankFragment();
                case 5 : return new BlankFragment();
            }
            return null;
        }

        // Show 6 total pages.
        @Override
        public int getCount() {
            return iTabs;
        }

        // This method returns the title of the tab according to its position.
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.section_monday);
                case 1:
                    return getString(R.string.section_tuesday);
                case 2:
                    return getString(R.string.section_wednesday);
                case 3:
                    return getString(R.string.section_thursday);
                case 4:
                    return getString(R.string.section_friday);
                case 5:
                    return getString(R.string.section_saturday);
            }
            return null;
        }
    }

}
