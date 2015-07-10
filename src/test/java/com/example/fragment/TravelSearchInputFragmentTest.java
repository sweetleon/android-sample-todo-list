package com.example.fragment;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.example.R;
import com.example.network.SabreClient;
import com.example.network.SabreClient.FlightSearchResponse;
import com.example.test.MyTestRunner;
import org.apache.http.HttpRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.util.ActivityController;
import retrofit.Callback;
import retrofit.RestAdapter;

import static junit.framework.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MyTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 18)
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

    @Test
    public void pressingButton_showsSpinner() throws Exception {
        assertThat(fragment.getView().findViewById(android.R.id.progress).getVisibility()).isEqualTo(View.GONE);
        fragment.getView().findViewById(R.id.travel_go_button).performClick();
        assertThat(fragment.getView().findViewById(android.R.id.progress).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    @Config(shadows = {MockingRestAdapterBuilder.class})
    public void whenApiReturnsData() throws Exception {
        fragment.getView().findViewById(R.id.travel_go_button).performClick();

        ArgumentCaptor<Callback> callbackArgumentCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(MockingRestAdapterBuilder.sabreClient).searchFlights(anyString(), anyString(), anyString(), anyString(), callbackArgumentCaptor.capture());
        callbackArgumentCaptor.getValue().success(new FlightSearchResponse(), null);

        fail("TODO");
    }

    public static class FakeFragmentActivity extends FragmentActivity {
    }

    @Implements(RestAdapter.Builder.class)
    public static class MockingRestAdapterBuilder {
        private static SabreClient sabreClient = Mockito.mock(SabreClient.class, CALLS_REAL_METHODS);

        @Implementation
        public RestAdapter build() {
            RestAdapter restAdapter = mock(RestAdapter.class);
            when(restAdapter.create(SabreClient.class)).thenReturn(sabreClient);
            return restAdapter;
        }
    }
}