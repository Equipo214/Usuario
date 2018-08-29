package com.grupo214.usuario.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.grupo214.usuario.R;
import com.grupo214.usuario.activities.MainActivity;
import com.grupo214.usuario.adapters.LineasAdapter;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Clase gestiona la pestaña con el listado de listas para seleccionar.
 *
 * @author Daniel Boullon
 */
public class LineasFragment extends Fragment {

    Dialog startMenuDialog;
    private ExpandableListView expandableListView;
    private ArrayList<Linea> mLineas;
    private HashMap<String,Ramal> ramales_seleccionados;
    private LineasAdapter adapter;
    private Button bt_dondeEstaMiBondi;
    private SmartTabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lineas, container, false);

        // con este tema personalizado evitamos los bordes por defecto
        startMenuDialog = new Dialog(getContext(), R.style.Theme_Dialog_Translucent);
        //deshabilitamos el título por defecto
        startMenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //obligamos al usuario a pulsar los botones para cerrarlo
        startMenuDialog.setCancelable(false);
        //establecemos el contenido de nuestro dialog
        startMenuDialog.setContentView(R.layout.start_menu_route);

        ((Button) startMenuDialog.findViewById(R.id.bt_ubicacion)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startMenuDialog.dismiss();
                Toast.makeText(getContext(), "Ubicacion", Toast.LENGTH_SHORT).show();


            }
        });

        ((Button) startMenuDialog.findViewById(R.id.bt_loc_map)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startMenuDialog.dismiss();
                Toast.makeText(getContext(), "Selecionar", Toast.LENGTH_SHORT).show();

            }
        });

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
                tabLayout.getTabAt(MainActivity.TAB_MAPA).setSelected(true);
                //
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
                if(r.isCheck()){
                    r.getDibujo().show();
                    ramales_seleccionados.put(r.getIdLinea(),r);
                }
                else{
                    r.getDibujo().hide();
                    ramales_seleccionados.remove(r.getIdLinea());
                }

                checkBox.setChecked(r.isCheck());

                return false;
            }
        });

        return rootView;
    }

    private void mensaje(String msj) {
        Snackbar.make(getActivity().findViewById(android.R.id.content)
                , msj, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    public void setLineas(ArrayList<Linea> mLineas, HashMap<String,Ramal> lineas_seleccionadas) {
        this.mLineas = mLineas;
        this.ramales_seleccionados = lineas_seleccionadas;

    }

    public void setTabLayout(SmartTabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }
}
