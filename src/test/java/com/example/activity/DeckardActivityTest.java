package com.example.activity;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.R;
import org.junit.Before;
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
        TextView input = addItem("hello");

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        shadowOf(todoList).populateItems();

        TextView item = (TextView) todoList.getChildAt(0);
        assertThat(item.getText()).isEqualTo("hello");

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(alertDialog).isNull();
    }

    @Test
    public void createdItemsPersistAcrossInstances() throws Exception {
        TextView input = addItem("hello");

        activityController = Robolectric.buildActivity(DeckardActivity.class).create().resume().visible();
        activity = activityController.get();

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        shadowOf(todoList).populateItems();

        TextView item = (TextView) todoList.getChildAt(0);
        assertThat(item.getText()).isEqualTo("hello");
    }

    @Test
    public void resumeDoesNotDuplicateItems() throws Exception {
        TextView input = addItem("hello");

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

    @Test
    public void testClearTextAfterAdding() throws Exception {
        TextView input = addItem("hello");

        assertThat(input.getText()).isEmpty();
    }

    @Test
    public void testScrollToEOLAfterAdd() throws Exception {
        addItem("hello");
        addItem("goodbye");

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        assertThat(shadowOf(todoList).getSmoothScrolledPosition()).isEqualTo(1);
    }

    private TextView addItem(String inputText) {
        TextView input = (TextView) activity.findViewById(R.id.new_item_text);
        input.setText(inputText);

        Button button = (Button) activity.findViewById(R.id.new_item_add);
        button.performClick();
        return input;
    }

    @Test
    public void testHittingEnterInsteadOfAdd() throws Exception {
        TextView input = (TextView) activity.findViewById(R.id.new_item_text);
        input.setText("hello");
        input.append("\n");

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        shadowOf(todoList).populateItems();

        TextView item = (TextView) todoList.getChildAt(0);
        assertThat(item.getText()).isEqualTo("hello");

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(alertDialog).isNull();

        assertThat(input.getText()).isEmpty();
    }

    @Test
    public void testHittingEnterWithNoOtherInput() throws Exception {
        TextView input = (TextView) activity.findViewById(R.id.new_item_text);
        input.append("\n");

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(alertDialog).isNotNull();

        assertThat(input.getText()).isEmpty();
    }
}
