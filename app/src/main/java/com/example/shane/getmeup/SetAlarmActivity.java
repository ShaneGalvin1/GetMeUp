package com.example.shane.getmeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SetAlarmActivity extends AppCompatActivity {

    Spinner spinner;
    TimePicker timepicker;
    long millisecondsAlarmTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        setUpTimePicker();
        setUpTheSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_alarm, menu);
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
    private void setUpTimePicker() {
        timepicker = (TimePicker)findViewById(R.id.alarmTimePicker);
        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                millisecondsAlarmTime = hourOfDay * 3600000;
                millisecondsAlarmTime = minute * 60000;
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
                millisecondsAlarmTime = cal.getTimeInMillis() + offset;
            }
        });

    }

    void setUpTheSpinner()
    {
        spinner = (Spinner) findViewById(R.id.alarmType_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.alarmType_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).equals("Choose Alarm type")) {
                    //do nothing.
                } else if (parent.getItemAtPosition(position).equals("Shake")) {
                    Intent AlarmTypeIntent = new Intent(view.getContext(), ShakeAlarmActivity.class);
                    AlarmTypeIntent.putExtra("AlarmTime", millisecondsAlarmTime);
                    startActivity(AlarmTypeIntent);
                } else if (parent.getItemAtPosition(position).equals("Walk")) {
                    Intent AlarmTypeIntent = new Intent(view.getContext(), WalkAlarmActivity.class);
                    AlarmTypeIntent.putExtra("AlarmTime", millisecondsAlarmTime);
                    startActivity(AlarmTypeIntent);
                } else {
                    Intent AlarmTypeIntent = new Intent(view.getContext(), TalkAlarmActivity.class);
                    AlarmTypeIntent.putExtra("AlarmTime", millisecondsAlarmTime);
                    startActivity(AlarmTypeIntent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}