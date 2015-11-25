package com.example.valentincomte.getup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AlarmListActivity extends AppCompatActivity {

    private ListView listView;
    private CustomListAdapter adapter;
    private AlarmDBHelper db = new AlarmDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        Button addButton = (Button) this.findViewById(R.id.buttonAddAlarm);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmListActivity.this, AlarmDetailsActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra("id", (long) -1);
                startActivityForResult(intent, 0);
            }
        });


        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomListAdapter(this, db.getAlarms());
        listView.setAdapter(adapter);

        listView.setFocusable(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long viewId = view.getId();

                if (viewId == R.id.alarmButton) {
                    AlarmModel model = adapter.getAlarms().get(position);
                    model.isEnabled = false;
                    db.updateAlarm(model);
                    adapter.setAlarms(db.getAlarms());
                    adapter.notifyDataSetChanged();
                } else if (viewId == R.id.alarmButton2) {
                    AlarmModel model = adapter.getAlarms().get(position);
                    model.isEnabled = true;
                    db.updateAlarm(model);
                    adapter.setAlarms(db.getAlarms());
                    adapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent(AlarmListActivity.this, AlarmDetailsActivity.class);
                    intent.setAction(Intent.ACTION_SEND);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.putExtra("id", (long) adapter.getAlarms().get(position).id);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            adapter.setAlarms(db.getAlarms());
            adapter.notifyDataSetChanged();
        }
    }

}
