package com.example.activity;

import android.app.Fragment;
import com.example.fragment.TravelSearchInputFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by pivotal on 7/9/15.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 18)
public class TravelSearchActivityTest {
    private ActivityController<TravelSearchActivity> activityController;
    private TravelSearchActivity activity;

    @Before
    public void setUp() throws Exception {
        activityController = Robolectric.buildActivity(TravelSearchActivity.class).create().resume().visible();
        activity = activityController.get();
    }

    @Test
    public void activityCanBeCreated() throws Exception {
        assertThat(activity).isNotNull();
    }

    @Test
    public void testHasFragment() throws Exception {
        Fragment fragment = activity.getFragmentManager().findFragmentByTag(TravelSearchInputFragment.class.getName());
        assertThat(fragment).isInstanceOf(TravelSearchInputFragment.class);
    }
}