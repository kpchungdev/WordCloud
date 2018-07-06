package com.gmail.kpchungdev.wordcloud.tutorial;

import android.graphics.drawable.Drawable;

public interface WordCloudContract {

    interface WordCloudView {
        void showWordCloud(WordCloud wordCloud);
        void moveSheet(float translateY, int duration);
        void wordCloudDoneLoading();
    }

    interface FeatureTextView {
        void setWords(String words);
        void showWords(String words);
        void showClearWordsButton(boolean show);
        void showTextEquation(String textEquation);
        void showSkips(String skips);
        void clearFocusAndKeyboard();
    }

    interface FeatureOtherView {
        void showLoadingIcon(Drawable loadingIcon);
        void showLoading(boolean showLoading);
        void showCircleRadius(String radius);
        void clearFocusAndKeyboard();
    }


    interface UserActionListener {
        void initWordCloudFragment(int position);
        void initTextFragment();
        void initOthersFragment();
        void setWords(String words);
        void clearWords();
        void setTextSizeEquation(String equation);
        void setAllowedSkips(int skips);
        void setCircleRadius(int radius);
        void showWordsLoading(boolean showWordsLoading);
        void loadWordCloud();
        void clearAllFocusAndKeyboard();
        void wordCloudDoneLoading();
    }

}
