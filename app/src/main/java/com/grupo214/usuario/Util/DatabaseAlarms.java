package com.grupo214.usuario.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grupo214.usuario.objects.Alarm;
import com.grupo214.usuario.objects.ParadaAlarma;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;
import java.util.HashMap;


public final class DatabaseAlarms extends SQLiteOpenHelper {
    private static String TAG = "DatabaseAlarms";
    public static final String TABLE_PARADAS_ALARM = "paradas_alarm";
    static final String _ID = "_ID";
    static final String COL_TIME = "TIME";
    static final String COL_LABEL = "LABEL";
    static final String COL_MON = "LUNES";
    static final String COL_TUES = "MARTES";
    static final String COL_WED = "MIERCOLES";
    static final String COL_THURS = "JUEVES";
    static final String COL_FRI = "VIERNES";
    static final String COL_SAT = "SABADO";
    static final String COL_SUN = "DOMINGO";
    static final String COL_IS_ENABLED = "IS_ENABLED";
    static final String COL_ID_ALARMS = "ID_ALARMS";
    static final String COL_ID_PARADA = "ID_PARADA";
    static final String COL_ID_LINEA_ALARMS = "ID_LINEA_ALARMS";
    static final String COL_ID_RAMAL_ALARMS = "ID_RAMAL_ALARMS";
    static final String COL_LAT = "LAT";
    static final String COL_LNG = "LNG";
    static final String COL_ID_LINEA = "ID_LINEA";
    static final String COL_ID_RAMAL = "ID_RAMAL";
    static final String COL_DESCRIPCION = "DESCRIPCION";
    static final String COL_LINEA = "LINEA";
    static final String COL_CHECKED = "CHECKED";

    private static final String DATABASE_NAME = "alarms.db";
    private static final int VERSION = 1;
    private static final String TABLE_ALARM = "alarms";
    private static final String TABLE_RAMAL = "ramal";
    private static DatabaseAlarms sInstance = null;
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
            COL_ID_LINEA_ALARMS + " INTEGER NOT NULL, " +
            COL_ID_RAMAL_ALARMS + " INTEGER NOT NULL, " +
            COL_LAT + " DOUBLE NOT NULL, " +
            COL_LNG + " DOUBLE NOT NULL, " +
            COL_ID_PARADA + " INTEGER PRIMARY KEY , " +
            " FOREIGN KEY(" + COL_ID_ALARMS + ") REFERENCES " +
            TABLE_PARADAS_ALARM + "(" + _ID + ") " +
            ");";

    private final String CREATE_TABLE_LINEAS = "CREATE TABLE " + TABLE_RAMAL + " (" +
            COL_ID_LINEA + " INTEGER NOT NULL, " +
            COL_ID_RAMAL + " INTEGER PRIMARY KEY, " +
            COL_DESCRIPCION + " TEXT NOT NULL, " +
            COL_LINEA + " TEXT NOT NULL, " +
            COL_CHECKED + " INTEGER NOT NULL " +
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
        db.execSQL(CREATE_TABLE_LINEAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseAlarms.TABLE_ALARM);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseAlarms.TABLE_PARADAS_ALARM);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseAlarms.TABLE_RAMAL);
        db.execSQL(CREATE_ALARMS_TABLE);
        db.execSQL(CREATE_PARADAS_TABLE);
        db.execSQL(CREATE_TABLE_LINEAS);
    }


    public int updateRamal(String idRamal, Boolean check) {
        final String where = COL_ID_RAMAL + "=?";
        final String[] whereArgs = new String[]{idRamal};
        return getWritableDatabase()
                .update(TABLE_RAMAL, Util.toContentValues(check), where, whereArgs);
    }

    public int updateRamal(Ramal ramal) {
        final String where = COL_ID_RAMAL + "=?";
        final String[] whereArgs = new String[]{ramal.getIdRamal()};
        return getWritableDatabase()
                .update(TABLE_RAMAL, Util.toContentValues(ramal), where, whereArgs);
    }

    public long addRamal(Ramal ramal){
        return getWritableDatabase().insert(TABLE_RAMAL, null, Util.toContentValues(ramal));
    }

    public Ramal getRamal(String id) {

        Cursor c = null;
        final String where = COL_ID_RAMAL + "=?";
        final String[] whereArgs = new String[]{id};
        try {
            c = getReadableDatabase().query(TABLE_RAMAL, null, where, whereArgs,
                    null, null, null);
            return Util.buildRamal(c);
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }
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
            c = getReadableDatabase().query(DatabaseAlarms.TABLE_ALARM, null, null, null,
                    null, null, null);
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
            c = getReadableDatabase().query(TABLE_ALARM, null, where, whereArgs,
                    null, null, null);
            Alarm alarm = Util.buildAlarm(c);
            alarm.setParadaAlarmas(getParadas(alarm.getId()));
            return alarm;
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }
    }

    HashMap<String, ParadaAlarma> getParadas(int idAlarma) {
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

    public HashMap<String,Ramal> getRamales() {
        Cursor c = null;
        final String where = COL_CHECKED + "=?";
        final String[] whereArgs = new String[]{"1"};
        try {
            c = getReadableDatabase().query(TABLE_RAMAL, null, where, whereArgs,
                    null, null, null);
            return Util.buildRamales(c);
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }
    }

    public boolean exist(Ramal r) {
        Cursor c =  getReadableDatabase().query(TABLE_RAMAL, new String[] {"1"},
                COL_ID_RAMAL + "=\'" + r.getIdRamal()+"\'", null, null, null, null);
        c.moveToFirst();
        int count = c.getCount();
        Log.d(TAG,"cantidad: " + count );
       // if( count == 1 ){
         //   Log.d(TAG,c.getString(c.getColumnIndex(DatabaseAlarms.COL_DESCRIPCION)));
       // }
        return  c.getCount() == 1;
    }
}
