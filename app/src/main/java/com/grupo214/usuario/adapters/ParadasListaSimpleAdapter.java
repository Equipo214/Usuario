package com.grupo214.usuario.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo214.usuario.R;
import com.grupo214.usuario.activities.MainActivity;
import com.grupo214.usuario.fragment.MapFragment;
import com.grupo214.usuario.objects.ParadaAlarma;

import java.util.ArrayList;
import java.util.List;


public class ParadasListaSimpleAdapter extends RecyclerView.Adapter<ParadasListaSimpleAdapter.ViewHolder> {

    private List<ParadaAlarma> mData;
    private LayoutInflater mInflater;
    private MapFragment mapFragment;
    private ViewPager tabViewPager;
    private Context context;

    public void setTabViewPager(ViewPager tabViewPager) {
        this.tabViewPager = tabViewPager;
    }

    // data is passed into the constructor
    ParadasListaSimpleAdapter(Context context, ArrayList<ParadaAlarma> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.parada_not_simple, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ParadaAlarma paradaAlarma = mData.get(position);
        holder.tx_linea.setText(paradaAlarma.getLinea());
        holder.btImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Entro",Toast.LENGTH_LONG).show();
                tabViewPager.setCurrentItem(MainActivity.TAB_MAPA);
                mapFragment.camare(paradaAlarma.getPunto());
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


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tx_linea;
        ImageButton btImageButton;

        ViewHolder(View itemView) {
            super(itemView);
            tx_linea = itemView.findViewById(R.id.tx_not_label);
            btImageButton = itemView.findViewById(R.id.bt_not_parada);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }
}

