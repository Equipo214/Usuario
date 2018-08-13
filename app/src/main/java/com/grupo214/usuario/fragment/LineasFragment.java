package com.grupo214.usuario.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;

import com.grupo214.usuario.R;
import com.grupo214.usuario.activities.MainActivity;
import com.grupo214.usuario.adapters.LineasAdapter;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;

import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Clase gestiona la pestaña con el listado de listas para seleccionar.
 *
 * @author Daniel Boullon
 */
public class LineasFragment extends Fragment {

    private ExpandableListView expandableListView;
    private ArrayList<Linea> mLineas;
    private LineasAdapter adapter;
    private Button bt_dondeEstaMiBondi;
    private TabLayout tabLayout;


    @Nullable
    void setAdapter(ArrayList<Linea> mLineas){
        this.adapter.setMlineas(mLineas);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lineas, container, false);

        // Obtener el ExpandableListView y setearle el adaptador
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

        adapter = new LineasAdapter(getContext(), mLineas);
        expandableListView.setAdapter(adapter);

        // ¿ DONDE ESTA MI BONDI ?
        bt_dondeEstaMiBondi = (Button) rootView.findViewById(R.id.bt_dondeEstaMiBondi);
        bt_dondeEstaMiBondi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabLayout.getTabAt(MainActivity.TAB_MAPA).select();

            }
        });
        // EditText busqueda = rootView.findViewById(R.id.busqueda);


        /* Buscar google polylineOptions.
         if (GoogleMapsDirectionsAPI.checkNull(mLineas)) {
            GoogleMapsDirectionsAPI.loadPolylineOptions(mLineas);
        }*/



        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {


            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < mLineas.size(); i++)
                    if (i != groupPosition)
                        expandableListView.collapseGroup(i);
            }
        });


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Linea l = mLineas.get(expandableListView.getFlatListPosition(id));

                Ramal r = mLineas.get(groupPosition).getRamales().get(childPosition);
                CheckBox checkBox = v.findViewById(R.id.list_checkBox);
                r.setChecked(!r.isCheck());
                checkBox.setChecked(!r.isCheck());
                return false;
            }
        });




        /*  Esto es viejo pero fijate si hay una logica estrambolica aqui: fkdsn a
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineaDemo l = mLineas.get(expandableListView.getChildAdapterPosition(v));
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.list_checkBox);
                checkBox.setChecked(!checkBox.isChecked());
                l.setCheck(!l.isCheck());

                //por si las moscas que no tire null :v
                if (l.isCheck()) {
                    if (l.getPolylineOptions() == null) {
                        GoogleMapsDirectionsAPI.loadPolylineOptions(l);
                    }
                }

            }
        });*/


        return rootView;
    }

    private void mensaje(String msj) {
        Snackbar.make( getActivity().findViewById(android.R.id.content)
                , msj , Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    public void setmLineas(ArrayList<Linea> mLineas) {
        this.mLineas = mLineas;
    }

    public void setTabLayout(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }
}
