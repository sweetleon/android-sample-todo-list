package com.example.activity;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.R;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.util.ActivityController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class DeckardActivityTest {

    private ActivityController<DeckardActivity> activityController;
    private DeckardActivity activity;

    @Before
    public void setUp() throws Exception {
        activityController = Robolectric.buildActivity(DeckardActivity.class).create().resume().visible();
        activity = activityController.get();
    }

    @Test
    public void activityCanBeCreated() throws Exception {
        assertThat(activity).isNotNull();
    }

    @Test
    public void inputTextMustBeSingleLine() throws Exception {
        TextView input = (TextView) activity.findViewById(R.id.new_item_text);
        assertThat(input.getMaxLines()).isEqualTo(1);
    }

    @Test
    public void pressingEnterAddsTheItem() throws Exception {
        TextView input = (TextView) activity.findViewById(R.id.new_item_text);
        input.setText("hello");

        Button button = (Button) activity.findViewById(R.id.new_item_add);
        button.performClick();

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        shadowOf(todoList).populateItems();

        TextView item = (TextView) todoList.getChildAt(0);
        assertThat(item.getText()).isEqualTo("hello");

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(alertDialog).isNull();
    }

    @Test
    public void createdItemsPersistAcrossInstances() throws Exception {
        TextView input = (TextView) activity.findViewById(R.id.new_item_text);
        input.setText("hello");

        Button button = (Button) activity.findViewById(R.id.new_item_add);
        button.performClick();

        activityController = Robolectric.buildActivity(DeckardActivity.class).create().resume().visible();
        activity = activityController.get();

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        shadowOf(todoList).populateItems();

        TextView item = (TextView) todoList.getChildAt(0);
        assertThat(item.getText()).isEqualTo("hello");
    }

    @Test
    public void resumeDoesNotDuplicateItems() throws Exception {
        TextView input = (TextView) activity.findViewById(R.id.new_item_text);
        input.setText("hello");

        Button button = (Button) activity.findViewById(R.id.new_item_add);
        button.performClick();

        activityController.pause().resume();

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        assertThat(todoList.getCount()).isEqualTo(1);
    }

    @Test
    public void showErrorMsg_whenThereIsNoText_andDoNotAddItem() throws Exception {
        Button button = (Button) activity.findViewById(R.id.new_item_add);
        button.performClick();

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(alertDialog).isNotNull();
        assertThat(shadowOf(alertDialog).getMessage()).isEqualTo("Please enter the text");

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        assertThat(todoList.getCount()).isEqualTo(0);
    }
}
