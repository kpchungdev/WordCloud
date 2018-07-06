package com.gmail.kpchungdev.wordcloud.tutorial.features;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.gmail.kpchungdev.wordcloud.MainActivity;
import com.gmail.kpchungdev.wordcloud.R;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudContract;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter;
import com.gmail.kpchungdev.wordcloud.tutorial.di.Injectable;
import com.gmail.kpchungdev.wordcloud.tutorial.di.features.DaggerFeaturesComponent;
import com.gmail.kpchungdev.wordcloud.tutorial.di.features.FeaturesModule;

import javax.inject.Inject;
import javax.inject.Named;

public class OthersFragment extends Fragment implements Injectable, WordCloudContract.FeatureOtherView {

    @Inject
    @Named("circle_radius")
    TextWatcher radiusTextWatcher;

    @Inject
    @Named("wordcloud_button")
    View.OnClickListener wordCloudButton;

    @Inject
    MainActivity mainActivity;

    @Inject
    WordCloudPresenter presenter;

    private ImageView loadingIcon;
    private EditText radiusEditText;
    private SwitchCompat showWordsSwitch;

    InputMethodManager inputMethodManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feature_others, container, false);

        inputMethodManager = DaggerFeaturesComponent
                .builder()
                .featuresModule(new FeaturesModule(mainActivity))
                .build()
                .getInputMethodManager();

        loadingIcon = view.findViewById(R.id.loading_icon_imageview);

        radiusEditText = view.findViewById(R.id.radius_edittext);
        radiusEditText.addTextChangedListener(radiusTextWatcher);

        showWordsSwitch = view.findViewById(R.id.loading_show_words_switch);
        showWordsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.showWordsLoading(isChecked);
            }
        });

        ImageView wordCloudImageButton = view.findViewById(R.id.word_cloud_button);
        wordCloudImageButton.setOnClickListener(wordCloudButton);

        presenter.initOthersFragment();

        return view;
    }

    @Override
    public void showLoadingIcon(Drawable drawable) {
        loadingIcon.setImageDrawable(drawable);
    }

    @Override
    public void showLoading(boolean showLoading) {
        showWordsSwitch.setChecked(showLoading);
    }

    @Override
    public void showCircleRadius(String radius) {
        radiusEditText.setText(radius);
    }

    @Override
    public void clearFocusAndKeyboard() {
        if (radiusEditText.hasFocus()) {
            radiusEditText.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(radiusEditText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }
}
