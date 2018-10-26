package com.grupo214.usuario.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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


public class CrearYEditarNotificacionActivity extends AppCompatActivity {

    private static final int ZONE_HOUR = -3;
    public static String EXTRA_ID_ALARMA = "EXTRA_ID_ALARMA";
    SparseBooleanArray dias;
    EditText editText;
    TextView tx_parada_fixed_not_add;
    TimePicker timePicker;
    ImageButton bt_guardar;
    CheckBox lunes;
    CheckBox martes;
    CheckBox miercoles;
    CheckBox jueves;
    CheckBox viernes;
    CheckBox sabado;
    CheckBox domingo;
    RecyclerView rw_paradas;
    String modo;
    ParadasListaAdapter paradasListaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_alarm);
        modo = getIntent().getExtras().getString("modo");
        editText = (EditText) findViewById(R.id.edit_alarm_label);
        tx_parada_fixed_not_add = (TextView) findViewById(R.id.tx_parada_fixed_not_add);
        timePicker = (TimePicker) findViewById(R.id.edit_alarm_time_picker);
        bt_guardar = (ImageButton) findViewById(R.id.bt_guardar_not);
        lunes = (CheckBox) findViewById(R.id.edit_alarm_mon);
        martes = (CheckBox) findViewById(R.id.edit_alarm_tues);
        miercoles = (CheckBox) findViewById(R.id.edit_alarm_wed);
        jueves = (CheckBox) findViewById(R.id.edit_alarm_thurs);
        viernes = (CheckBox) findViewById(R.id.edit_alarm_fri);
        sabado = (CheckBox) findViewById(R.id.edit_alarm_sat);
        domingo = (CheckBox) findViewById(R.id.edit_alarm_sun);
        rw_paradas = (RecyclerView) findViewById(R.id.list_paradas_noti);
        dias = new SparseBooleanArray(7);
        tx_parada_fixed_not_add.setVisibility(TextView.INVISIBLE);

        if (modo.equals(EDITAR)) {
            int id = getIntent().getExtras().getInt("id");
            Alarm alarm = DatabaseAlarms.getInstance(this).getAlarm(id);
            paradasListaAdapter = new ParadasListaAdapter(this, alarm.getParadaAlarmas());
            rw_paradas.setAdapter(paradasListaAdapter);
            editText.setText(alarm.getLabel());
            lunes.setChecked(alarm.getDay(Alarm.MON));
            martes.setChecked(alarm.getDay(Alarm.TUES));
            miercoles.setChecked(alarm.getDay(Alarm.WED));
            jueves.setChecked(alarm.getDay(Alarm.THURS));
            viernes.setChecked(alarm.getDay(Alarm.FRI));
            sabado.setChecked(alarm.getDay(Alarm.SAT));
            domingo.setChecked(alarm.getDay(Alarm.SUN));

            long milis = alarm.getTime();
            int minutos = (int) ((milis / (1000 * 60)) % 60);
            int horas = (int) ((milis / (1000 * 60 * 60)) % 24);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(horas + ZONE_HOUR);
                timePicker.setMinute(minutos);
            } else {
                timePicker.setCurrentHour(horas + ZONE_HOUR);
                timePicker.setCurrentMinute(minutos);
            }

            if (alarm.getParadaAlarmas().size() > 0) {
                String mensaje = "Paradas: " + alarm.getParadaAlarmas().size() + ".";
                tx_parada_fixed_not_add.setVisibility(TextView.VISIBLE);
                tx_parada_fixed_not_add.setText(mensaje);

                // ACA TENGO FIXEAR.
                /*
                rw_paradas.setOnTouchListener(new View.OnTouchListener() {
                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });*/
                // setListViewHeightBasedOnChildren(rw_paradas);
            }
            paradasListaAdapter.notifyDataSetChanged();
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
        alt_bld.setMessage("¿Desea salir? Los cambios no guardados se perderan");
        alt_bld.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //guardar();
                CrearYEditarNotificacionActivity.super.onBackPressed();
                dialog.dismiss();
            }
        });
        alt_bld.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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

        final Calendar calendar = Calendar.getInstance();
        if (modo.equals(AGREGAR)) {
            dias.put(Alarm.MON, lunes.isChecked());
            dias.put(Alarm.TUES, martes.isChecked());
            dias.put(Alarm.WED, miercoles.isChecked());
            dias.put(Alarm.THURS, jueves.isChecked());
            dias.put(Alarm.FRI, viernes.isChecked());
            dias.put(Alarm.SAT, sabado.isChecked());
            dias.put(Alarm.SUN, sabado.isChecked());
            alarm = new Alarm(NotificacionFragment.getCountNotificacion(),
                    calendar.getTimeInMillis(),
                    editText.getText().toString(),
                    dias,
                    true);
            DatabaseAlarms.getInstance(this).addAlarm(alarm);
            NotificacionFragment.addNotificacion(alarm);
        } else if (modo.equals(EDITAR)) {
            alarm = DatabaseAlarms.getInstance(this).getAlarm(getIntent().getExtras().getInt("id"));
            SparseBooleanArray dias = new SparseBooleanArray(7);
            dias.append(Alarm.MON, lunes.isChecked());
            dias.append(Alarm.TUES, martes.isChecked());
            dias.append(Alarm.WED, miercoles.isChecked());
            dias.append(Alarm.THURS, jueves.isChecked());
            dias.append(Alarm.FRI, viernes.isChecked());
            dias.append(Alarm.SAT, sabado.isChecked());
            dias.append(Alarm.SUN, domingo.isChecked());
            alarm.setAllDays(dias);
            alarm.setLabel(editText.getText().toString());
        } else
            return;

        int hour = Util.getTimePickerHour(timePicker);
        int minute = Util.getTimePickerMinute(timePicker);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        alarm.setTime(calendar.getTimeInMillis());


        final int rowsUpdated = DatabaseAlarms.getInstance(this).updateAlarm(alarm);
        String messageId;
        if (rowsUpdated == 1) {
            crearAlarma(alarm);
            messageId = "Guardado";
        } else
            messageId = "Fallo";

        // Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
        NotificacionFragment.notifyDataSetChange(this);
        finish();
    }

    void crearAlarma(Alarm alarm) {

        AlarmManager alarmMgr;

        for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++) {

            if (!(alarm.getAllDays().get(dayOfWeek))) {
                continue;
            }
            PendingIntent pendingIntent;
            Intent intent = new Intent(this, CheckPostsReceiver.class);
            intent.putExtra(EXTRA_ID_ALARMA, alarm.getId());
            pendingIntent = PendingIntent.getBroadcast(this, alarm.getId() + dayOfWeek, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // seteo hora y dia.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(alarm.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

            long alarm_time = calendar.getTimeInMillis();

            if (calendar.before(Calendar.getInstance()))
                alarm_time += AlarmManager.INTERVAL_DAY * 7; // si la configuro antes de la hora actual, la crea lasemana que viene.

            alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
            assert alarmMgr != null;

            Log.d("AMN", "Creo esto: " + alarm_time);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alarmMgr.setAlarmClock(new AlarmManager.AlarmClockInfo(alarm_time, pendingIntent), pendingIntent);
                //    AlarmManager.AlarmClockInfo alarmClockInfo = alarmMgr.getNextAlarmClock();
                //    Log.d("AMN", "Zonara esto: " + alarmClockInfo.getTriggerTime());
            }


        }

        Toast.makeText(this, "Alarma guardada", Toast.LENGTH_SHORT).show();
    }

}
