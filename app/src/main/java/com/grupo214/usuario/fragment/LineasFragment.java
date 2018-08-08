package com.grupo214.usuario.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.grupo214.usuario.R;
import com.grupo214.usuario.adapters.LineasAdapter;
import com.grupo214.usuario.objects.Linea;

import java.util.ArrayList;


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


    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lineas, container, false);

        // Obtener el ExpandableListView y setearle el adaptador
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

        adapter = new LineasAdapter(getContext(), mLineas);
        expandableListView.setAdapter(adapter);

        // ¿ DONDE ESTA MI BONDI ?
        bt_dondeEstaMiBondi = (Button) rootView.findViewById(R.id.bt_dondeEstaMiBondi);
        // EditText busqueda = rootView.findViewById(R.id.busqueda);


        /* Buscar google polylineOptions.
         if (GoogleMapsDirectionsAPI.checkNull(mLineas)) {
            GoogleMapsDirectionsAPI.loadPolylineOptions(mLineas);
        }*/

        final Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t) {
                expandableListView.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (expandableListView.getHeight() * interpolatedTime);
                expandableListView.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };


        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {


            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < mLineas.size(); i++)
                    if (i != groupPosition)
                        expandableListView.collapseGroup(i);

            }
        });


        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {


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

    public void setmLineas(ArrayList<Linea> mLineas) {
        this.mLineas = mLineas;
    }

}
