package com.grupo214.usuario.fragment;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.grupo214.usuario.R;
import com.grupo214.usuario.adapters.LineasAdapter;
import com.grupo214.usuario.apiGoogleDirection.GoogleMapsDirectionsAPI;
import com.grupo214.usuario.objetos.Linea;
import com.grupo214.usuario.objetos.Punto;

import java.util.ArrayList;

public class LineasFragment extends Fragment {

    private RecyclerView recyclerLineas;
    private ArrayList<Linea> mLineas;
    private LineasAdapter adapter;

    public void setmLineas(ArrayList<Linea> mLineas) {
        this.mLineas = mLineas;
    }


    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_lineas, container, false);
        adapter = new LineasAdapter(mLineas);
        recyclerLineas = (RecyclerView) rootView.findViewById(R.id.recyclerViewLineas);
        recyclerLineas.setLayoutManager(new LinearLayoutManager(getContext()));
       // EditText busqueda = rootView.findViewById(R.id.busqueda);

        if (GoogleMapsDirectionsAPI.checkNull(mLineas)) {
            GoogleMapsDirectionsAPI.cargarPolylineOptions(mLineas);
        }


        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Linea l = mLineas.get(recyclerLineas.getChildAdapterPosition(v));
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.list_checkBox);
                checkBox.setChecked(!checkBox.isChecked());
                l.setCheck(!l.isCheck());
                //por si las moscas que no tire null :v
                if (l.isCheck()) {
                    if (l.getPolylineOptions() == null) {
                        GoogleMapsDirectionsAPI.cargarPolylineOptions(l);
                    }
                }
            }
        });

        recyclerLineas.setAdapter(adapter);

        return rootView;
    }
}
