package com.gmail.kpchungdev.wordcloud.tutorial.di.features;

import android.view.inputmethod.InputMethodManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {FeaturesModule.class})
public interface FeaturesComponent {

    InputMethodManager getInputMethodManager();

}
