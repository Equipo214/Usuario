package com.grupo214.usuario.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Servicio;


public class TiempoEstimadoAdapter extends ArrayAdapter<Servicio> {

    public TiempoEstimadoAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Servicio s = getItem(position);

        if( convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tiempo_estimado_fila, parent, false);

        TextView descripcion = (TextView) convertView.findViewById(R.id.tx_te_ramal);
        descripcion.setText("Servicio: " + s.getIdServicio() +" - " +s.getRamal());

        TextView tiempoEstimado = (TextView) convertView.findViewById(R.id.tx_te);
        tiempoEstimado.setText(s.getTiempoEstimado());
        return convertView;
    }

}
