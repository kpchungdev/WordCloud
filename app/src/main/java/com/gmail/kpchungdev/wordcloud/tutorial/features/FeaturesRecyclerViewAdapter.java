package com.gmail.kpchungdev.wordcloud.tutorial.features;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.kpchungdev.wordcloud.R;

public class FeaturesRecyclerViewAdapter extends RecyclerView.Adapter<FeaturesViewHolder>{

    private Feature[] featuresArray;

    public FeaturesRecyclerViewAdapter(Feature[] featuresArray) {
        this.featuresArray = featuresArray;
    }

    @NonNull
    @Override
    public FeaturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_features, parent, false);

        return new FeaturesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturesViewHolder holder, int position) {
        holder.bindFeature(featuresArray[position]);
    }

    @Override
    public int getItemCount() {
        return featuresArray.length;
    }
}
