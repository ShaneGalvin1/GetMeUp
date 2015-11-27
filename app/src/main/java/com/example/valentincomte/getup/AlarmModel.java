package com.example.valentincomte.getup;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is our model of alarm
 * It contains every attribute we need to describe the alarm.
 * It has a default constructor and most of its attribute are public
 * The only private attribute is the Days boolean table. This table is associated with the
 * DAYS_STRINGS array to retrieve the names of the days.
 * The class features the method to access the Days table and to get the names of the days
 */
public class AlarmModel {

    static String[] array = new String[] {"Monday", "Tuesday", "Wednesay", "Thursday", "Friday", "Saturday", "Sunday"};
    public static final ArrayList<String> DAYS_STRINGS = new ArrayList<String>(Arrays.asList(array));


    public long id;
    public int Hour;
    public int Minute;
    public boolean vibrations;
    private boolean Days[];
    public Uri alarmTone;
    public String name;
    public boolean isEnabled;
    public boolean walk;
    public int numberSteps;
    public boolean shake;
    public int numberShakes;
    public boolean talk;
    public String talkFileName;
    public boolean puzzle;

    // Standard constructor with default values
    public AlarmModel() {
        this.id = -1;
        this.Hour = 7;
        this.Minute = 0;
        this.vibrations = false;
        this.name = "new alarm";
        this.isEnabled = true;
        this.walk = false;
        this.numberSteps = 0;
        this.talk = false;
        this.talkFileName = "";
        this.shake = false;
        this.numberShakes = 0;
        this.puzzle = false;
        Days = new boolean[7];
    }

    // This is a getter for Days
    public boolean[] getDays(){
        return Days;
    }

    // This will set the value for a specific element in Days
    public void setDay(int day, boolean value) {
        Days[day] = value;
    }

    // THis will get the value for a specific element in Days
    public boolean getDay(int day) {
        return Days[day];
    }

    // This is used to create a string containing the reduced version of the list of days
    public String getStringDays(){
        String days = "";
        if(Days[0]){ days += "Mon ";}
        if(Days[1]){ days += "Tue ";}
        if(Days[2]){ days += "Wed ";}
        if(Days[3]){ days += "Thu ";}
        if(Days[4]){ days += "Fri ";}
        if(Days[5]){ days += "Sat ";}
        if(Days[6]){ days += "Sun ";}
        return days;
    }
}
