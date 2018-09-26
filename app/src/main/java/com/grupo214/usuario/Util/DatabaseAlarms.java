package com.grupo214.usuario.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grupo214.usuario.objects.Alarm;

import java.util.ArrayList;
import java.util.HashMap;


public final class DatabaseAlarms extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarms.db";
    private static final int SCHEMA = 1;

    private static final String TABLE_NAME = "alarms";

    static final String _ID = "_id";
    static final String COL_TIME = "time";
    static final String COL_LABEL = "label";
    static final String COL_MON = "lunes";
    static final String COL_TUES = "martes";
    static final String COL_WED = "miercoles";
    static final String COL_THURS = "jueves";
    static final String COL_FRI = "viernes";
    static final String COL_SAT = "sabado";
    static final String COL_SUN = "domingo";
    static final String COL_IS_ENABLED = "is_enabled";

    private static DatabaseAlarms sInstance = null;

    public static synchronized DatabaseAlarms getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseAlarms(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseAlarms(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.i(getClass().getSimpleName(), "Creating database...");

        final String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TIME + " INTEGER NOT NULL, " +
                COL_LABEL + " TEXT, " +
                COL_MON + " INTEGER NOT NULL, " +
                COL_TUES + " INTEGER NOT NULL, " +
                COL_WED + " INTEGER NOT NULL, " +
                COL_THURS + " INTEGER NOT NULL, " +
                COL_FRI + " INTEGER NOT NULL, " +
                COL_SAT + " INTEGER NOT NULL, " +
                COL_SUN + " INTEGER NOT NULL, " +
                COL_IS_ENABLED + " INTEGER NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(CREATE_ALARMS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        throw new UnsupportedOperationException("This shouldn't happen yet!");
    }


    public long addAlarm(Alarm alarm) {
        return getWritableDatabase().insert(TABLE_NAME, null, Util.toContentValues(alarm));
    }

    public int updateAlarm(Alarm alarm) {
        final String where = _ID + "=?";
        final String[] whereArgs = new String[] { Long.toString(alarm.getId()) };
        return getWritableDatabase()
                .update(TABLE_NAME, Util.toContentValues(alarm), where, whereArgs);
    }


    public int deleteAlarm(Alarm alarm) {
        return deleteAlarm(alarm.getId());
    }

    public int deleteAlarm(long id) {
        final String where = _ID + "=?";
        final String[] whereArgs = new String[] { Long.toString(id) };
        return getWritableDatabase().delete(TABLE_NAME, where, whereArgs);
    }

    public ArrayList<Alarm> getAlarms() {

        Cursor c = null;

        try{
            c = getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null);
            return Util.buildAlarmList(c);
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }
    }
    public Alarm getAlarm(long id) {

        Cursor c = null;
        final String where = _ID + "=?";
        final String[] whereArgs = new String[] { Long.toString(id) };
        try{
            c = getReadableDatabase().query (TABLE_NAME,null ,where,whereArgs,null,null,null);
            return Util.buildAlarm(c);
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }
    }
}
