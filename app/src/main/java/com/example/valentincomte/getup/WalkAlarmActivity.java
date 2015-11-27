package com.example.valentincomte.getup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

public class WalkAlarmActivity extends AppCompatActivity {

    SeekBar walkSeekBar;
    int noOfSteps = 50;
    long alarmTimeMilliseconds = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_alarm);
        alarmTimeMilliseconds = getIntent().getExtras().getLong("AlarmTime");
        setUpStepSeeker();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_walk_alarm, menu);
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

    private void setUpStepSeeker()
    {
        walkSeekBar = (SeekBar)findViewById(R.id.walkSeekBar);
        walkSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
                    noOfSteps = 50;
                } else if (progress > 0 && progress < 40) {
                    noOfSteps = progress + 50;
                } else {
                    noOfSteps = 100;
                }
            }
        });
    }

    public void setAlarm(View view)
    {
        setResult(Activity.RESULT_OK,
                new Intent().putExtra("NoOfSteps", noOfSteps).putExtra("TYPE", "WALK"));

        finish();
    }

}
