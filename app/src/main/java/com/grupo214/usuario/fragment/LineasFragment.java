package com.grupo214.usuario.fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.DatabaseAlarms;
import com.grupo214.usuario.activities.MainActivity;
import com.grupo214.usuario.adapters.LineasAdapter;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.ParadaAlarma;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Clase gestiona la pestaña con el listado de listas para seleccionar.
 *
 * @author Daniel Boullon
 */
public class LineasFragment extends Fragment {

    private static String TAG = "LineasFragment";
    private ExpandableListView expandableListView;
    private ArrayList<Linea> mLineas;
    private HashMap<String, Ramal> ramales_seleccionados;
    private LineasAdapter adapter;
    private Button bt_dondeEstaMiBondi;
    private ViewPager tabViewPager;
    private Dialog startMenuDialog;
    private MapFragment mapFragment;

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lineas, container, false);

        // Obtener el ExpandableListView y setearle el notificacionesAdapter
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

        adapter = new LineasAdapter(getContext(), mLineas);
        expandableListView.setAdapter(adapter);

        // ¿ DONDE ESTA MI BONDI ?
        bt_dondeEstaMiBondi = (Button) rootView.findViewById(R.id.bt_dondeEstaMiBondi);

        /*
        Button testNot = (Button) rootView.findViewById(R.id.bt_testNOt);
        testNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), NotificationBus.class);
                i.putExtra("tiempo","5");
                i.putExtra("ramal","Lomas de Zamora");
                i.putExtra("linea","406");
                getContext().startService(i);
            }
        });*/

        final Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom);

        bt_dondeEstaMiBondi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setTab Change Tab algo con tab

                if (ramales_seleccionados.size() == 0) {
                    Toast.makeText(getContext(), "Seleciona por lo menos un ramal.", Toast.LENGTH_SHORT).show();
                    return;
                }
                bt_dondeEstaMiBondi.startAnimation(shake);

                if (MainActivity.puntoPartida == null) {
                    startMenuDialog.show();
                } else{
                    mapFragment.dondeEstaMiBondi(MainActivity.puntoPartida);
                }
                tabViewPager.setCurrentItem(MainActivity.TAB_MAPA);

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
                //getContext().startService(new Intent(getContext(),NotificationBus.class));

                Ramal r = mLineas.get(groupPosition).getRamales().get(childPosition);
                CheckBox checkBox = v.findViewById(R.id.list_checkBox);

                Log.d(TAG, ramales_seleccionados.toString());
                r.setChecked(!r.isCheck());
                if (r.isCheck()) {

                    if( mapFragment.cargarRamal(r) ){
                        Toast.makeText(getContext(),"Hay un recorrido alternativo para el ramal "+ r.getDescripcion(),Toast.LENGTH_LONG).show();
                    }
                    ramales_seleccionados.put(r.getIdRamal(), r);
                } else {
                    r.getDibujo().remove();
                    ramales_seleccionados.remove(r.getIdRamal());
                }

                Log.d(TAG, r.toString());
                checkBox.setChecked(r.isCheck());
                DatabaseAlarms.getInstance(getContext()).updateRamal(r.getIdRamal(), r.isCheck());

                return false;
            }
        });
        return rootView;
    }

    public void setLineas(ArrayList<Linea> mLineas, HashMap<String, Ramal> ramales_seleccionados) {
        this.mLineas = mLineas;
        this.ramales_seleccionados = ramales_seleccionados;
    }

    public void setParams(ViewPager tabViewPager,MapFragment mapFragment) {
        this.tabViewPager = tabViewPager;
        this.mapFragment = mapFragment;
    }

    public void setStartMenuDialog(Dialog startMenuDialog) {
        this.startMenuDialog = startMenuDialog;
    }

    public void checkRamal(String idRamal, String idParada) {
        for (Linea l : mLineas)
            for (Ramal r : l.getRamales())
                if (r.getIdRamal().equals(idRamal)) {
                    r.setChecked(true);
                    mapFragment.cargarRamal(r);
                    ramales_seleccionados.put(r.getIdRamal(), r);
                    DatabaseAlarms.getInstance(getContext()).updateRamal(r.getIdRamal(), r.isCheck());
                    for (Marker mk : r.getDibujo().getParadas())
                        if (((ParadaAlarma) (mk.getTag())).getId_parada().equals(idParada)) {
                            mk.showInfoWindow();
                        }
                    return;
                }
    }
}