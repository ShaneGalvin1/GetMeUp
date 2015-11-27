package com.example.valentincomte.getup;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;

public class AlarmModel {

    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRDIAY = 4;
    public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;

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

    public boolean[] getDays(){
        return Days;
    }

    public void setDay(int day, boolean value) {
        Days[day] = value;
    }

    public boolean getDay(int day) {
        return Days[day];
    }

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
