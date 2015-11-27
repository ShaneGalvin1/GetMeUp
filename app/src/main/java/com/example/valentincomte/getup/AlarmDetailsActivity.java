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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * This class is the Activity for our create/update alarm screen
 * It features a timepicker, a spinner, an edittext, a checkbox, an tonepicker, a toast and some textviews
 * Its purpose it to let the user decide the content of its alarm.
 * This activity leads to 3 other activities: ShakeAlarmActivity, TalkAlarmActivity, WalkAlarmActivity
 * When everything is set for the user, he can either choose:
 *  - to cancel what he did by pressing the return button
 *  - to delete the alarm
 *  - to validate its choice
 * When a user validate an alarm it is created in the database and an alarm for the set time is created
 * via the alarmManager.
 * After its mission is completed, the AlarmDetailsActivity closes and the user gets back to the AlarmListActivity
 *
 * Please note that in the current version, the team was not able to make the alarm frequency work due
 * to big issues with the AlarmManager
 */

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


    // In this onCreate method we are setting up everything the Activity needs to run properly
    // Depending if an id was passed or no the activity will create a new AlarmModel or take on from the database
    // It will populate all its content with the default choices or the choices stored in the AlarmModel
    // The ringToneContainer will start an activity when clicked on an return a result that will be stored in the model
    // The daysPicker is a dialog containing a list of days and checkboxes. The list is updated with the model content
    //    at any time.
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
                for (int i = 0; i < 7; i++) {
                    if (dayList[i] == true) {
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

    // This method is used to setup the content for the spinner and to determine the actions
    // when an item from the spinner is used.
    // The spinner will determine the alarm type and each time one of its item is clicked it
    // will open a new activity corresponding to the item selected and wait for a result.
    // Before starting the new activities it will set the global value: alarmType, isShake, isWalk, isTalk
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

    // This method will handle the multiple cases where the activity needs to wait for another's results
    // On the alarmTone case the method will get back the uri and set the textView accordingly.
    // The alarmTone part was inspired from answers on stackOverflow
    // On the alarmType case the method will analyze the given type and set the models attributes and the
    // alarmIntent's attribute accordingly
    // Note: this method could easily be refactored and is currently missing the default alarm type
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO: replace with default alarm
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    TextView txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);
                    txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
                    break;
                }
                case 2: {
                    AlarmIntent = new Intent(AlarmDetailsActivity.this, AlarmReceiver.class);
                    String alarmType = data.getStringExtra("TYPE");
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
                    else if(alarmType.equals("WALK"))
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
                default: {
                    break;
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

    // This method is the method called when one of the action bar button is clicked
    // On a home click it simply returns to the AlarmListActivity
    // On a save click it will ask for an update of the model, create or update the alarm in the database
    // and then create two alarmManager to start the alarm at the given time and to toggle the airplane mode
    // if required. When done it goes back to the AlarmListActivity
    // On a delete click it will simply delete the given alarm if present and go back to the AlarmListActivity
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

                Intent AirplaneIntent = new Intent(AlarmDetailsActivity.this, AlarmReceiver.class);
                AirplaneIntent.putExtra("TYPE", "AIRPLANE");
                AlarmManager AlmMgr2 = (AlarmManager) getSystemService(ALARM_SERVICE);
                PendingIntent planeSender = PendingIntent.getBroadcast(this, 0, AirplaneIntent, 0);
                AlmMgr2.set(AlarmManager.RTC, millisecondsAlarmTime-25200000, planeSender);

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

    // This method is used to update the AlarmModel with the content the user set on the screen
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
