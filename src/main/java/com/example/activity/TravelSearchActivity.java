package com.example.activity;

import android.app.Activity;
import android.os.Bundle;
import com.example.R;
import com.example.fragment.TravelSearchInputFragment;

public class TravelSearchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_travel);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.travel_search_input, new TravelSearchInputFragment(), TravelSearchInputFragment.class.getName())
                .commit();
    }
}
