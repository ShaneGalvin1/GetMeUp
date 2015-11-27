package com.example.shane.getmeup;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.*;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {
    GoogleAccountCredential mCredential;
    private TextView title;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };


    private List<Alarm> alarmList = new ArrayList<Alarm>();
    private ListView listView;
    private CalendarAdapter adapter;
    private Alarm temp;
    /**onst
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar API ...");

        setContentView(R.layout.activity_main);

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        title = (TextView) findViewById(R.id.title_calendar);
        title.setText("");

        listView = (ListView) findViewById(R.id.event_list);
        // Create Adapter
        adapter = new CalendarAdapter(this);


    }


    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            title.setText("Google Play Services required: " +
                    "after installing, close and relaunch this app.");
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    title.setText("Account unspecified.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                listView.setAdapter(null);
                new MakeRequestTask(mCredential).execute();
            } else {
                title.setText("No network connection available.");
            }
        }
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                MainActivity.this,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Get next event for each calendar user has
         * Check which event is the closest
         * Return Name and Time of event
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());

            List<String> eventStrings = new ArrayList<String>();
            List<String> calendarIds = new ArrayList<String>();
            String pageToken = null;
            CalendarList calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> calendarItems = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : calendarItems) {
                if(!calendarListEntry.getSummary().contains("Holidays in")) {
                    calendarIds.add(calendarListEntry.getId());
                }
            }
            String firstEvent = "";
            String lastAdded = "";
            DateTime max = new DateTime(System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(1));
            for(int j = 0; j < 7; j++)
            {
                now = new DateTime(System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(j));
                max = new DateTime(System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(j+1));
                for(int i = 0; i < calendarIds.size(); i++)
                {
                    Events events = mService.events().list(calendarIds.get(i))
                            .setMaxResults(1)
                            .setTimeMin(now)
                            .setTimeMax(max)
                            .setOrderBy("startTime")
                            .setSingleEvents(true)
                            .execute();
                    List<Event> items = events.getItems();
                    long first = 0;

                    for (Event event : items) {
                        DateTime start = event.getStart().getDateTime();


                        if (start != null) {
                            if(first == 0)
                            {
                                first = start.getValue();

                                firstEvent = event.getSummary() + "=" + start;
                            }
                            if(first > start.getValue())
                            {
                                firstEvent = event.getSummary() + "=" + start;;
                            }
                        }
                    }
                }
                if(eventStrings.isEmpty()) {
                    eventStrings.add(
                            firstEvent);
                    lastAdded = firstEvent;
                }
                else if(!lastAdded.equals(firstEvent)) {
                    eventStrings.add(
                            firstEvent);
                    lastAdded = firstEvent;
                }
            }
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();

            if (output == null || output.size() == 0) {
                //No Results Found
                title.setText("No Upcoming Events");
            } else {

                //Populate AlarmList for List of events
                String s = "";
                String name = "";
                String dt = "";
                for(int i = 0; i < output.size(); i++) {
                    s = output.get(i);
                    if(s != null) {

                        title.setText("Upcoming Events");
                        Pattern pattern = Pattern.compile("= *");
                        Matcher matcher = pattern.matcher(s);
                        if (matcher.find()) {
                            name = s.substring(0, matcher.start());
                            dt = s.substring(matcher.end());
                        }
                        String[] str_array = s.split("=");
                        if(str_array.length > 0) {
                            pattern = Pattern.compile("T *");
                            matcher = pattern.matcher(dt);
                            String time = "";
                            String day = "";
                            Date d = new Date(0,0,0);
                            if (matcher.find()) {
                                day = dt.substring(0, matcher.start());
                                time = dt.substring(matcher.end());
                                time = time.substring(0, 5);
                                d = Date.valueOf(day);
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
                            String dayOfTheWeek = sdf.format(d);
                            Alarm temp = new Alarm(name, dayOfTheWeek, time, true, false, false, false, false);
                            if(temp.getTime() != "" && temp .getDays() != "") {
                                alarmList.add(temp);
                            }

                        }

                    }
                    else {
                        title.setText("No Upcoming Events");
                    }
                }

                adapter.setList(alarmList);
                listView.setAdapter(adapter);

                listView.setFocusable(false);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);



                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        long viewId = view.getId();

                        if (viewId == R.id.create) {
                            // Create new alarm in db
                            temp = alarmList.get(position);
                            temp.setOn(true);
                            alarmList.set(position, temp);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    title.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                title.setText("Request cancelled.");
            }
        }
    }
}