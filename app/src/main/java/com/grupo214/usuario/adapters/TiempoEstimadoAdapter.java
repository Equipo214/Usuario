package com.grupo214.usuario.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Servicio;


public class TiempoEstimadoAdapter extends ArrayAdapter<Servicio> {

    private GoogleMap googleMap;

    public TiempoEstimadoAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Servicio s = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tiempo_estimado_fila, parent, false);


        final TextView tx_linea = (TextView) convertView.findViewById(R.id.tx_te_linea);
        tx_linea.setText(s.getLinea());

        final TextView tx_ramal = (TextView) convertView.findViewById(R.id.tx_te_ramal);
        tx_ramal.setText(s.getRamal());

        TextView tiempoEstimado = (TextView) convertView.findViewById(R.id.tx_te);
        tiempoEstimado.setText(s.getTiempoEstimado());

        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.bt_te_servicio);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(s.getMk().getPosition()).zoom(13).build()));
            }
        });

        return convertView;
    }

    public void setGoogleMap(GoogleMap googleMaps) {
        this.googleMap = googleMaps;
    }
}
