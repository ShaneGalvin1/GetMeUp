package com.example.valentincomte.getup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PutOffAlarmActivity extends AppCompatActivity {

    String alarmType;
    Button stopAlarmBtn;
    TextView descriptionTxtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_off_alarm);
        alarmType = getIntent().getExtras().getString("TYPE");
        descriptionTxtView = (TextView)findViewById(R.id.alarmDescription);
        if(alarmType.equals("TALK"))
        {
            descriptionTxtView.setText("Put the Talking alarm off.");
            stopAlarmBtn = (Button)findViewById(R.id.alarmOffButton);
            stopAlarmBtn.setVisibility(View.VISIBLE);
        }
        else if(alarmType.equals("SHAKE"))
        {
            descriptionTxtView.setText("Shake to put the alarm off.");
        }
        else
        {
            descriptionTxtView.setText("Walk to put the alarm off.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_put_off_alarm, menu);
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

    public void putOffAlarm(View view)
    {
        if (AlarmReceiver.mediaPlayer.isPlaying()) {
            AlarmReceiver.mediaPlayer.stop();
        }

    }
}
