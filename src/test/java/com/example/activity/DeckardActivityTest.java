package com.example.activity;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.example.R;
import com.example.persistence.ToDoDatabaseContract;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Robolectric.directlyOn;
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

        TextView item = (TextView) todoList.getChildAt(0).findViewById(R.id.item_text);
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

        TextView item = (TextView) todoList.getChildAt(0).findViewById(R.id.item_text);
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
    public void testScrollToTopAfterAdd() throws Exception {
        addItem("hello");

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        todoList.smoothScrollToPosition(1);

        addItem("goodbye");

        assertThat(shadowOf(todoList).getSmoothScrolledPosition()).isEqualTo(0);

        shadowOf(todoList).populateItems();

        TextView item = (TextView) todoList.getChildAt(0).findViewById(R.id.item_text);
        assertThat(item.getText()).isEqualTo("goodbye");

        item = (TextView) todoList.getChildAt(1).findViewById(R.id.item_text);
        assertThat(item.getText()).isEqualTo("hello");
    }

    @Test
    public void testHittingEnterInsteadOfAdd() throws Exception {
        TextView input = (TextView) activity.findViewById(R.id.new_item_text);
        input.setText("hello");
        input.append("\n");

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        shadowOf(todoList).populateItems();

        TextView item = (TextView) todoList.getChildAt(0).findViewById(R.id.item_text);
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

    @Test
    @Config(shadows = {QueryCapturingSqliteDatabase.class})
    public void testMaintainOrder() throws Exception {
        addItem("hello");
        addItem("goodbye");

        activityController = Robolectric.buildActivity(DeckardActivity.class).create().resume().visible();

        assertThat(QueryCapturingSqliteDatabase.lastOrderBy).isEqualTo(ToDoDatabaseContract.ToDoEntry._ID + " DESC");
    }

    @Test
    public void testCheckBoxAndBolded() throws Exception {
        TextView view = addItem("hello");

        ListView todoList = (ListView) activity.findViewById(android.R.id.list);
        shadowOf(todoList).populateItems();

        TextView item = (TextView) todoList.getChildAt(0).findViewById(R.id.item_text);
        assertThat(item.getText()).isEqualTo("hello");

        CheckBox checkBox = (CheckBox) todoList.getChildAt(0).findViewById(R.id.item_checkbox);
        assertThat(checkBox.isChecked()).isFalse();

        checkBox.performClick();

        assertThat(checkBox.isChecked()).isTrue();
        assertThat(item.getTypeface().isBold()).isTrue();
        assertThat(item.getTypeface().isItalic()).isTrue();

        checkBox.performClick();

        assertThat(item.getTypeface()).isNull();
    }

    private TextView addItem(String inputText) {
        TextView input = (TextView) activity.findViewById(R.id.new_item_text);
        input.setText(inputText);

        Button button = (Button) activity.findViewById(R.id.new_item_add);
        button.performClick();
        return input;
    }

    @Implements(SQLiteDatabase.class)
    public static class QueryCapturingSqliteDatabase {
        @RealObject
        SQLiteDatabase realObject;
        private static String lastOrderBy = "query() has never been called";

        @Implementation
        public Cursor query(String table, String[] columns, String selection,
                            String[] selectionArgs, String groupBy, String having,
                            String orderBy) {
            lastOrderBy = orderBy;

            return directlyOn(realObject, SQLiteDatabase.class).query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }
    }
}
