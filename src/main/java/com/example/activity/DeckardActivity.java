package com.example.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.R;
import com.example.models.ToDoItem;
import com.example.persistence.DbHelper;
import com.example.persistence.ToDoDatabaseContract;

public class DeckardActivity extends Activity {

    private DbHelper dbHelper;
    private ArrayAdapter<ToDoItem> adapter;
    private SQLiteDatabase writableDB;
    private ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        dbHelper = new DbHelper(this);
        writableDB = dbHelper.getWritableDatabase();

        adapter = new ArrayAdapter<ToDoItem>(this, R.layout.todo_list_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(DeckardActivity.this).inflate(R.layout.todo_list_item, parent, false);

                final TextView itemText = (TextView) view.findViewById(R.id.item_text);
                final ToDoItem item = getItem(position);
                itemText.setText(item.getText());

                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_checkbox);

                checkBox.setChecked(item.isCompleted());

                int typeface = item.isCompleted() ? Typeface.BOLD_ITALIC : Typeface.NORMAL;

                itemText.setTypeface(null, typeface);
                if (item.getText().contains(getString(R.string.travel))) {
                    itemText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                    itemText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(DeckardActivity.this, TravelSearchActivity.class));
                        }
                    });
                }

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final boolean isComplete = checkBox.isChecked();
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                writableDB.execSQL("UPDATE " + ToDoDatabaseContract.ToDoEntry.TABLE_NAME + " SET "
                                        + ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_COMPLETED
                                        + " = " + (isComplete ? 1 : 0)
                                        + " WHERE " + ToDoDatabaseContract.ToDoEntry._ID + " = " + item.getId());

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                if (isComplete) {
                                    itemText.setTypeface(null, Typeface.BOLD_ITALIC);
                                } else {
                                    itemText.setTypeface(null, Typeface.NORMAL);
                                }
                            }
                        }.execute();

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
                CharSequence inserted = s.subSequence(start, Math.max(start, start + count));
                if (inserted.toString().indexOf('\n') >= 0) {
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

            ContentValues values = new ContentValues();

            values.put(ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT, value.toString());

            long id = writableDB.insert(
                    ToDoDatabaseContract.ToDoEntry.TABLE_NAME,
                    ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT,
                    values);
            adapter.insert(new ToDoItem(id, value.toString(), false), 0);

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
            long id = c.getLong(c.getColumnIndex(ToDoDatabaseContract.ToDoEntry._ID));
            String text = c.getString(c.getColumnIndex(ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT));
            boolean completed = c.getInt(c.getColumnIndex(ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_COMPLETED)) != 0;
            adapter.add(new ToDoItem(id, text, completed));
        }

        list.setAdapter(adapter);
    }
}
