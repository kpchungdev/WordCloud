package com.gmail.kpchungdev.wordcloud.features;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;

import com.gmail.kpchungdev.wordcloud.MainActivity;
import com.gmail.kpchungdev.wordcloud.R;
import com.gmail.kpchungdev.wordcloud.tutorial.FragmentEngineer;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudFragment;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloud;
import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudPresenter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TextInstrumentedTest {

    WordCloud wordCloud;

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void init() {
        MainActivity mainActivity = activityActivityTestRule
                .getActivity();

        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();

        WordCloudFragment wordCloudFragment = (WordCloudFragment) fragmentManager.findFragmentByTag(FragmentEngineer.TAG_WORDCLOUD_FRAGMENT);
        WordCloudPresenter presenter = wordCloudFragment.presenter;
        wordCloud = presenter.wordCloud;

    }

    @Test
    public void testWords() {
        String words = "hi hi hi hi bye bye bye";
        onView(withId(R.id.words_edittext)).perform(typeText(words)).check(matches(withText(words)));
        assertEquals(words, wordCloud.getWords());
    }

    @Test
    public void testTextEquation() {
        String equation = "2 * repetition";
        onView(withId(R.id.text_size_equation)).perform(typeText(equation)).check(matches(withText(equation)));
        assertEquals(equation, wordCloud.getTextExpression());
    }

    @Test
    public void testSkips() {
        String skips = "3";
        onView(withId(R.id.skips_edittext)).perform(typeText(skips)).check(matches(withText(skips)));
        assertEquals(skips, Integer.toString(wordCloud.getAllowedSkips()));
    }

}
