package com.grupo214.usuario.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Servicio;


public class TiempoEstimadoAdapter extends ArrayAdapter<Servicio> {

    private final TextView txServicioBack;
    private GoogleMap googleMap;
    private ListView lv_listTiempoEstimado;
    private boolean heightAdjust = false;


    public TiempoEstimadoAdapter(@NonNull Context context, int resource,TextView txServicioBack) {
        super(context, resource);
        this.txServicioBack = txServicioBack;
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
        tiempoEstimado.setText(s.getTiempoEstimado()+"'");

        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.bt_te_servicio);
        imageButton.setImageResource(s.getIco());


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(s.getMk().getPosition()).zoom(13).build()));
            }
        });

        return convertView;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(this.getCount() > 0){
            txServicioBack.setVisibility(View.INVISIBLE);
        }
        else if( this.getCount()==0){
            txServicioBack.setVisibility(View.VISIBLE);
        }

        /*
        if (!heightAdjust && this.getCount() >= 4) { // asi entra una vez cuando sea mayor a tres.
            ViewGroup.LayoutParams params = lv_listTiempoEstimado.getLayoutParams();
            params.height = lv_listTiempoEstimado.getMeasuredHeight() + (lv_listTiempoEstimado.getDividerHeight() * (lv_listTiempoEstimado.getCount() - 1));
            lv_listTiempoEstimado.setLayoutParams(params);
            lv_listTiempoEstimado.requestLayout();
            heightAdjust = true;
        } else if (heightAdjust && this.getCount() < 4) {
            ViewGroup.LayoutParams params = lv_listTiempoEstimado.getLayoutParams();
            params.height = lv_listTiempoEstimado.getMeasuredHeight() + (lv_listTiempoEstimado.getDividerHeight() * (lv_listTiempoEstimado.getCount() - 1));
            lv_listTiempoEstimado.setLayoutParams(params);
            lv_listTiempoEstimado.requestLayout();
            heightAdjust = false;
        }*/
    }

    public void setGoogleMap(GoogleMap googleMaps) {
        this.googleMap = googleMaps;
    }

    public void setLv(ListView lv) {
        this.lv_listTiempoEstimado = lv;
    }

}

