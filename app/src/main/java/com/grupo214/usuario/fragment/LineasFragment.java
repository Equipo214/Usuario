package com.grupo214.usuario.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.Toast;


import com.grupo214.usuario.R;
import com.grupo214.usuario.activities.MainActivity;
import com.grupo214.usuario.adapters.LineasAdapter;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Clase gestiona la pestaña con el listado de listas para seleccionar.
 *
 * @author Daniel Boullon
 */
public class LineasFragment extends Fragment {


    private ExpandableListView expandableListView;
    private ArrayList<Linea> mLineas;
    private HashMap<String, Ramal> ramales_seleccionados;
    private LineasAdapter adapter;
    private Button bt_dondeEstaMiBondi;
    private ViewPager tabViewPager;
    private Dialog startMenuDialog;


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
                // setTab Change Tab algo con tab
                if (ramales_seleccionados.size() == 0) {
                    Toast.makeText(getContext(), "Seleciona por lo menos un ramal. PUTO", Toast.LENGTH_SHORT).show();
                    return;
                }
                tabViewPager.setCurrentItem(MainActivity.TAB_MAPA);
                startMenuDialog.show();

            }
        });


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

                Ramal r = mLineas.get(groupPosition).getRamales().get(childPosition);
                CheckBox checkBox = v.findViewById(R.id.list_checkBox);


                r.setChecked(!r.isCheck());
                if (r.isCheck()) {
                    r.getDibujo().show();
                    ramales_seleccionados.put(r.getIdLinea(), r);
                } else {
                    r.getDibujo().hide();
                    ramales_seleccionados.remove(r.getIdLinea());
                }

                checkBox.setChecked(r.isCheck());

                return false;
            }
        });

        return rootView;
    }

    public void setLineas(ArrayList<Linea> mLineas, HashMap<String, Ramal> lineas_seleccionadas) {
        this.mLineas = mLineas;
        this.ramales_seleccionados = lineas_seleccionadas;
    }

    public void setTabViewPager(ViewPager tabViewPager) {
        this.tabViewPager = tabViewPager;
    }

    public void setStartMenuDialog(Dialog startMenuDialog) {
        this.startMenuDialog = startMenuDialog;
    }
}
