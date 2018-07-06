package com.gmail.kpchungdev.wordcloud.tutorial.di.features;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.gmail.kpchungdev.wordcloud.tutorial.features.FeaturesRecyclerViewAdapter;
import com.gmail.kpchungdev.wordcloud.tutorial.features.FeaturesViewPageAdapter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {WordCloudFragmentModule.class})
public interface WordCloudFragmentComponent {

    FeaturesViewPageAdapter getFeaturesPageAdapter();

    LinearLayoutManager getLinearLayoutManager();

    FeaturesRecyclerViewAdapter getFeaturesRecyclerViewAdapter();

    TabLayout.OnTabSelectedListener getTabListener();

    ViewPager.SimpleOnPageChangeListener getPageListener();

    View.OnClickListener getNavigationListener();

}
