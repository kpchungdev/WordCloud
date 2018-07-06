package com.gmail.kpchungdev.wordcloud.tutorial.di;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModule {

    @Provides
    @Singleton
    @Named("circle_radius")
    public TextWatcher provideCircleRadiusTextWatcher(final WordCloudPresenter presenter) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int circleRadius = 0;

                if (s.length() > 0) {
                    circleRadius = Integer.valueOf(s.toString());
                }
                presenter.setCircleRadius(circleRadius);
            }
        };
    }

    @Provides
    @Singleton
    @Named("wordcloud_button")
    public View.OnClickListener provideWordCloudButtonOnClickListener(final WordCloudPresenter presenter) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.loadWordCloud();
            }
        };
    }

    @Provides
    @Singleton
    @Named("words")
    public TextWatcher provideWordsTextWatcher(final WordCloudPresenter presenter) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.setWords(s.toString());
            }
        };
    }

    @Provides
    @Singleton
    @Named("text_size_equation")
    public TextWatcher provideTextEquationTextWatcher(final WordCloudPresenter presenter) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.setTextSizeEquation(s.toString());
            }
        };
    }

    @Provides
    @Singleton
    @Named("skips")
    public TextWatcher provideSkipsTextWatcher(final WordCloudPresenter presenter) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int skips = 0;

                if (s.length() > 0) {
                    skips = Integer.valueOf(s.toString());
                }
                presenter.setAllowedSkips(skips);
            }
        };
    }

    @Provides
    @Singleton
    @Named("clear_words")
    public View.OnClickListener provideClearWordsListener(final WordCloudPresenter presenter) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clearWords();
            }
        };
    }

}
