package com.gmail.kpchungdev.wordcloud.tutorial;

import android.graphics.drawable.Drawable;

import com.gmail.kpchungdev.wordcloud.WordCloudView;
import com.gmail.kpchungdev.wordcloud.tutorial.features.Feature;

import net.objecthunter.exp4j.ExpressionBuilder;

public class WordCloudPresenter implements WordCloudContract.UserActionListener {

    public static final int INDEX_TEXT_EQUATION = 0;
    public static final int INDEX_SKIPS = 1;
    public static final int INDEX_RADIUS = 2;
    public static final int INDEX_SHOW_LOADING = 3;

    public static final int INIT_SHEET_MOVE_DURATION = 0;
    public static final int LOAD_SHEET_MOVE_DURATION = 500;
    public static final int SHEET_LOADED_FINISHED_Y = 0;

    private static final String STATUS_ON = "ON";
    private static final String STATUS_OFF = "OFF";

    private Drawable noShowLoadingFeatureIcon;
    private Drawable showLoadingFeatureIcon;
    private Drawable noShowLoadingIcon;
    private Drawable showLoadingIcon;

    private WordCloudContract.WordCloudView wordCloudFragment;
    private WordCloudContract.FeatureTextView featureTextFragment;
    private WordCloudContract.FeatureOtherView featureOtherFragment;

    public WordCloud wordCloud;

    private Feature[] featuresArray;

    private float marginTextFragmentReveal;

    private ShowWordCloudBoolean shownWordCloud;
    private ViewPagerInitiatedBoolean viewPagerInitiatedBoolean;
    private LoadingWordCloudBoolean loadingWordCloudBoolean;

    public WordCloudPresenter(Drawable noShowLoadingFeatureIcon, Drawable showLoadingFeatureIcon, Drawable noShowLoadingIcon, Drawable showLoadingIcon, WordCloudContract.WordCloudView wordCloudFragment, WordCloudContract.FeatureTextView featureTextFragment, WordCloudContract.FeatureOtherView featureOtherFragment, WordCloud wordCloud, Feature[] featuresArray, float marginTextFragmentReveal, ShowWordCloudBoolean shownWordCloud, ViewPagerInitiatedBoolean viewPagerInitiatedBoolean, LoadingWordCloudBoolean loadingWordCloudBoolean) {
        this.noShowLoadingFeatureIcon = noShowLoadingFeatureIcon;
        this.showLoadingFeatureIcon = showLoadingFeatureIcon;
        this.noShowLoadingIcon = noShowLoadingIcon;
        this.showLoadingIcon = showLoadingIcon;
        this.wordCloudFragment = wordCloudFragment;
        this.featureTextFragment = featureTextFragment;
        this.featureOtherFragment = featureOtherFragment;
        this.wordCloud = wordCloud;
        this.featuresArray = featuresArray;
        this.marginTextFragmentReveal = marginTextFragmentReveal;
        this.shownWordCloud = shownWordCloud;
        this.viewPagerInitiatedBoolean = viewPagerInitiatedBoolean;
        this.loadingWordCloudBoolean = loadingWordCloudBoolean;
    }

    @Override
    public void initWordCloudFragment(int position) {
        if (position == 0) {
            viewPagerInitiatedBoolean.setInitiated(true);
            if (shownWordCloud.isShowWordCloud()) {
                wordCloudFragment.moveSheet(SHEET_LOADED_FINISHED_Y, INIT_SHEET_MOVE_DURATION);
            } else {
                wordCloudFragment.moveSheet(marginTextFragmentReveal, INIT_SHEET_MOVE_DURATION);
            }

        }

        loadingWordCloudBoolean.setLoading(true);
        wordCloudFragment.showWordCloud(wordCloud);
    }

    @Override
    public void initTextFragment() {
        featureTextFragment.showWords(wordCloud.getWords());
        featureTextFragment.showTextEquation(wordCloud.getTextExpression());
        featureTextFragment.showSkips(String.valueOf(wordCloud.getAllowedSkips()));
    }

    @Override
    public void initOthersFragment() {
        featureOtherFragment.showCircleRadius(String.valueOf(wordCloud.getCircleRadius()));

        boolean isLoading = wordCloud.isLoadingShowWords();

        featureOtherFragment.showLoading(isLoading);

        Drawable loadingIcon;
        if (isLoading) {
            loadingIcon = showLoadingIcon;
        } else {
            loadingIcon = noShowLoadingIcon;
        }
        featureOtherFragment.showLoadingIcon(loadingIcon);
    }

    @Override
    public void setWords(String words) {
        wordCloud.setWords(words);
        featureTextFragment.showClearWordsButton(words.length() != 0);
    }

    @Override
    public void clearWords() {
        featureTextFragment.showWords("");
    }

    @Override
    public void setTextSizeEquation(String equation) {
        try {
            new ExpressionBuilder(equation).variable(WordCloudView.REPETITION_VARIABLE).build();
            featuresArray[INDEX_TEXT_EQUATION].setFeature(equation);
        } catch (Exception e){
            featuresArray[INDEX_TEXT_EQUATION].setFeature("repetition + " + WordCloudView.DEFAULT_MINIMUM_TEXT_SIZE);
        }

        wordCloud.setTextExpression(featuresArray[INDEX_TEXT_EQUATION].getFeature());
    }

    @Override
    public void setAllowedSkips(int skips) {
        wordCloud.setAllowedSkips(skips);
        featuresArray[INDEX_SKIPS].setFeature(String.valueOf(skips));
    }

    @Override
    public void setCircleRadius(int radius) {
        wordCloud.setCircleRadius(radius);
        featuresArray[INDEX_RADIUS].setFeature(String.valueOf(radius));
    }

    @Override
    public void showWordsLoading(boolean showWordsLoading) {
        wordCloud.setLoadingShowWords(showWordsLoading);

        Drawable loadingFeatureIcon;
        Drawable loadingIcon;
        String status;
        if (showWordsLoading) {
            loadingFeatureIcon = showLoadingFeatureIcon;
            loadingIcon = showLoadingIcon;
            status = STATUS_ON;
        } else {
            loadingFeatureIcon = noShowLoadingFeatureIcon;
            loadingIcon = noShowLoadingIcon;
            status = STATUS_OFF;
        }

        Feature showLoading = featuresArray[INDEX_SHOW_LOADING];
        showLoading.setIcon(loadingFeatureIcon);
        showLoading.setFeature(status);

        featureOtherFragment.showLoadingIcon(loadingIcon);
    }

    @Override
    public void loadWordCloud() {
        clearAllFocusAndKeyboard();
        loadingWordCloudBoolean.setLoading(true);
        shownWordCloud.setShowWordCloud(true);
        wordCloudFragment.moveSheet(SHEET_LOADED_FINISHED_Y, LOAD_SHEET_MOVE_DURATION);
        wordCloudFragment.showWordCloud(wordCloud);
    }

    @Override
    public void clearAllFocusAndKeyboard() {
        featureOtherFragment.clearFocusAndKeyboard();
        featureTextFragment.clearFocusAndKeyboard();
    }

    @Override
    public void wordCloudDoneLoading() {
        loadingWordCloudBoolean.setLoading(false);
        wordCloudFragment.wordCloudDoneLoading();
    }
}
