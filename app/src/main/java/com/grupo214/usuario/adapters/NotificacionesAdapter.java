package com.grupo214.usuario.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Notificacion;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class NotificacionesAdapter extends ArrayAdapter<Notificacion> {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm", Locale.getDefault());
    private static final SimpleDateFormat AM_PM_FORMAT =
            new SimpleDateFormat("a", Locale.getDefault());
    private final TextView txServicioBack;
    private ListView lv_listNotificaciones;
    private boolean heightAdjust = false;

    public NotificacionesAdapter(@NonNull Context context, int resource, TextView txServicioBack) {
        super(context, resource);
        this.txServicioBack = txServicioBack;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Notificacion n = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notif_row, parent, false);


        final TextView tx_linea = (TextView) convertView.findViewById(R.id.ar_time);
        tx_linea.setText(TIME_FORMAT.format(n.getTime()));

        final TextView tx_te_servicio = (TextView) convertView.findViewById(R.id.ar_am_pm);
        tx_te_servicio.setText(AM_PM_FORMAT.format(n.getTime()));

        final TextView tx_ramal = (TextView) convertView.findViewById(R.id.ar_label);
        tx_ramal.setText(n.getLabel());

        TextView tiempoEstimado = (TextView) convertView.findViewById(R.id.ar_days);
        tiempoEstimado.setText("Lu Ma Mi Ju Vi Sa Do"); // despues cambiar.-

        ImageView imageButton = (ImageView) convertView.findViewById(R.id.ar_icon);
        imageButton.setImageResource(n.isEnabled() ? R.drawable.ic_alarm_on_black_24dp : R.drawable.ic_alarm_off_black_24dp);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // editar o eliminar
                n.setEnabled(!n.isEnabled());




                // al final de guardar o quitar de la bbd
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (this.getCount() > 0) {
            txServicioBack.setVisibility(View.INVISIBLE);
        } else if (this.getCount() == 0) {
            txServicioBack.setVisibility(View.VISIBLE);
        }

        /*
        if (!heightAdjust && this.getCount() >= 4) { // asi entra una vez cuando sea mayor a tres.
            ViewGroup.LayoutParams params = lv_listNotificaciones.getLayoutParams();
            params.height = lv_listNotificaciones.getMeasuredHeight() + (lv_listNotificaciones.getDividerHeight() * (lv_listNotificaciones.getCount() - 1));
            lv_listNotificaciones.setLayoutParams(params);
            lv_listNotificaciones.requestLayout();
            heightAdjust = true;
        } else if (heightAdjust && this.getCount() < 4) {
            ViewGroup.LayoutParams params = lv_listNotificaciones.getLayoutParams();
            params.height = lv_listNotificaciones.getMeasuredHeight() + (lv_listNotificaciones.getDividerHeight() * (lv_listNotificaciones.getCount() - 1));
            lv_listNotificaciones.setLayoutParams(params);
            lv_listNotificaciones.requestLayout();
            heightAdjust = false;
        }*/
    }


    public void setLv(ListView lv) {
        this.lv_listNotificaciones = lv;
    }

}

