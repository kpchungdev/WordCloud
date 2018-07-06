package com.gmail.kpchungdev.wordcloud.tutorial.di;

import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudFragment;
import com.gmail.kpchungdev.wordcloud.tutorial.features.OthersFragment;
import com.gmail.kpchungdev.wordcloud.tutorial.features.TextFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector
    abstract WordCloudFragment contributeWordCloudFragment();

    @ContributesAndroidInjector
    abstract TextFragment contributeTextFragment();

    @ContributesAndroidInjector
    abstract OthersFragment contributeOtherFragment();

}
