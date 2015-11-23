package com.example.shane.getmeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class ShakeAlarmActivity extends AppCompatActivity {


    SeekBar shakeSeekBar;
    int noOfShakes = 10;
    long alarmTimeMilliseconds = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_alarm);
        alarmTimeMilliseconds = getIntent().getExtras().getLong("AlarmTime");
        setUpShakeSeeker();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shake_alarm, menu);
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



    private void setUpShakeSeeker() {
        shakeSeekBar = (SeekBar)findViewById(R.id.shakeSeekBar);
        shakeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                if (progress == 0) {
                    noOfShakes = 10;
                } else if (progress > 0 && progress < 40) {
                    noOfShakes = progress + 10;
                } else {
                    noOfShakes = progress + 1;
                }
                noOfShakes = 10;
                Toast.makeText(getApplicationContext(), String.valueOf(noOfShakes), Toast.LENGTH_LONG).show();

            }
        });
    }


    public void setAlarm(View view)
    {
        Toast.makeText(view.getContext(), String.valueOf(alarmTimeMilliseconds), Toast.LENGTH_SHORT).show();
        Intent AlarmIntent = new Intent(ShakeAlarmActivity.this, AlarmReceiver.class);
        AlarmIntent.putExtra("NoOfShakes", noOfShakes);
        AlarmIntent.putExtra("TYPE", "SHAKE");
        AlarmManager AlmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent Sender = PendingIntent.getBroadcast(this, 0, AlarmIntent, 0);
        AlmMgr.set(AlarmManager.RTC_WAKEUP,  alarmTimeMilliseconds, Sender);
    }
}
