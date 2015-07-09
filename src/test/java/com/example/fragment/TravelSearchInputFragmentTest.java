package com.example.fragment;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.R;
import com.example.activity.TravelSearchActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class TravelSearchInputFragmentTest {
    private ActivityController<FakeFragmentActivity> activityController;
    private FakeFragmentActivity activity;
    private TravelSearchInputFragment fragment;

    @Before
    public void setUp() throws Exception {
        activityController = Robolectric.buildActivity(FakeFragmentActivity.class).create().resume().visible();
        activity = activityController.get();
        fragment = new TravelSearchInputFragment();
        activity.getFragmentManager().beginTransaction().add(fragment, "abc").commit();
    }

    @Test
    public void fragmentCanBeCreated() throws Exception {
        assertThat(fragment).isNotNull();
    }

    @Test
    public void testShowFields() throws Exception {
        View dept_date = fragment.getView().findViewById(R.id.travel_dept_date);
        assertThat(dept_date).isNotNull();


        View return_date = fragment.getView().findViewById(R.id.travel_return_date);
        assertThat(return_date).isNotNull();


        View dept_city = fragment.getView().findViewById(R.id.travel_dept_city);
        assertThat(dept_city).isNotNull();


        View dest_city = fragment.getView().findViewById(R.id.travel_dest_city);
        assertThat(dest_city).isNotNull();

        View go_button = fragment.getView().findViewById(R.id.travel_go_button);
        assertThat(go_button).isNotNull();


    }

    private TextView addItem(String inputText) {
        TextView input = (TextView) activity.findViewById(R.id.new_item_text);
        input.setText(inputText);

        Button button = (Button) activity.findViewById(R.id.new_item_add);
        button.performClick();
        return input;
    }

    public static class FakeFragmentActivity extends FragmentActivity {
    }
}