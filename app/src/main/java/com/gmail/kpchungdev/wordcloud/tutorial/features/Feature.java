package com.gmail.kpchungdev.wordcloud.tutorial.features;

import android.graphics.drawable.Drawable;

public class Feature {

    private Drawable icon;

    private String feature;

    public Feature(Drawable icon, String feature) {
        this.icon = icon;
        this.feature = feature;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

}
