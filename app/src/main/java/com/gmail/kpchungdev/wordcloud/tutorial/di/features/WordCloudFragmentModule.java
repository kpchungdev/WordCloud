package com.gmail.kpchungdev.wordcloud.tutorial.di.features;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.gmail.kpchungdev.wordcloud.MainActivity;
import com.gmail.kpchungdev.wordcloud.tutorial.FragmentEngineer;
import com.gmail.kpchungdev.wordcloud.tutorial.ShowWordCloudBoolean;
import com.gmail.kpchungdev.wordcloud.tutorial.ViewPagerInitiatedBoolean;
import com.gmail.kpchungdev.wordcloud.tutorial.ViewPagerPosition;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudContract;
import com.gmail.kpchungdev.wordcloud.tutorial.features.Feature;
import com.gmail.kpchungdev.wordcloud.tutorial.features.FeaturesRecyclerViewAdapter;
import com.gmail.kpchungdev.wordcloud.tutorial.features.FeaturesViewPageAdapter;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter.INIT_SHEET_MOVE_DURATION;
import static com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter.LOAD_SHEET_MOVE_DURATION;
import static com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter.SHEET_LOADED_FINISHED_Y;
import static com.gmail.kpchungdev.wordcloud.tutorial.features.FeaturesViewPageAdapter.OTHERS_FRAGMENT_POSITION;
import static com.gmail.kpchungdev.wordcloud.tutorial.features.FeaturesViewPageAdapter.TEXT_FRAGMENT_POSITION;

@Module
public class WordCloudFragmentModule {

    private MainActivity mainActivity;
    private WordCloudContract.UserActionListener presenter;
    private AnimatorSet animatorSet;
    private AccelerateDecelerateInterpolator interpolator;
    private FragmentEngineer fragmentEngineer;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CardView wordCloudCardView;
    private Feature[] featureArray;
    private ShowWordCloudBoolean shownWordCloud;
    private ViewPagerInitiatedBoolean viewPagerInitiatedBoolean;
    private float marginTextFragmentReveal;
    private float marginOthersFragmentReveal;
    private ViewPagerPosition viewPagerPosition;

    public WordCloudFragmentModule(MainActivity mainActivity,
                                   WordCloudContract.UserActionListener presenter,
                                   AnimatorSet animatorSet,
                                   AccelerateDecelerateInterpolator interpolator,
                                   FragmentEngineer fragmentEngineer,
                                   ViewPager viewPager, TabLayout tabLayout,
                                   CardView wordCloudCardView,
                                   Feature[] featureArray,
                                   ShowWordCloudBoolean shownWordCloud,
                                   ViewPagerInitiatedBoolean viewPagerInitiatedBoolean,
                                   float marginTextFragmentReveal,
                                   float marginOthersFragmentReveal,
                                   ViewPagerPosition viewPagerPosition) {
        this.mainActivity = mainActivity;
        this.presenter = presenter;
        this.animatorSet = animatorSet;
        this.interpolator = interpolator;
        this.fragmentEngineer = fragmentEngineer;
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
        this.wordCloudCardView = wordCloudCardView;
        this.featureArray = featureArray;
        this.shownWordCloud = shownWordCloud;
        this.viewPagerInitiatedBoolean = viewPagerInitiatedBoolean;
        this.marginTextFragmentReveal = marginTextFragmentReveal;
        this.marginOthersFragmentReveal = marginOthersFragmentReveal;
        this.viewPagerPosition = viewPagerPosition;
    }

    @Singleton
    @Provides
    public FeaturesViewPageAdapter provideFeaturesPageAdapter() {
        return new FeaturesViewPageAdapter(fragmentEngineer);
    }

    @Singleton
    @Provides
    public LinearLayoutManager provideLinearLayoutManager() {
        return new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
    }

    @Singleton
    @Provides
    public FeaturesRecyclerViewAdapter provideFeaturesRecyclerViewAdapter() {
        return new FeaturesRecyclerViewAdapter(featureArray);
    }

    @Singleton
    @Provides
    public DisplayMetrics provideDisplayMetrics() {
        return new DisplayMetrics();
    }

    @Singleton
    @Provides
    @Named("screen_height")
    public float provideScreenHeight(DisplayMetrics displayMetrics) {
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Singleton
    @Provides
    public TabLayout.OnTabSelectedListener provideTabListener() {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (viewPagerInitiatedBoolean.isInitiated()) {
                    shownWordCloud.setShowWordCloud(false);
                }
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };
    }

    @Singleton
    @Provides
    public ViewPager.SimpleOnPageChangeListener providePageListener() {
        return new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tabLayout.getTabAt(position);

                if (tab != null) {
                    if (viewPagerInitiatedBoolean.isInitiated()) {
                        shownWordCloud.setShowWordCloud(false);
                    }
                    tab.select();
                    moveSheetTabbing(position);
                }
            }
        };
    }

    @Singleton
    @Provides
    public View.OnClickListener provideOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clearAllFocusAndKeyboard();
                shownWordCloud.setShowWordCloud(!shownWordCloud.isShowWordCloud());
                moveSheetTabbing(viewPagerPosition.getPosition());
            }
        };
    }

    private void moveSheetTabbing(int position) {
        final float translateYText = marginTextFragmentReveal;
        final float translateYOther = marginOthersFragmentReveal;
        int duration = LOAD_SHEET_MOVE_DURATION;
        float translateY;

        viewPagerPosition.setPosition(position);

        animatorSet.removeAllListeners();
        animatorSet.end();
        animatorSet.cancel();

        if (shownWordCloud.isShowWordCloud()) {
            translateY = SHEET_LOADED_FINISHED_Y;
            if (!viewPagerInitiatedBoolean.isInitiated()) {
                viewPagerInitiatedBoolean.setInitiated(true);
                duration = INIT_SHEET_MOVE_DURATION;
            }
        } else {
            if (position == TEXT_FRAGMENT_POSITION) {
                translateY = translateYText;
            } else if (position == OTHERS_FRAGMENT_POSITION) {
                if (!viewPagerInitiatedBoolean.isInitiated()) {
                    viewPagerInitiatedBoolean.setInitiated(true);
                    duration = INIT_SHEET_MOVE_DURATION;
                }
                translateY = translateYOther;
            } else {
                position = viewPager.getCurrentItem();
                if (position == TEXT_FRAGMENT_POSITION) {
                    translateY = translateYText;
                } else {
                    translateY = translateYOther;
                }
            }
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(wordCloudCardView, "translationY", translateY);

        animator.setDuration(duration);
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        animatorSet.play(animator);
        animator.start();
    }

}
