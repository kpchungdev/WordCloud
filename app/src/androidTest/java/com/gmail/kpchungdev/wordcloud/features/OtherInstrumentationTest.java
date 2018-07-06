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
public class OtherInstrumentationTest {

    WordCloudFragment wordCloudFragment;

    WordCloudPresenter presenter;

    WordCloud wordCloud;

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void init() {
        MainActivity mainActivity = activityActivityTestRule
                .getActivity();

        FragmentEngineer fragmentEngineer = mainActivity.fragmentEngineer;

        FragmentManager fragmentManager = activityActivityTestRule
                .getActivity()
                .getSupportFragmentManager();

        wordCloudFragment = (WordCloudFragment) fragmentManager.findFragmentById(R.id.fragment_container);

        presenter = (WordCloudPresenter) wordCloudFragment.presenter;
        wordCloud = presenter.wordCloud;

    }



    @Test
    public void testLoadingShowWords() {
        assertEquals(false, wordCloud.isLoadingShowWords());
        onView(withId(R.id.loading_show_words_switch)).perform(click());
        assertEquals(true, wordCloud.isLoadingShowWords());
    }

    @Test
    public void testRadius() {
        float radius = 100;
        onView(withId(R.id.radius_edittext)).perform(typeText("100")).check(matches(withText("100")));
        assertEquals(radius, wordCloud.getCircleRadius());
    }

}
