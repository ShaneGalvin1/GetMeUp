package com.example.valentincomte.getup;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class AlarmDetailsActivity extends AppCompatActivity {

    private AlarmModel alarmDetails;
    private ArrayList<Integer> tempDays; //Contains indexes of days
    long millisecondsAlarmTime;
    private AlarmDBHelper db = new AlarmDBHelper(this);

    Spinner spinner;
    String alarmType;
    boolean isShake = false;
    boolean isWalk = false;
    boolean isTalk = false;

    Intent AlarmIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);
        getSupportActionBar().setTitle("Create New Alarm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        long id = -1;
        if(extras!= null){
            id= extras.getLong("id");
        }

        if(id < 0) {
            alarmDetails = new AlarmModel();
        }else{
            alarmDetails = db.getAlarm(id);

            EditText editName = (EditText) findViewById(R.id.alarm_details_name);
            editName.setText(alarmDetails.name);
            TextView txtTone = (TextView) findViewById(R.id.alarm_label_tone);
            txtTone.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
            CheckBox checkVibrations = (CheckBox) findViewById(R.id.alarm_details_vibrate);
            checkVibrations.setChecked(alarmDetails.vibrations);
            TextView txtDays = (TextView) findViewById(R.id.alarm_label_days_selection);
            txtDays.setText(alarmDetails.getStringDays());
        }

        setUpTheSpinner();


        TimePicker timepicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
        timepicker.setCurrentHour(alarmDetails.Hour);
        timepicker.setCurrentMinute(alarmDetails.Minute);



        final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
        ringToneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent, 1);
            }
        });

        final LinearLayout daysContainer = (LinearLayout) findViewById(R.id.alarm_days_container);
        daysContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList mSelectedItems = new ArrayList();
                boolean[] dayList = alarmDetails.getDays();
                for(int i=0; i<7;i++){
                    if(dayList[i] == true){
                        mSelectedItems.add(i);
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.dialog_days_title);
                builder.setMultiChoiceItems(R.array.days_array, dayList, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            mSelectedItems.add(which);
                        } else if (mSelectedItems.contains(which)) {
                            mSelectedItems.remove(Integer.valueOf(which));
                        }
                    }

                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Collections.sort(mSelectedItems);
                        tempDays = mSelectedItems;
                        TextView labelDays = (TextView) findViewById(R.id.alarm_label_days_selection);
                        labelDays.setText("");
                        for (Integer item : (ArrayList<Integer>) mSelectedItems) {
                            labelDays.setText(labelDays.getText() + alarmDetails.DAYS_STRINGS.get(item).substring(0, 3) + ",");
                        }
                        labelDays.setText(labelDays.getText().toString().substring(0, labelDays.getText().length() - 1));
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
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
                    startActivityForResult(AlarmTypeIntent, 2);
                } else if (parent.getItemAtPosition(position).equals("Walk")) {
                    alarmType = "WALK";
                    isWalk = true;
                    Intent AlarmTypeIntent = new Intent(view.getContext(), WalkAlarmActivity.class);
                    AlarmTypeIntent.putExtra("AlarmTime", millisecondsAlarmTime);
                    startActivityForResult(AlarmTypeIntent, 2);
                } else {
                    alarmType = "TALK";
                    isTalk = true;
                    Intent AlarmTypeIntent = new Intent(view.getContext(), TalkAlarmActivity.class);
                    AlarmTypeIntent.putExtra("AlarmTime", millisecondsAlarmTime);
                    startActivityForResult(AlarmTypeIntent, 2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AlarmIntent = new Intent(AlarmDetailsActivity.this, AlarmReceiver.class);
        //TODO: replace with default alarm
        AlarmIntent.putExtra("TYPE", "SHAKE");
        AlarmIntent.putExtra("NoOfShakes", 10);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    TextView txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);
                    txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
                    break;
                }
                default: {
                    break;
                }
                case 2: {
                    // String alarmType = data.getExtras().getString("TYPE");
                    if(alarmType.equals("SHAKE"))
                    {
                        int noOfShakes = data.getIntExtra("NoOfShakes", 0);
                        alarmDetails.shake = true;
                        alarmDetails.walk = false;
                        alarmDetails.talk = false;
                        alarmDetails.numberShakes = noOfShakes;
                        AlarmIntent.putExtra("NoOfShakes", noOfShakes);
                        AlarmIntent.putExtra("TYPE", "SHAKE");
                    }
                    if(alarmType.equals("WALK"))
                    {
                        int noOfSteps = data.getIntExtra("NoOfSteps", 0);
                        alarmDetails.walk = true;
                        alarmDetails.shake = false;
                        alarmDetails.talk = false;
                        alarmDetails.numberSteps = noOfSteps;
                        AlarmIntent.putExtra("NoOfSteps", noOfSteps);
                        AlarmIntent.putExtra("TYPE", "WALK");
                    }
                    else
                    {
                        String recordFileName = data.getStringExtra("FileName");
                        alarmDetails.talk = true;
                        alarmDetails.shake = false;
                        alarmDetails.walk = false;
                        alarmDetails.talkFileName = recordFileName;
                        AlarmIntent.putExtra("FileName",recordFileName);
                        AlarmIntent.putExtra("TYPE", "TALK");
                    }
                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                setResult(RESULT_OK);
                finish();
                break;
            }
            case R.id.action_save_alarm: {
                updateAlarmFromDetails();
                AlarmDBHelper db = new AlarmDBHelper(this);
                if(alarmDetails.id == -1){
                    db.createAlarm(alarmDetails);
                }else{
                    db.updateAlarm(alarmDetails);
                }

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, alarmDetails.Hour);
                cal.set(Calendar.MINUTE, alarmDetails.Minute);
                int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
                millisecondsAlarmTime = cal.getTimeInMillis() + offset;


                AlarmManager AlmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                PendingIntent Sender = PendingIntent.getBroadcast(this, 0, AlarmIntent, 0);
                AlmMgr.set(AlarmManager.RTC_WAKEUP, millisecondsAlarmTime, Sender);

                setResult(RESULT_OK);
                finish();
                break;
            }
            case R.id.action_delete_alarm: {
                if(alarmDetails.id < 0){
                    setResult(RESULT_OK);
                    finish();
                    break;
                }else{
                    AlarmDBHelper db = new AlarmDBHelper(this);
                    db.deleteAlarm(alarmDetails.id);
                    setResult(RESULT_OK);
                    finish();
                    break;
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateAlarmFromDetails() {
        for (int i=0; i < 7; i++) {
            if (tempDays.contains(i)){
                alarmDetails.setDay(i, true);
            }else{
                alarmDetails.setDay(i, false);
            }
        }

        TimePicker timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
        alarmDetails.Minute = timePicker.getCurrentMinute();
        alarmDetails.Hour = timePicker.getCurrentHour();

        EditText edtName = (EditText) findViewById(R.id.alarm_details_name);
        alarmDetails.name = edtName.getText().toString();

        CheckBox checkVibrate = (CheckBox) findViewById(R.id.alarm_details_vibrate);
        alarmDetails.vibrations = checkVibrate.isChecked();

        alarmDetails.isEnabled = true;

    }
}
