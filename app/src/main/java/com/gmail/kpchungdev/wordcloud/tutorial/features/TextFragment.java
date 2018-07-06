package com.gmail.kpchungdev.wordcloud.tutorial.features;

import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gmail.kpchungdev.wordcloud.MainActivity;
import com.gmail.kpchungdev.wordcloud.R;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudContract;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter;
import com.gmail.kpchungdev.wordcloud.tutorial.di.Injectable;
import com.gmail.kpchungdev.wordcloud.tutorial.di.features.DaggerFeaturesComponent;
import com.gmail.kpchungdev.wordcloud.tutorial.di.features.FeaturesModule;

import javax.inject.Inject;
import javax.inject.Named;

public class TextFragment extends Fragment implements Injectable, WordCloudContract.FeatureTextView {

    @Inject
    @Named("words")
    public TextWatcher wordsTextWatcher;

    @Inject
    @Named("text_size_equation")
    public TextWatcher textSizeEquationTextWatcher;

    @Inject
    @Named("skips")
    public TextWatcher skipTextWatcher;

    @Inject
    @Named("clear_words")
    public View.OnClickListener clearWordsListener;

    @Inject
    public MainActivity mainActivity;

    @Inject
    public WordCloudPresenter presenter;

    private InputMethodManager inputMethodManager;

    private EditText wordsEditText;
    private EditText textSizeEquationEditText;
    private EditText skipsEditText;
    private ImageButton clearWordsImageButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feature_text, container, false);

        inputMethodManager = DaggerFeaturesComponent
                .builder()
                .featuresModule(new FeaturesModule(mainActivity))
                .build()
                .getInputMethodManager();

        wordsEditText = view.findViewById(R.id.words_edittext);
        wordsEditText.addTextChangedListener(wordsTextWatcher);

        textSizeEquationEditText = view.findViewById(R.id.text_size_equation);
        textSizeEquationEditText.addTextChangedListener(textSizeEquationTextWatcher);

        skipsEditText = view.findViewById(R.id.skips_edittext);
        skipsEditText.addTextChangedListener(skipTextWatcher);

        clearWordsImageButton = view.findViewById(R.id.words_clear_button);
        clearWordsImageButton.setOnClickListener(clearWordsListener);

        presenter.initTextFragment();

        return view;
    }

    @Override
    public void setWords(String words) {
        wordsEditText.setText(words);
    }

    @Override
    public void showWords(String words) {
        wordsEditText.setText(words);
    }

    @Override
    public void showTextEquation(String textEquation) {
        textSizeEquationEditText.setText(textEquation);
    }

    @Override
    public void showSkips(String skips) {
        skipsEditText.setText(skips);
    }

    @Override
    public void showClearWordsButton(boolean show) {
        boolean isVisible = clearWordsImageButton.getVisibility() == View.VISIBLE;

        if (show) {
            if (!isVisible) {
                clearWordsImageButton.setVisibility(View.VISIBLE);
            }
        } else {
            if (isVisible) {
                clearWordsImageButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void clearFocusAndKeyboard() {
        boolean hadFocus = false;
        IBinder windowToken = null;

        if (wordsEditText.hasFocus()) {
            wordsEditText.clearFocus();
            hadFocus = true;
            windowToken = wordsEditText.getWindowToken();
        }

        if (textSizeEquationEditText.hasFocus()) {
            textSizeEquationEditText.clearFocus();
            hadFocus = true;
            windowToken = textSizeEquationEditText.getWindowToken();
        }

        if (skipsEditText.hasFocus()) {
            skipsEditText.clearFocus();
            hadFocus = true;
            windowToken = skipsEditText.getWindowToken();
        }

        if (hadFocus) {
            if (windowToken != null) {
                inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }
    }
}
