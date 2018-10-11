package com.grupo214.usuario.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.ParadaAlarma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParadasListaAdapter extends RecyclerView.Adapter<ParadasListaAdapter.ViewHolder> {

    private List<ParadaAlarma> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public ParadasListaAdapter(Context context, HashMap<String,ParadaAlarma> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = new ArrayList<>();
        this.mData.addAll(data.values());
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.parada_edit_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParadasListaAdapter.ViewHolder holder, int position) {
        ParadaAlarma paradaAlarma = mData.get(position);
        holder.tx_linea.setText(paradaAlarma.getId_linea());
        holder.tx_ramal.setText(paradaAlarma.getIdRamal());
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tx_linea;
        TextView tx_ramal;

        ViewHolder(View itemView) {
            super(itemView);
            tx_linea = itemView.findViewById(R.id.tx_linea_parada_not);
            tx_ramal = itemView.findViewById(R.id.tx_ramal_parada_not);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}

