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
    String alarmTimeStr;
    Intent AlarmIntent;
    String alarmType;
    Alarm alarm;
    boolean isShake = false;
    boolean isWalk = false;
    boolean isTalk = false;
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
                alarmTimeStr = String.valueOf(hourOfDay) + " : " + String.valueOf(minute);
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
                    alarmType = "SHAKE";
                    isShake = true;
                    Intent AlarmTypeIntent = new Intent(view.getContext(), ShakeAlarmActivity.class);
                    AlarmTypeIntent.putExtra("AlarmTime", millisecondsAlarmTime);
                    startActivityForResult(AlarmTypeIntent, 0);
                } else if (parent.getItemAtPosition(position).equals("Walk")) {
                    alarmType = "WALK";
                    isWalk = true;
                    Intent AlarmTypeIntent = new Intent(view.getContext(), WalkAlarmActivity.class);
                    AlarmTypeIntent.putExtra("AlarmTime", millisecondsAlarmTime);
                    startActivityForResult(AlarmTypeIntent, 0);
                } else {
                    alarmType = "TALK";
                    isTalk = true;
                    Intent AlarmTypeIntent = new Intent(view.getContext(), TalkAlarmActivity.class);
                    AlarmTypeIntent.putExtra("AlarmTime", millisecondsAlarmTime);
                    startActivityForResult(AlarmTypeIntent, 0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AlarmIntent = new Intent(SetAlarmActivity.this, AlarmReceiver.class);
       // String alarmType = data.getExtras().getString("TYPE");
        if(alarmType.equals("SHAKE"))
        {
            int noOfShakes = data.getIntExtra("NoOfShakes", 0);
            AlarmIntent.putExtra("NoOfShakes", noOfShakes);
            AlarmIntent.putExtra("TYPE", "SHAKE");
        }
        if(alarmType.equals("WALK"))
        {
            int noOfSteps = data.getIntExtra("NoOfSteps", 0);
            AlarmIntent.putExtra("NoOfSteps", noOfSteps);
            AlarmIntent.putExtra("TYPE", "WALK");
        }
        else
        {
            String recordFileName = data.getStringExtra("FileName");
            AlarmIntent.putExtra("FileName",recordFileName);
            AlarmIntent.putExtra("TYPE", "TALK");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setUpAlarm(View view)
    {
            AlarmManager AlmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent Sender = PendingIntent.getBroadcast(this, 0, AlarmIntent, 0);
            AlmMgr.set(AlarmManager.RTC_WAKEUP, millisecondsAlarmTime, Sender);
            //Populate the alarm model.

            alarm = new Alarm("My Alarm", "Thursday", alarmTimeStr,isShake,isWalk,false,isTalk, true);
            AlarmListActivity.alarmList.add(alarm);
            finish();
    }
}