package com.gmail.kpchungdev.wordcloud.tutorial.di.features;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.gmail.kpchungdev.wordcloud.MainActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FeaturesModule {

    private MainActivity mainActivity;

    public FeaturesModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    @Singleton
    public InputMethodManager provideInputMethodManager() {
        return (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

}
