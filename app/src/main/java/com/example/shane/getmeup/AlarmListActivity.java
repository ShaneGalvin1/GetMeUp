package com.example.shane.getmeup;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class AlarmListActivity extends AppCompatActivity {

    private List<Alarm> alarmList = new ArrayList<Alarm>();
    private ListView listView;
    private CustomListAdapter adapter;
    private TextView title;
    private ImageButton add, alarmOn, alarmOff;
    private int selectedRow;
    private Alarm temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (TextView) findViewById(R.id.title);
        title.setText("Alarms");
        add = (ImageButton) findViewById(R.id.addAlarm);
        //alarmOn = (ImageButton) findViewById(R.id.alarmButton);
        //alarmOn = (ImageButton) findViewById(R.id.alarmButton);


        Alarm b = new Alarm("Get Up", "Weekdays", "07:00", true, false, true, false, false);
        Alarm c = new Alarm("Work", "Weekdays", "18:00", false, true, false, true, true);
        Alarm d = new Alarm("Snooze", "Sunday", "12:00", false, false, true, true, false);


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

        return super.onOptionsItemSelected(item);
    }


}
