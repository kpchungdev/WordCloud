package com.gmail.kpchungdev.wordcloud.tutorial;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.gmail.kpchungdev.wordcloud.MainActivity;
import com.gmail.kpchungdev.wordcloud.R;
import com.gmail.kpchungdev.wordcloud.tutorial.features.OthersFragment;
import com.gmail.kpchungdev.wordcloud.tutorial.features.TextFragment;

import javax.inject.Inject;

public class FragmentEngineer {
    public static final String TAG_WORDCLOUD_FRAGMENT = "WordCloudFragment";

    private final int fragmentContainer;

    private WordCloudFragment mWordCloudFragment;
    private TextFragment textFragment;
    private OthersFragment othersFragment;

    private FragmentManager fragmentManager;

    @Inject
    public FragmentEngineer(MainActivity mainActivity,
                            WordCloudFragment wordCloudFragment,
                            TextFragment textFragment,
                            OthersFragment othersFragment) {
        this.fragmentManager = mainActivity.getSupportFragmentManager();
        this.fragmentContainer = R.id.fragment_container;
        this.mWordCloudFragment = wordCloudFragment;
        this.textFragment = textFragment;
        this.othersFragment = othersFragment;
    }

    public void insertWordCloudFragment() {
        fragmentManager.beginTransaction()
                .replace(fragmentContainer, mWordCloudFragment, TAG_WORDCLOUD_FRAGMENT)
                .commit();
    }

    public Fragment getTextFragment() {
        return textFragment;
    }

    public Fragment getOthersFragment() {
        return othersFragment;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

}
