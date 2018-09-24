package com.grupo214.usuario.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.DatabaseHelper;
import com.grupo214.usuario.fragment.NotificacionFragment;
import com.grupo214.usuario.objects.Alarm;


public class AMNotificacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_edit_alarm);
        final Context context = this;
        final String modo = getIntent().getExtras().getString("modo");

        // HARDCODEO
        final SparseBooleanArray dias = new SparseBooleanArray(7);
        dias.put(1, false);
        dias.put(2, false);
        dias.put(3, false);
        dias.put(4, false);
        dias.put(5, false);
        dias.put(6, false);
        dias.put(7, false);
        // HARDCODEO

        final EditText editText = (EditText) findViewById(R.id.edit_alarm_label);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.edit_alarm_time_picker);
        final ImageButton bt_guardar = (ImageButton) findViewById(R.id.bt_guardar_not);


        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("EditarAgregarNoti", timePicker.toString());
                Alarm alarm = null;
                if( modo.equals(NotificacionFragment.AGREGAR) ) {
                    DatabaseHelper.getInstance(context).addAlarm();
                    alarm = new Alarm(NotificacionFragment.getCountNotificacion(),
                            111111111,
                            editText.getText().toString(),
                            dias,
                            true);
                    NotificacionFragment.addNotificacion(alarm);
                }
                else if(modo.equals(NotificacionFragment.EDITAR)){
                    alarm = DatabaseHelper.getInstance(context).getAlarms().get(getIntent().getExtras().getInt("id"));
                    alarm.setLabel(editText.getText().toString());
                    DatabaseHelper.getInstance(context).updateAlarm(alarm);
                    NotificacionFragment.notifyDataSetChange();
                }


                final int rowsUpdated = DatabaseHelper.getInstance(context).updateAlarm(alarm);
                String messageId = (rowsUpdated == 1) ? "Guardado" :"Fallo";
                Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();

                finish();
            }
        });


    }
}
