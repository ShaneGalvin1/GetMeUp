package com.example.valentincomte.getup;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;

import java.io.IOException;

/**
 * Created by Eazhilarasi on 23/11/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static MediaPlayer mediaPlayer;

    public  AlarmReceiver()
    {

    }
    @Override
    public void onReceive(Context context, Intent intent) {

        String alarmType = intent.getExtras().getString("TYPE");
        if(alarmType.equals("AIRPLANE")){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isAirPlane = pref.getBoolean("pref_airplane_mode", false);

            Settings.System.putInt(
                    context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, isAirPlane ? 0 : 1);

            // Post an intent to reload.
            Intent airIntent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", !isAirPlane);
            context.sendBroadcast(airIntent);
        }
        else if(alarmType.equals("SHAKE")) {
            PendingIntent Sender = PendingIntent.getBroadcast(context, 0, intent, 0);
            NotificationManager manager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);


            mediaPlayer = MediaPlayer.create(context, notification);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            int value = intent.getExtras().getInt("NoOfShakes");

            Intent i = new Intent();
            i.putExtra("TYPE", alarmType);
            i.putExtra("NoOfShakes", value);
            i.setClassName("com.example.valentincomte.getup", "com.example.valentincomte.getup.PutShakeAlarmOff");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

        else if(alarmType.equals("TALK"))
        {
            String recordFileName = intent.getExtras().getString("FileName");
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(recordFileName);
                mediaPlayer.prepare();
            } catch (IOException e) {

            }
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            Intent i = new Intent();
            i.putExtra("TYPE", alarmType);
            i.setClassName("com.example.valentincomte.getup", "com.example.valentincomte.getup.PutOffAlarmActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        else
        {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mediaPlayer = MediaPlayer.create(context, notification);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            int value = intent.getExtras().getInt("NoOfSteps");

            Intent i = new Intent();
            i.putExtra("TYPE", alarmType);
            i.putExtra("NoOfSteps", value);
            i.setClassName("com.example.valentincomte.getup", "com.example.valentincomte.getup.PutWalkAlarmOff");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }
}
