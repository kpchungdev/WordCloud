package com.gmail.kpchungdev.wordcloud.tutorial.di;

import com.gmail.kpchungdev.wordcloud.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = {FragmentsModule.class})
    abstract MainActivity contributeMainActivity();

}
