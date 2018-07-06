package com.gmail.kpchungdev.wordcloud.tutorial;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;

import com.gmail.kpchungdev.wordcloud.MainActivity;
import com.gmail.kpchungdev.wordcloud.R;
import com.gmail.kpchungdev.wordcloud.WordCloudView;
import com.gmail.kpchungdev.wordcloud.tutorial.di.Injectable;
import com.gmail.kpchungdev.wordcloud.tutorial.di.features.DaggerWordCloudFragmentComponent;
import com.gmail.kpchungdev.wordcloud.tutorial.di.features.WordCloudFragmentComponent;
import com.gmail.kpchungdev.wordcloud.tutorial.di.features.WordCloudFragmentModule;
import com.gmail.kpchungdev.wordcloud.tutorial.features.Feature;
import com.gmail.kpchungdev.wordcloud.tutorial.features.FeaturesRecyclerViewAdapter;
import com.gmail.kpchungdev.wordcloud.tutorial.features.FeaturesViewPageAdapter;

import javax.inject.Inject;
import javax.inject.Named;

public class WordCloudFragment extends Fragment implements Injectable, WordCloudContract.WordCloudView {
    @Inject
    public FragmentEngineer fragmentEngineer;

    @Inject
    public MainActivity mainActivity;

    @Inject
    public AnimatorSet animatorSet;

    @Inject
    public AccelerateDecelerateInterpolator interpolator;

    @Inject
    public Resources resources;

    @Inject
    public WordCloudPresenter presenter;

    @Inject
    public Feature[] featuresArray;

    @Inject
    @Named("reveal_margin_text")
    public float revealTextFragmentMargin;

    @Inject
    @Named("reveal_margin_others")
    public float revealOthersFragmentMargin;

    public CardView sheet;
    public Toolbar toolbar;
    public WordCloudView wordCloudView;

    public WordCloudFragmentComponent wordCloudFragmentComponent;

    public ViewPager viewPager;
    public FeaturesRecyclerViewAdapter featuresRecyclerViewAdapter;
    public ProgressBar progressBar;

    @Inject
    public ViewPagerPosition viewPagerPosition;

    @Inject
    public ShowWordCloudBoolean shownWordCloud;

    @Inject
    public ViewPagerInitiatedBoolean viewPagerInitiatedBoolean;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wordcloud, container, false);

        viewPagerInitiatedBoolean.setInitiated(false);

        wordCloudView = view.findViewById(R.id.word_cloudview);
        viewPager = view.findViewById(R.id.pager);
        sheet = view.findViewById(R.id.wordcloud_cardview);
        progressBar = view.findViewById(R.id.progressbar);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        wordCloudFragmentComponent = DaggerWordCloudFragmentComponent
                .builder()
                .wordCloudFragmentModule(new WordCloudFragmentModule(mainActivity, presenter, animatorSet, interpolator, fragmentEngineer, viewPager, tabLayout, sheet, featuresArray, shownWordCloud, viewPagerInitiatedBoolean, revealTextFragmentMargin, revealOthersFragmentMargin, viewPagerPosition))
                .build();

        setUpToolbar(view);

        TabLayout.OnTabSelectedListener tabSelectedListener = wordCloudFragmentComponent.getTabListener();
        tabLayout.addOnTabSelectedListener(tabSelectedListener);

        FeaturesViewPageAdapter featuresViewPageAdapter = wordCloudFragmentComponent
                .getFeaturesPageAdapter();
        ViewPager.SimpleOnPageChangeListener pageListener = wordCloudFragmentComponent
                .getPageListener();

        viewPager.setAdapter(featuresViewPageAdapter);
        viewPager.addOnPageChangeListener(pageListener);
        viewPager.setCurrentItem(viewPagerPosition.getPosition());

        LinearLayoutManager linearLayoutManager = wordCloudFragmentComponent
                .getLinearLayoutManager();

        featuresRecyclerViewAdapter = wordCloudFragmentComponent
                .getFeaturesRecyclerViewAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.features_recyclerview);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(featuresRecyclerViewAdapter);

        wordCloudView.setPresenter(presenter);
        presenter.initWordCloudFragment(viewPagerPosition.getPosition());

        return view;
    }

    private void setUpToolbar(View view) {
        toolbar = view.findViewById(R.id.app_bar);
        mainActivity.setSupportActionBar(toolbar);

        View.OnClickListener navigationListener = wordCloudFragmentComponent.getNavigationListener();
        toolbar.setNavigationOnClickListener(navigationListener);
    }


    @Override
    public void showWordCloud(WordCloud wordCloud) {
        featuresRecyclerViewAdapter.notifyDataSetChanged();

        if (wordCloud.isLoadingShowWords()) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        wordCloudView.setTextSizeExpression(wordCloud.getTextExpression());
        wordCloudView.setAllowedSkips(wordCloud.getAllowedSkips());
        wordCloudView.setLoadingShowWords(wordCloud.isLoadingShowWords());
        wordCloudView.setCircleRadius(wordCloud.getCircleRadius());
        wordCloudView.setWords(wordCloud.getWords());
        wordCloudView.loadWordCloud();
    }

    public void moveSheet(float translateY, int duration) {
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            animatorSet.end();
            animatorSet.cancel();

            ObjectAnimator animator = ObjectAnimator.ofFloat(sheet, "translationY", translateY);
            animator.setDuration(duration);
            if (interpolator != null) {
                animator.setInterpolator(interpolator);
            }
            animatorSet.play(animator);
            animator.start();
        }
    }

    public void wordCloudDoneLoading() {
        progressBar.setVisibility(View.GONE);
    }
}
