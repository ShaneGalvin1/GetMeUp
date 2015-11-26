package com.example.shane.getmeup;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

@TargetApi(23)
public class AlarmListActivity extends AppCompatActivity {

    private Cursor mCursor = null;
    private static final String[] COLS = new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART};

    private List<Alarm> alarmList = new ArrayList<Alarm>();
    private ListView listView;
    private CustomListAdapter adapter;
    private TextView title;
    private ImageButton add;
    private Alarm temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (TextView) findViewById(R.id.title);
        add = (ImageButton) findViewById(R.id.addAlarm);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });

        Alarm b = new Alarm("Get Up", "Mon Tue", "07:00", true, false, true, false, false);
        Alarm c = new Alarm("Work", "Wed Thu Fri Sat", "18:00", false, true, false, false, true);
        Alarm d = new Alarm("Snooze", "Sun", "12:00", false, false, true, true, false);


        alarmList.add(b);
        alarmList.add(c);
        alarmList.add(d);


        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomListAdapter(this, alarmList);
        listView.setAdapter(adapter);

        listView.setFocusable(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long viewId = view.getId();

                if (viewId == R.id.alarmButton) {
                    temp = alarmList.get(position);
                    temp.setOn(false);
                    alarmList.set(position,temp);
                    adapter.notifyDataSetChanged();
                } else if (viewId == R.id.alarmButton2) {
                    temp = alarmList.get(position);
                    temp.setOn(true);
                    alarmList.set(position,temp);
                    adapter.notifyDataSetChanged();
                } else {
                    // TODO custom dialog to edit alarm.
                    //selectedRow = position;
                    //adapter.notifyDataSetChanged();
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
        else if (id == R.id.action_calendar)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
