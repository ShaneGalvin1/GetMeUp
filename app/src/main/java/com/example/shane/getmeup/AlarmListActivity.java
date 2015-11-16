package com.example.shane.getmeup;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class AlarmListActivity extends AppCompatActivity {

    private List<Alarm> alarmList = new ArrayList<Alarm>();
    private ListView listView;
    private CustomListAdapter adapter;
    private TextView title;
    private ImageButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (TextView) findViewById(R.id.title);
        title.setText("Alarms");
        add = (ImageButton) findViewById(R.id.addAlarm);

        Alarm a = new Alarm("SNOOZE", "Weekend", "11:00", true, true, true, true);
        Alarm b = new Alarm("GET UP", "Weekdays", "07:00", true, false, true, false);
        Alarm c = new Alarm("WORK", "Weekdays", "18:00", false, true, false, true);

        alarmList.add(a);
        alarmList.add(b);
        alarmList.add(c);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomListAdapter(this, alarmList);
        listView.setAdapter(adapter);

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
}
