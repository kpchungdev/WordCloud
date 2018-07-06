package com.gmail.kpchungdev.wordcloud.tutorial;

import com.gmail.kpchungdev.wordcloud.WordCloudView;

public class WordCloud {

    private String words;
    private String textExpression;

    private int circleRadius;

    private int allowedSkips;

    private boolean loadingShowWords;

    public WordCloud(String defaultWords) {
        words = defaultWords;
        textExpression = "repetition + " + WordCloudView.DEFAULT_MINIMUM_TEXT_SIZE;
        circleRadius = 0;
        allowedSkips = WordCloudView.DEFAULT_ALLOWED_SKIPS;
        loadingShowWords = false;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getTextExpression() {
        return textExpression;
    }

    public void setTextExpression(String textExpression) {
        this.textExpression = textExpression;
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public int getAllowedSkips() {
        return allowedSkips;
    }

    public void setAllowedSkips(int allowedSkips) {
        this.allowedSkips = allowedSkips;
    }

    public boolean isLoadingShowWords() {
        return loadingShowWords;
    }

    public void setLoadingShowWords(boolean loadingShowWords) {
        this.loadingShowWords = loadingShowWords;
    }
}
