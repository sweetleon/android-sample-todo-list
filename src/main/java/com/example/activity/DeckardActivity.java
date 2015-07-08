package com.example.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.example.R;
import com.example.persistence.DbHelper;
import com.example.persistence.ToDoDatabaseContract;

public class DeckardActivity extends Activity {

    private DbHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private SQLiteDatabase writableDB;
    private ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        dbHelper = new DbHelper(this);
        writableDB = dbHelper.getWritableDatabase();

        adapter = new ArrayAdapter<String>(this, R.layout.todo_list_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(DeckardActivity.this).inflate(R.layout.todo_list_item, parent, false);
                final TextView itemText = (TextView) view.findViewById(R.id.item_text);
                itemText.setText(getItem(position));

                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_checkbox);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkBox.isChecked()) {
                            itemText.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                        else {
                            itemText.setTypeface(null, Typeface.NORMAL);

                        }
                    }
                });
                return view;
            }
        };
        list = (ListView) findViewById(android.R.id.list);

        final TextView input = (TextView) findViewById(R.id.new_item_text);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CharSequence inserted =  s.subSequence(start, Math.max(start, start + count));
                if(inserted.toString().indexOf('\n') >= 0) {
                    addItem(s.subSequence(0, start), input);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.new_item_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence value = input.getText();

                addItem(value, input);
            }
        });
    }

    private void addItem(CharSequence value, TextView input) {
        if (value == null || value.length() == 0) {
            new AlertDialog.Builder(this).setMessage(getString(R.string.input_required)).create().show();
        } else {
            adapter.insert(value.toString(), 0);

            ContentValues values = new ContentValues();

            values.put(ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT, value.toString());

            writableDB.insert(
                    ToDoDatabaseContract.ToDoEntry.TABLE_NAME,
                    ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT,
                    values);

            list.smoothScrollToPosition(0);
        }

        input.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        String[] columns = {"*"};
        Cursor c = writableDB.query(ToDoDatabaseContract.ToDoEntry.TABLE_NAME, columns, null, null, null, null, ToDoDatabaseContract.ToDoEntry._ID + " DESC");

        adapter.clear();
        while (c.moveToNext()) {
            String value = c.getString(c.getColumnIndex(ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT));
            adapter.add(value);
        }

        list.setAdapter(adapter);
    }
}
