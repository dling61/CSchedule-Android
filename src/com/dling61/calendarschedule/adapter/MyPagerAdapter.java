/**
 * Develop by Antking
 * */
package com.dling61.calendarschedule.adapter;

import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * @class MyPagerAdapter
 * @author Huyen Nguyen
 * @version 1.0
 * @Date April 8th,2014 @ This class get FragmentPagerAdapter for TabActivity
 * */
public class MyPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
