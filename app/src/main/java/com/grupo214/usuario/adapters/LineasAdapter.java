package com.grupo214.usuario.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.grupo214.usuario.R;
import com.grupo214.usuario.objetos.Linea;

import java.util.ArrayList;

public class LineasAdapter extends Adapter<LineasAdapter.ViewHolderLineas>
        implements View.OnClickListener {

    private ArrayList<Linea> listasLineas;
    private View.OnClickListener listener;


    public LineasAdapter(ArrayList<Linea> lineas) {
        this.listasLineas = lineas;
    }

    @NonNull
    @Override
    public ViewHolderLineas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_fila, null, false);

        view.setOnClickListener(this);

        return new ViewHolderLineas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderLineas holder, int position) {
        Linea l = listasLineas.get(position);
        holder.linea.setText(l.getLinea());
        holder.ramal.setText(l.getRamal());
        holder.checkBox.setChecked(l.isCheck());
        holder.ico.setColorFilter(l.getColor());
        //holder.icono.setImageResource(listasLineas.get(position).getIcono());

    }

    @Override
    public int getItemCount() {
        return listasLineas.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onClick(v);
    }

    public class ViewHolderLineas extends RecyclerView.ViewHolder {

        TextView linea;
        TextView ramal;
        ImageView ico;
        CheckBox checkBox;

        public ViewHolderLineas(View itemView) {
            super(itemView);
            linea = itemView.findViewById(R.id.list_text_linea);
            ramal = itemView.findViewById(R.id.list_text_ramal);
            ico = itemView.findViewById(R.id.icoColorColectivo);
            checkBox = itemView.findViewById(R.id.list_checkBox);
        }
    }


}