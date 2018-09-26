package com.grupo214.usuario.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grupo214.usuario.objects.ParadaAlarma;

import java.util.ArrayList;

import static com.grupo214.usuario.Util.DatabaseAlarms._ID;


public final class DatabaseParadasAlarms extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarms.db";
    private static final int SCHEMA = 1;

    private static final String TABLE_NAME = "paradas_alarm";

    static final String COL_ID_ALARMS = "id_alarms";
    static final String COL_ID_PARADA = "id_parada";
    static final String COL_ID_LINEA = "id_linea";
    static final String COL_ID_RAMAL = "id_ramal";
    static final String COL_LAT = "lat";
    static final String COL_LNG = "lng";


    private static DatabaseParadasAlarms sInstance = null;

    private DatabaseParadasAlarms(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    public static synchronized DatabaseParadasAlarms getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseParadasAlarms(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.i(getClass().getSimpleName(), "Creating database...");

        final String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID_ALARMS + " INTEGER NOT NULL, " +
                COL_ID_LINEA + " INTEGER NOT NULL, " +
                COL_ID_RAMAL + " INTEGER NOT NULL, " +
                COL_LAT + " DOUBLE NOT NULL, " +
                COL_LNG + " DOUBLE NOT NULL, " +
                COL_ID_PARADA + " INTEGER PRIMARY KEY , " +
                " FOREIGN KEY(" + COL_ID_ALARMS + ") REFERENCES " +
                DatabaseParadasAlarms.TABLE_NAME + "(" + _ID + ") " +
                ");";

        sqLiteDatabase.execSQL(CREATE_ALARMS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        throw new UnsupportedOperationException("This shouldn't happen yet!");
    }


    public long addParadaAlarma(ParadaAlarma paradaAlarma) {
        return getWritableDatabase().insert(TABLE_NAME, null, Util.toContentValues(paradaAlarma));
    }

    public int updateParadaAlarma(ParadaAlarma paradaAlarma) {
        final String where = COL_ID_ALARMS + "=?";
        final String[] whereArgs = new String[]{paradaAlarma.getId_parada()};
        return getWritableDatabase()
                .update(TABLE_NAME, Util.toContentValues(paradaAlarma), where, whereArgs);
    }


    public int deleteParadaAlarma(long id) {
        final String where = COL_ID_ALARMS + "=?";
        final String[] whereArgs = new String[]{Long.toString(id)};
        return getWritableDatabase().delete(TABLE_NAME, where, whereArgs);
    }

    public ArrayList<ParadaAlarma> getParadas(Long idAlarma) {
        Cursor c = null;

        final String where = COL_ID_ALARMS + "=?";
        final String[] whereArgs = new String[]{Long.toString(idAlarma)};
        try {
            c = getReadableDatabase().query(TABLE_NAME, null, where, whereArgs, null, null, null);
            return Util.buildParadasList(c);
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }
    }


}
