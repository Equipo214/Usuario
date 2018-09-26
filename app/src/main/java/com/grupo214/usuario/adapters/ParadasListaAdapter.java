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
import com.grupo214.usuario.objects.ParadaAlarma;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class ParadasListaAdapter extends ArrayAdapter<ParadaAlarma> {

    public ParadasListaAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ParadaAlarma paradaAlarma = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notif_row_simple, parent, false);

        final TextView tx_linea = (TextView) convertView.findViewById(R.id.tx_linea_parada_not);
        tx_linea.setText(paradaAlarma.getLinea());

        final TextView tx_ramal = (TextView) convertView.findViewById(R.id.tx_ramal_parada_not);
        tx_ramal.setText(paradaAlarma.getRamal());


        ImageView bt_onOff = (ImageView) convertView.findViewById(R.id.bt_parada_not_edit);
      //  bt_onOff.setImageResource(paradaAlarma.isEnable() ? R.mipmap.ic_bt_parada_not : R.mipmap.ic_bt_parada_not_disable);

        // click comun alternar.
        // long click eliminar.

        return convertView;
    }
}

