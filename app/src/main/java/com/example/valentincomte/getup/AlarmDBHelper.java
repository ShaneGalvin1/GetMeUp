package com.example.valentincomte.getup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.example.valentincomte.getup.AlarmContract.Alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by valentincomte on 24/11/2015.
 */
public class AlarmDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "alarmclock.db";

    private static final String SQL_CREATE_ALARM = "CREATE TABLE " + Alarm.TABLE_NAME + " (" +
            Alarm._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Alarm.COLUMN_NAME_ALARM_NAME + " TEXT," +
            Alarm.COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER," +
            Alarm.COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER," +
            Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS + " TEXT," +
            Alarm.COLUMN_NAME_ALARM_TONE + " TEXT," +
            Alarm.COLUMN_NAME_ALARM_ENABLED + " BOOLEAN," +
            Alarm.COLUMN_NAME_ALARM_SHAKE + " BOOLEAN," +
            Alarm.COLUMN_NAME_ALARM_NUMBER_SHAKES + " INTEGER," +
            Alarm.COLUMN_NAME_ALARM_TALK + " BOOLEAN," +
            Alarm.COLUMN_NAME_ALARM_TALK_FILENAME + " TEXT," +
            Alarm.COLUMN_NAME_ALARM_WALK + " BOOLEAN," +
            Alarm.COLUMN_NAME_ALARM_NUMBER_STEPS + " INTEGER,"+
            Alarm.COLUMN_NAME_ALARM_VIBRATE + " BOOLEAN" +
            " )";

    private static final String SQL_DELETE_ALARM =
            "DROP TABLE IF EXISTS " + Alarm.TABLE_NAME;

    public AlarmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALARM);
        onCreate(db);
    }

    private AlarmModel populateModel(Cursor c) {
        AlarmModel model = new AlarmModel();
        model.id = c.getLong(c.getColumnIndex(Alarm._ID));
        model.name = c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_NAME));
        model.Hour = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_HOUR));
        model.Minute = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE));
        model.alarmTone = c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TONE)) != "" ? Uri.parse(c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TONE))) : null;
        model.isEnabled = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_ENABLED)) == 0 ? false : true;
        model.shake = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_SHAKE)) == 0 ? false : true;
        model.numberShakes = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_NUMBER_SHAKES));
        model.walk = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_WALK)) == 0 ? false : true;
        model.numberSteps = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_NUMBER_STEPS));
        model.talk = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TALK)) == 0 ? false : true;
        model.talkFileName = c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TALK_FILENAME));
        model.vibrations = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_VIBRATE)) == 0 ? false : true;

        String[] repeatingDays = c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS)).split(",");
        for (int i = 0; i < repeatingDays.length; ++i) {
            model.setDay(i, repeatingDays[i].equals("false") ? false : true);
        }

        return model;
    }

    private ContentValues populateContent(AlarmModel model) {
        ContentValues values = new ContentValues();
        values.put(Alarm.COLUMN_NAME_ALARM_NAME, model.name);
        values.put(Alarm.COLUMN_NAME_ALARM_TIME_HOUR, model.Hour);
        values.put(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE, model.Minute);
        values.put(Alarm.COLUMN_NAME_ALARM_TONE, model.alarmTone != null ? model.alarmTone.toString() : "");
        values.put(Alarm.COLUMN_NAME_ALARM_ENABLED, model.isEnabled);
        values.put(Alarm.COLUMN_NAME_ALARM_WALK, model.walk);
        values.put(Alarm.COLUMN_NAME_ALARM_NUMBER_STEPS, model.numberSteps);
        values.put(Alarm.COLUMN_NAME_ALARM_SHAKE, model.shake);
        values.put(Alarm.COLUMN_NAME_ALARM_NUMBER_SHAKES, model.numberShakes);
        values.put(Alarm.COLUMN_NAME_ALARM_TALK, model.talk);
        values.put(Alarm.COLUMN_NAME_ALARM_TALK_FILENAME, model.talkFileName);
        values.put(Alarm.COLUMN_NAME_ALARM_VIBRATE, model.vibrations);

        String repeatingDays = "";
        for (int i = 0; i < 7; ++i) {
            repeatingDays += model.getDay(i) + ",";
        }
        values.put(Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS, repeatingDays);

        return values;
    }

    public long createAlarm(AlarmModel model) {
        ContentValues values = populateContent(model);
        return getWritableDatabase().insert(Alarm.TABLE_NAME, null, values);
    }

    public long updateAlarm(AlarmModel model) {
        ContentValues values = populateContent(model);
        return getWritableDatabase().update(Alarm.TABLE_NAME, values, Alarm._ID + " = ?", new String[] { String.valueOf(model.id) });
    }

    public AlarmModel getAlarm(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Alarm.TABLE_NAME + " WHERE " + Alarm._ID + " = " + id;

        Cursor c = db.rawQuery(select, null);

        if (c.moveToNext()) {
            return populateModel(c);
        }

        return null;
    }

    public List<AlarmModel> getAlarms() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Alarm.TABLE_NAME;

        Cursor c = db.rawQuery(select, null);

        List<AlarmModel> alarmList = new ArrayList<AlarmModel>();

        while (c.moveToNext()) {
            alarmList.add(populateModel(c));
        }

        if (!alarmList.isEmpty()) {
            return alarmList;
        }

        return null;
    }

    public int deleteAlarm(long id) {
        return getWritableDatabase().delete(Alarm.TABLE_NAME, Alarm._ID + " = ?", new String[] { String.valueOf(id) });
    }
}
