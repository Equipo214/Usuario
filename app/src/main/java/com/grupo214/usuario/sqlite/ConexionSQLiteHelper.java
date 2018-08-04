package com.grupo214.usuario.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.grupo214.usuario.objects.LineaDemo;

import java.util.ArrayList;

/**
 * Clase beta para guardar contenido de la app. quizas muera como ella.
 * @author  Daniel Boullon
 */
public class ConexionSQLiteHelper extends SQLiteOpenHelper {


    public ConexionSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UtilSQLite.CREARTABLALINEA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UtilSQLite.DROPTABLALINEA);
        onCreate(db);
    }

    /**
     * Cargar una LineaDemo a la base de datos local.
     * @param lineaDemo
     * @return
     */
    public Long insertarLinea(LineaDemo lineaDemo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(UtilSQLite.CAMPO_ID, lineaDemo.getId());
        values.put(UtilSQLite.CAMPOS_LINEA, lineaDemo.getLinea());
        values.put(UtilSQLite.CAMPO_RAMAL, lineaDemo.getRamal());
        values.put(UtilSQLite.CAMPO_CHECKED, lineaDemo.isCheck()?1:0);

        Long idLite = db.insert(UtilSQLite.TABLA_LINEA, UtilSQLite.CAMPO_ID, values);
        db.close();
        return idLite;
    }


    /**
     * No esta terminada
     * @param id
     * @param lineaDemos
     * @return
     */
    public LineaDemo consultarLineaByID(int id, ArrayList<LineaDemo> lineaDemos) {
        SQLiteDatabase db = getReadableDatabase();
        String[] campos = {UtilSQLite.CAMPOS_LINEA, UtilSQLite.CAMPO_RAMAL};
        String[] parametros = {Integer.toString(id)};

        Cursor cursor = db.query(UtilSQLite.TABLA_LINEA, campos, UtilSQLite.CAMPO_ID + "=?", parametros, null, null, null);
        cursor.moveToFirst();
        db.close();

        return lineaDemos.get(id);
    }

    public ArrayList<LineaDemo> cargarLineas(){
         ArrayList<LineaDemo> mLineaDemos = new ArrayList<>();
        /*
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM "+ UtilSQLite.TABLA_LINEA,null);
        while( cursor.moveToNext()){
            mLineaDemos.add( new LineaDemo(cursor.getInt(0),
                    cursor.getString(1)
                    ,cursor.getString(2),
                    cursor.getInt(3)==1));
        }
        db.close();*/
        return mLineaDemos;
    }

}
