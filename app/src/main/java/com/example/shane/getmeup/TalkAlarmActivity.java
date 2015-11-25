package com.example.shane.getmeup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TalkAlarmActivity extends AppCompatActivity {

    MediaRecorder recorder;
    ImageButton recordButton;
    String fileName;
    long alarmTimeMilliseconds;
    private static final String LOG_TAG = "TalkAlarmActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_alarm);
        recordButton = (ImageButton)findViewById(R.id.recordButton);
        alarmTimeMilliseconds = getIntent().getExtras().getLong("AlarmTime");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_talk_alarm, menu);
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

    public void recordVoice(View view)
    {
        recordButton.setEnabled(false);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        String datetime= new Date().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        fileName = getFilesDir() + "/" + formatter.format(now) + "audio.m4a";
        recorder.setOutputFile(fileName);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
        Toast.makeText(getApplicationContext(), "Recording...", Toast.LENGTH_LONG).show();
    }

    public void stopRecordVoice(View view)
    {
        Toast.makeText(getApplicationContext(), "Stopped Recording...", Toast.LENGTH_LONG).show();
        recorder.stop();
        recorder.reset();
        recorder.release();
    }

    public void setTalkAlarm(View view)
    {
        setResult(Activity.RESULT_OK,
                new Intent().putExtra("FileName", fileName));
        finish();
    }
}
