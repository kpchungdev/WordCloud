package com.gmail.kpchungdev.wordcloud.tutorial.di;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.gmail.kpchungdev.wordcloud.R;
import com.gmail.kpchungdev.wordcloud.WordCloudView;
import com.gmail.kpchungdev.wordcloud.tutorial.LoadingWordCloudBoolean;
import com.gmail.kpchungdev.wordcloud.tutorial.ShowWordCloudBoolean;
import com.gmail.kpchungdev.wordcloud.tutorial.ViewPagerInitiatedBoolean;
import com.gmail.kpchungdev.wordcloud.tutorial.ViewPagerPosition;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudFragment;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloud;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter;
import com.gmail.kpchungdev.wordcloud.tutorial.features.Feature;
import com.gmail.kpchungdev.wordcloud.tutorial.features.OthersFragment;
import com.gmail.kpchungdev.wordcloud.tutorial.features.TextFragment;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter.INDEX_RADIUS;
import static com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter.INDEX_SHOW_LOADING;
import static com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter.INDEX_SKIPS;
import static com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter.INDEX_TEXT_EQUATION;

@Module
public class WordCloudModule {

    @Provides
    @Singleton
    public AnimatorSet provideAnimatorSet() {
        return new AnimatorSet();
    }

    @Provides
    @Singleton
    public AccelerateDecelerateInterpolator provideInterpolator() {
        return new AccelerateDecelerateInterpolator();
    }

    @Singleton
    @Provides
    public ViewPagerPosition provideViewPagerPosition() {
        return new ViewPagerPosition(0);
    }

    @Singleton
    @Provides
    @Named("dip_to_pixel")
    public float getDIPToPixelScale(Resources resources) {
        return resources.getDisplayMetrics().density;
    }

    @Provides
    @Singleton
    @Named("reveal_margin_others")
    public float provideMarginOthersFragment(Resources resources) {
        return resources.getDimensionPixelSize(R.dimen.wordcloud_reveal_other_feature);
    }

    @Singleton
    @Provides
    @Named("default_minimum_text_pixel")
    public int provideDefaultMinimumTextSize(@Named("dip_to_pixel") float dipToPixel) {
        return (int) (WordCloudView.DEFAULT_MINIMUM_TEXT_SIZE * dipToPixel);
    }

    @Singleton
    @Provides
    @Named("default_words")
    public String provideDefaultWords(Resources resources) {
        return resources.getString(R.string.example);
    }

    @Singleton
    @Provides
    @Named("no_show_loading_feature_icon")
    public Drawable provideNoShowLoadingFeatureIcon(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.loading_no_show_words_feature_icon);
    }

    @Singleton
    @Provides
    @Named("show_loading_feature_icon")
    public Drawable provideShowLoadingFeatureIcon(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.loading_no_show_words_feature_icon);
    }

    @Singleton
    @Provides
    @Named("no_show_loading_icon")
    public Drawable provideNoShowLoadingIcon(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.loading_no_show_words_icon);
    }

    @Singleton
    @Provides
    @Named("show_loading_icon")
    public Drawable provideShowLoadingIcon(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.loading_show_words_icon);
    }

    @Singleton
    @Provides
    @Named("text_size_equation_icon")
    public Drawable provideTextSizeEquation(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.text_size_equation_feature_icon);
    }

    @Singleton
    @Provides
    @Named("skip_icon")
    public Drawable provideSkipsIcon(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.skips_feature_icon);
    }

    @Singleton
    @Provides
    @Named("radius_icon")
    public Drawable provideRadiusIcon(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.radius_feature_icon);
    }

    @Singleton
    @Provides
    public WordCloudFragment provideWordCloudFragment() {
        return new WordCloudFragment();
    }

    @Singleton
    @Provides
    public TextFragment provideTextFragment() {
        return new TextFragment();
    }

    @Singleton
    @Provides
    public OthersFragment provideOtherFragment() {
        return new OthersFragment();
    }

    @Provides
    @Singleton
    public WordCloud provideWordCloud(@Named("default_words") String defaultWords) {
        return new WordCloud(defaultWords);
    }

    @Singleton
    @Provides
    public Feature[] provideFeaturesArray(@Named("text_size_equation_icon") Drawable textSizeEquationIcon,
                                          @Named("skip_icon") Drawable skipIcon,
                                          @Named("no_show_loading_feature_icon") Drawable noShowLoadingIcon,
                                          @Named("radius_icon") Drawable radiusIcon) {
        Feature[] featureArray = new Feature[4];

        featureArray[INDEX_TEXT_EQUATION] = new Feature(textSizeEquationIcon, "repetition + " + WordCloudView.DEFAULT_MINIMUM_TEXT_SIZE);
        featureArray[INDEX_SKIPS] = new Feature(skipIcon, "3");
        featureArray[INDEX_SHOW_LOADING] = new Feature(noShowLoadingIcon, "OFF");
        featureArray[INDEX_RADIUS] = new Feature(radiusIcon, "0");
        return featureArray;
    }

    @Provides
    @Singleton
    @Named("reveal_margin_text")
    public float provideMarginTextFragment(Resources resources) {
        return resources.getDimensionPixelSize(R.dimen.wordcloud_reveal_text_feature);
    }

    @Provides
    @Singleton
    public ShowWordCloudBoolean provideShownWordCloud() {
        return new ShowWordCloudBoolean(false);
    }

    @Singleton
    @Provides
    public ViewPagerInitiatedBoolean provideViewPagerInitBoolean() {
        return new ViewPagerInitiatedBoolean(false);
    }

    @Provides
    @Singleton
    public LoadingWordCloudBoolean provideLoadingWordCloudBoolean() {
        return new LoadingWordCloudBoolean(false);
    }

    @Singleton
    @Provides
    public WordCloudPresenter providePresenter(@Named("no_show_loading_feature_icon") Drawable noShowLoadingFeatureIcon,
                                                                 @Named("show_loading_feature_icon") Drawable showLoadingFeatureIcon,
                                                                 @Named("no_show_loading_icon") Drawable noShowLoadingIcon,
                                                                 @Named("show_loading_icon") Drawable showLoadingIcon,
                                                                 WordCloudFragment wordCloudFragment,
                                                                 TextFragment textFragment,
                                                                 OthersFragment othersFragment,
                                                                 WordCloud wordCloud,
                                                                 Feature[] featuresArray,
                                                                 @Named("reveal_margin_text") float revealTextFragmentMargin,
                                                                 ShowWordCloudBoolean shownWordCloud,
                                                                 ViewPagerInitiatedBoolean ViewPagerInitiatedBoolean,
                                                                 LoadingWordCloudBoolean loadingWordCloudBoolean) {
        return new WordCloudPresenter(noShowLoadingFeatureIcon, showLoadingFeatureIcon, noShowLoadingIcon, showLoadingIcon, wordCloudFragment, textFragment, othersFragment, wordCloud, featuresArray, revealTextFragmentMargin, shownWordCloud, ViewPagerInitiatedBoolean, loadingWordCloudBoolean);
    }

}
