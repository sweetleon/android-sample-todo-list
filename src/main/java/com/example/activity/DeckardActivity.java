package com.example.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.R;
import com.example.persistence.DbHelper;
import com.example.persistence.ToDoDatabaseContract;

public class DeckardActivity extends Activity {

    private DbHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private SQLiteDatabase writableDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        dbHelper = new DbHelper(this);
        writableDB = dbHelper.getWritableDatabase();

        adapter = new ArrayAdapter<String>(this, R.layout.todo_list_item);

        findViewById(R.id.new_item_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView input = (TextView) findViewById(R.id.new_item_text);
                CharSequence value = input.getText();

                if (value == null || value.length() == 0) {
                    new AlertDialog.Builder(view.getContext()).setMessage(getString(R.string.input_required)).create().show();
                } else {
                    adapter.add(value.toString());

                    ContentValues values = new ContentValues();

                    values.put(ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT, value.toString());

                    writableDB.insert(
                            ToDoDatabaseContract.ToDoEntry.TABLE_NAME,
                            ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT,
                            values);

                    input.setText("");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String[] columns = {ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT};
        Cursor c = writableDB.query(ToDoDatabaseContract.ToDoEntry.TABLE_NAME, columns, null, null, null, null, null);

        adapter.clear();
        while (c.moveToNext()) {
            String value = c.getString(c.getColumnIndex(ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT));
            adapter.add(value);
        }

        final ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(adapter);
    }
}
