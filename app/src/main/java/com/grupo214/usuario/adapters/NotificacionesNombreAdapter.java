package com.grupo214.usuario.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Alarm;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class NotificacionesNombreAdapter extends ArrayAdapter<Alarm> {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm", Locale.getDefault());
    private static final SimpleDateFormat AM_PM_FORMAT =
            new SimpleDateFormat("a", Locale.getDefault());

    public NotificacionesNombreAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Alarm curAlarm = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notif_row_simple, parent, false);


        final TextView tx_time = (TextView) convertView.findViewById(R.id.ar_time_simple);
        tx_time.setText(TIME_FORMAT.format(curAlarm.getTime()));

        final TextView tx_am_pm = (TextView) convertView.findViewById(R.id.ar_am_pm_simple);
        tx_am_pm.setText(AM_PM_FORMAT.format(curAlarm.getTime()));

        final TextView tx_label = (TextView) convertView.findViewById(R.id.ar_label_simple);
        tx_label.setText(curAlarm.getLabel());

        final TextView tx_dias = (TextView) convertView.findViewById(R.id.ar_days_simple);

        SparseBooleanArray diasActivos = curAlarm.getAllDays();
        String dias = "";
        dias += diasActivos.get(Alarm.MON) ? "Lu · " : "";
        dias += diasActivos.get(Alarm.TUES) ? "Ma · " : "";
        dias += diasActivos.get(Alarm.WED) ? "Mi · " : "";
        dias += diasActivos.get(Alarm.THURS) ? "Ju · " : "";
        dias += diasActivos.get(Alarm.FRI) ? "Vi · " : "";
        dias += diasActivos.get(Alarm.SAT) ? "Sa · " : "";
        dias += diasActivos.get(Alarm.SUN) ? "Do · " : "";
        int tamano = dias.length();
        if (tamano > 4)
            dias = dias.substring(0, tamano - 3);

        tx_dias.setText(dias);

        ImageView bt_onOff = (ImageView) convertView.findViewById(R.id.ar_icon_simple);
        bt_onOff.setImageResource(curAlarm.isEnabled() ? R.drawable.ic_alarm_on_black_24dp : R.drawable.ic_alarm_off_black_24dp);

        return convertView;
    }
}

