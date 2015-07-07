package com.example.activity;

import android.app.Activity;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        dbHelper = new DbHelper(this);
        final SQLiteDatabase writableDB = dbHelper.getWritableDatabase();

        String[] columns = {
                ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT};


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.todo_list_item);
        Cursor c = writableDB.query(ToDoDatabaseContract.ToDoEntry.TABLE_NAME, columns, null, null, null, null, null);

        while (c.moveToNext()) {
            String value = c.getString(c.getColumnIndex(ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT));
            adapter.add(value);
        }

        final ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(adapter);

        findViewById(R.id.new_item_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView input = (TextView) findViewById(R.id.new_item_text);
                String value = input.getText().toString();
                adapter.add(value);
                ContentValues values = new ContentValues();

                values.put(ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT, value);

                writableDB.insert(
                        ToDoDatabaseContract.ToDoEntry.TABLE_NAME,
                        ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT,
                        values);
            }
        });
    }
}
