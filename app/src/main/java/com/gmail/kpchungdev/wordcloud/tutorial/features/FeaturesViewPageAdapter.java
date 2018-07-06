package com.gmail.kpchungdev.wordcloud.tutorial.features;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.gmail.kpchungdev.wordcloud.tutorial.FragmentEngineer;

import javax.inject.Inject;

public class FeaturesViewPageAdapter extends FragmentPagerAdapter {

    public static final int TEXT_FRAGMENT_POSITION = 0;
    public static final int OTHERS_FRAGMENT_POSITION = 1;

    private FragmentEngineer fragmentEngineer;

    @Inject
    public FeaturesViewPageAdapter(FragmentEngineer fragmentEngineer) {
        super(fragmentEngineer.getFragmentManager());

        this.fragmentEngineer = fragmentEngineer;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        if (position == TEXT_FRAGMENT_POSITION) {
            fragment = fragmentEngineer.getTextFragment();
        } else {
            fragment = fragmentEngineer.getOthersFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title;

        if (position == TEXT_FRAGMENT_POSITION) {
            title = "TEXT";
        } else {
            title = "OTHERS";
        }
        return title;
    }

}
