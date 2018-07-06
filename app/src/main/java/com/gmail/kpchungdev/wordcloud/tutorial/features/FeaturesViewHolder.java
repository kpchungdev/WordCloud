package com.gmail.kpchungdev.wordcloud.tutorial.features;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.kpchungdev.wordcloud.R;

public class FeaturesViewHolder extends RecyclerView.ViewHolder {

    private ImageView iconImageView;
    private TextView featureTextView;

    public FeaturesViewHolder(View itemView) {
        super(itemView);

        iconImageView = itemView.findViewById(R.id.icon);
        featureTextView = itemView.findViewById(R.id.feature_textview);
    }

    public void bindFeature(Feature feature) {
        iconImageView.setImageDrawable(feature.getIcon());
        featureTextView.setText(feature.getFeature());
    }

}
