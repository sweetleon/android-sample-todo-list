package com.example.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.R;

public class DeckardActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.todo_list);

    final ListView list = (ListView) findViewById(android.R.id.list);
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.todo_list_item);
    list.setAdapter(adapter);

    findViewById(R.id.new_item_add).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        TextView input = (TextView) findViewById(R.id.new_item_text);
        adapter.add(input.getText().toString());
      }
    });
  }
}
