package com.grupo214.usuario.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.grupo214.usuario.R;
import com.grupo214.usuario.fragment.NotificacionFragment;
import com.grupo214.usuario.objects.Notificacion;

public class EditarNotificacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_edit_alarm);

        final SparseBooleanArray dias = new SparseBooleanArray(7);
        final EditText editText = (EditText) findViewById(R.id.edit_alarm_label);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.edit_alarm_time_picker);
        final ImageButton bt_guardar = (ImageButton) findViewById(R.id.bt_guardar_not);

        dias.put(1, false);
        dias.put(2, false);
        dias.put(3, false);
        dias.put(4, true);
        dias.put(5, false);
        dias.put(6, false);
        dias.put(7, false);

        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EditarNotificacion", timePicker.toString());
                NotificacionFragment.addNotificacion(
                        new Notificacion(NotificacionFragment.getCountNotificacion(),
                                111111111,
                                editText.getText().toString(),
                                dias,
                                true));
                finish();
            }
        });


    }
}
