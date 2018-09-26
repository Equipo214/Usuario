package com.grupo214.usuario.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.DatabaseAlarms;
import com.grupo214.usuario.Util.Util;
import com.grupo214.usuario.adapters.ParadasListaAdapter;
import com.grupo214.usuario.alarma.CheckPostsReceiver;
import com.grupo214.usuario.fragment.NotificacionFragment;
import com.grupo214.usuario.objects.Alarm;

import java.util.Calendar;

import static com.grupo214.usuario.fragment.NotificacionFragment.AGREGAR;
import static com.grupo214.usuario.fragment.NotificacionFragment.EDITAR;


public class AMNotificacion extends AppCompatActivity {

    SparseBooleanArray dias;
    EditText editText;
    TimePicker timePicker;
    ImageButton bt_guardar;
    CheckBox lunes;
    CheckBox martes;
    CheckBox miercoles;
    CheckBox jueves;
    CheckBox viernes;
    CheckBox sabado;
    CheckBox domingo;
    ListView paradas;
    String modo;
    ParadasListaAdapter paradasListaAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_edit_alarm);
        modo = getIntent().getExtras().getString("modo");
        editText = (EditText) findViewById(R.id.edit_alarm_label);
        timePicker = (TimePicker) findViewById(R.id.edit_alarm_time_picker);
        bt_guardar = (ImageButton) findViewById(R.id.bt_guardar_not);
        lunes = (CheckBox) findViewById(R.id.edit_alarm_mon);
        martes = (CheckBox) findViewById(R.id.edit_alarm_tues);
        miercoles = (CheckBox) findViewById(R.id.edit_alarm_wed);
        jueves = (CheckBox) findViewById(R.id.edit_alarm_thurs);
        viernes = (CheckBox) findViewById(R.id.edit_alarm_fri);
        sabado = (CheckBox) findViewById(R.id.edit_alarm_sat);
        domingo = (CheckBox) findViewById(R.id.edit_alarm_sun);
        paradas = (ListView) findViewById(R.id.list_paradas_noti);
        dias = new SparseBooleanArray(7);
        paradasListaAdapter = new ParadasListaAdapter(this, android.R.layout.simple_list_item_2);
        paradas.setAdapter(paradasListaAdapter);

        if (modo.equals(EDITAR)) {
            Alarm alarm = DatabaseAlarms.getInstance(this).getAlarm(getIntent().getExtras().getInt("id"));
            editText.setText(alarm.getLabel());
            lunes.setChecked(alarm.getDay(Alarm.MON));
            martes.setChecked(alarm.getDay(Alarm.TUES));
            miercoles.setChecked(alarm.getDay(Alarm.WED));
            jueves.setChecked(alarm.getDay(Alarm.THURS));
            viernes.setChecked(alarm.getDay(Alarm.FRI));
            sabado.setChecked(alarm.getDay(Alarm.SAT));
            domingo.setChecked(alarm.getDay(Alarm.SUN));
        }


        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);

        alt_bld.setTitle("Salir");
        alt_bld.setMessage("¿Desea guardar los cambios?");
        alt_bld.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                guardar();
                dialog.dismiss();
            }
        });
        alt_bld.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AMNotificacion.super.onBackPressed();
            }
        });

        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void guardar() {
        if (editText.getText().toString().length() == 0) {
            Toast.makeText(this, "Ingrese un nombre", Toast.LENGTH_LONG).show();
            return;
        }

        Alarm alarm = null;

        final Calendar time = Calendar.getInstance();
        if (modo.equals(AGREGAR)) {
            time.set(Calendar.MINUTE, Util.getTimePickerMinute(timePicker));
            time.set(Calendar.HOUR_OF_DAY, Util.getTimePickerHour(timePicker));
            dias.put(Alarm.MON, lunes.isChecked());
            if(lunes.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
            dias.put(Alarm.TUES, martes.isChecked());
            if(martes.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
            dias.put(Alarm.WED, miercoles.isChecked());
            if(miercoles.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
            dias.put(Alarm.THURS, jueves.isChecked());
            if(jueves.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
            dias.put(Alarm.FRI, viernes.isChecked());
            if(viernes.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
            dias.put(Alarm.SAT, sabado.isChecked());
            if(sabado.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
            dias.put(Alarm.SUN, sabado.isChecked());
            if(sabado.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            alarm = new Alarm(NotificacionFragment.getCountNotificacion(),
                    time.getTimeInMillis(),
                    editText.getText().toString(),
                    dias,
                    true);
            DatabaseAlarms.getInstance(this).addAlarm(alarm);
            NotificacionFragment.addNotificacion(alarm);
        } else if (modo.equals(EDITAR)) {
            alarm = DatabaseAlarms.getInstance(this).getAlarm(getIntent().getExtras().getInt("id"));
            time.set(Calendar.MINUTE, Util.getTimePickerMinute(timePicker));
            time.set(Calendar.HOUR_OF_DAY, Util.getTimePickerHour(timePicker));
            SparseBooleanArray dias = new SparseBooleanArray(7);
            dias.append(Alarm.MON, lunes.isChecked());
            if(lunes.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
            dias.append(Alarm.TUES, martes.isChecked());
            if(martes.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
            dias.append(Alarm.WED, miercoles.isChecked());
            if(miercoles.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
            dias.append(Alarm.THURS, jueves.isChecked());
            if(jueves.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
            dias.append(Alarm.FRI, viernes.isChecked());
            if(viernes.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
            dias.append(Alarm.SAT, sabado.isChecked());
            if(sabado.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
            dias.append(Alarm.SUN, domingo.isChecked());
            if(sabado.isChecked())
                time.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            alarm.setAllDays(dias);
            alarm.setTime(time.getTimeInMillis());
            alarm.setLabel(editText.getText().toString());
        }

        final int rowsUpdated = DatabaseAlarms.getInstance(this).updateAlarm(alarm);
        String messageId;
        if (rowsUpdated == 1) {
            // crear alarma con el manger
            averAlcine(time);
            messageId = "Guardado";
        } else
            messageId = "Fallo";

       // Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
        NotificacionFragment.notifyDataSetChange(this);
        finish();
    }

    void averAlcine(Calendar calendar) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, CheckPostsReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 5, alarmIntent);
        Toast.makeText(this, "Alarma guardada", Toast.LENGTH_SHORT).show();

    }
}
