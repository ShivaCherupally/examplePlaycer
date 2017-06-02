package in.playcer.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.playcer.MyBooksSlotsListFragment;
import in.playcer.ParticipateListFragment;
import in.playcer.R;
import in.playcer.SportsCafeNewsListFragment;
import in.playcer.SportsListFragment;
import in.playcer.utilities.AppDataBaseHelper;

/**
 * Created by HARIKRISHNA on 02/20/2016. at CaretTech
 */

public class DashboardTabPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private List<String> titles;

    public DashboardTabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        titles = new ArrayList<>();
        fragments = new ArrayList<>();
        titles.add(context.getString(R.string.tab_sports));
        fragments.add(SportsListFragment.newInstance(""));

        AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(context);
        String role = dataBaseHelper.getRegisteredRole();
        if (role != null && role.equalsIgnoreCase("manager")) {
            titles.add(context.getString(R.string.nav_item_My_bookings));
            fragments.add(MyBooksSlotsListFragment.newInstance());
        } else {
            titles.add(context.getString(R.string.tab_participate));
            titles.add(context.getString(R.string.tab_sports_cafe));

            fragments.add(ParticipateListFragment.newInstance(""));
            fragments.add(SportsCafeNewsListFragment.newInstance(""));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}