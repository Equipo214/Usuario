package com.grupo214.usuario.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.DatabaseAlarms;
import com.grupo214.usuario.activities.MainActivity;
import com.grupo214.usuario.fragment.LineasFragment;
import com.grupo214.usuario.fragment.MapFragment;
import com.grupo214.usuario.fragment.NotificacionFragment;
import com.grupo214.usuario.objects.ParadaAlarma;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParadasListaSimpleAdapter extends RecyclerView.Adapter<ParadasListaSimpleAdapter.ViewHolder> {

    private static String TAG = "ParadasListaSimpleAdapter";
    private List<ParadaAlarma> mData;
    private LayoutInflater mInflater;
    private MapFragment mapFragment;
    private ViewPager tabViewPager;
    private Context context;
    private LineasFragment lineasFragment;

    // data is passed into the constructor
    ParadasListaSimpleAdapter(Context context, HashMap<String, ParadaAlarma> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = new ArrayList<>();
        this.mData.addAll(data.values());
    }

    public void setTabViewPager(ViewPager tabViewPager) {
        this.tabViewPager = tabViewPager;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.parada_edit_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ParadaAlarma paradaAlarma = mData.get(position);
        Ramal r = DatabaseAlarms.getInstance(context).getRamal(paradaAlarma.getIdRamal());
        holder.tx_linea.setText(r.getLinea());
        holder.tx_ramal.setText(r.getDescripcion());
        holder.btImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    public void setLineasFragment(LineasFragment lineasFragment) {
        this.lineasFragment = lineasFragment;
    }

    void mensaje(String msj) {
        Toast toast = Toast.makeText(context, msj, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tx_linea;
        TextView tx_ramal;
        ImageButton btImageButton;

        ViewHolder(View itemView) {
            super(itemView);
            tx_linea = itemView.findViewById(R.id.tx_linea_parada_not);
            tx_ramal = itemView.findViewById(R.id.tx_ramal_parada_not);
            btImageButton = itemView.findViewById(R.id.bt_parada_not_edit);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                //Yes button clicked
                                ParadaAlarma paradaAlarma = mData.get(getAdapterPosition());
                                DatabaseAlarms.getInstance(context).deleteParadaAlarma(paradaAlarma);
                                NotificacionFragment.notifyDataSetChange(context);
                                notifyDataSetChanged();
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("¿Desea eliminar esta parada?").setPositiveButton("Si", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    return false;
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final ParadaAlarma paradaAlarma = mData.get(getAdapterPosition());

            tabViewPager.setCurrentItem(MainActivity.TAB_MAPA);
            lineasFragment.checkRamal(paradaAlarma.getIdRamal(), paradaAlarma.getId_parada());
            mapFragment.camare(paradaAlarma.getPunto());
        }
    }
}

