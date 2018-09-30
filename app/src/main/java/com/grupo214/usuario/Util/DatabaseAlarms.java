package com.grupo214.usuario.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.grupo214.usuario.objects.Alarm;
import com.grupo214.usuario.objects.ParadaAlarma;

import java.util.ArrayList;


public final class DatabaseAlarms extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarms.db";
    private static DatabaseAlarms sInstance = null;
    private static final int VERSION = 1;
    public static final String TABLE_PARADAS_ALARM = "paradas_alarm";
    private static final String TABLE_ALARM = "alarms";


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

    static final String COL_ID_ALARMS = "id_alarms";
    static final String COL_ID_PARADA = "id_parada";
    static final String COL_ID_LINEA = "id_linea";
    static final String COL_ID_RAMAL = "id_ramal";
    static final String COL_LAT = "lat";
    static final String COL_LNG = "lng";


    final String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_ALARM + " (" +
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

    final String CREATE_PARADAS_TABLE = "CREATE TABLE " + TABLE_PARADAS_ALARM + " (" +
            COL_ID_ALARMS + " INTEGER NOT NULL, " +
            COL_ID_LINEA + " INTEGER NOT NULL, " +
            COL_ID_RAMAL + " INTEGER NOT NULL, " +
            COL_LAT + " DOUBLE NOT NULL, " +
            COL_LNG + " DOUBLE NOT NULL, " +
            COL_ID_PARADA + " INTEGER PRIMARY KEY , " +
            " FOREIGN KEY(" + COL_ID_ALARMS + ") REFERENCES " +
            TABLE_PARADAS_ALARM + "(" + _ID + ") " +
            ");";

    private DatabaseAlarms(Context context) {
        super(context, DATABASE_NAME, null, VERSION);

    }

    public static synchronized DatabaseAlarms getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseAlarms(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ALARMS_TABLE);
        db.execSQL(CREATE_PARADAS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseAlarms.TABLE_ALARM);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseAlarms.TABLE_PARADAS_ALARM);
        db.execSQL(CREATE_ALARMS_TABLE);
        db.execSQL(CREATE_PARADAS_TABLE);
    }


    public long addAlarm(Alarm alarm) {
        return getWritableDatabase().insert(TABLE_ALARM, null, Util.toContentValues(alarm));
    }

    public int updateAlarm(Alarm alarm) {
        final String where = _ID + "=?";
        final String[] whereArgs = new String[]{Long.toString(alarm.getId())};
        return getWritableDatabase()
                .update(TABLE_ALARM, Util.toContentValues(alarm), where, whereArgs);
    }

    public int deleteAlarm(long id) {
        String where = _ID + "=?";
        String[] whereArgs = new String[]{Long.toString(id)};
        getWritableDatabase().delete(TABLE_ALARM, where, whereArgs);
        where = COL_ID_ALARMS + "=?";
        return getWritableDatabase().delete(TABLE_PARADAS_ALARM, where, whereArgs);
    }

    public ArrayList<Alarm> getAlarms() {

        Cursor c = null;
        try {
            c = getReadableDatabase().query(DatabaseAlarms.TABLE_ALARM, null, null, null, null, null, null);
            ArrayList<Alarm> alarms = Util.buildAlarmList(c);
            setParadas(alarms);
            return alarms;
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }
    }

    public long addParadaAlarma(ParadaAlarma paradaAlarma) {
        return getWritableDatabase().insert(TABLE_PARADAS_ALARM, null, Util.toContentValues(paradaAlarma));
    }

    public Alarm getAlarm(long id) {

        Cursor c = null;
        final String where = _ID + "=?";
        final String[] whereArgs = new String[]{Long.toString(id)};
        try {
            c = getReadableDatabase().query(TABLE_ALARM, null, where, whereArgs, null, null, null);
            Alarm alarm = Util.buildAlarm(c);
            alarm.setParadaAlarmas(getParadas(alarm.getId()));
            return alarm;
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }
    }

    public ArrayList<ParadaAlarma> getParadas(int idAlarma) {
        Cursor c = null;

        final String where = COL_ID_ALARMS + "=?";
        final String[] whereArgs = new String[]{Long.toString(idAlarma)};
        try {
            c = getReadableDatabase().query(TABLE_PARADAS_ALARM, null, where, whereArgs, null, null, null);
            return Util.buildParadasList(c);
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }
    }

    public void setParadas(ArrayList<Alarm> listAlarms) {
        for (Alarm alarm : listAlarms)
            alarm.setParadaAlarmas(getParadas(alarm.getId()));
    }
}
