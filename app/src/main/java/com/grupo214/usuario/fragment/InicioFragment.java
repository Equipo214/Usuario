package com.grupo214.usuario.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.grupo214.usuario.R;

/**
 * Clase gestiona la pestaña "inicio" quizas esto muera como los sentimientos de ella hacia mi.
 * @author  Daniel Boullon
 */
public class InicioFragment extends Fragment {
    private static final String TAG = "InicioFragment";



    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab1_fragment,container,false);
        FloatingActionButton bt_float = view.findViewById(R.id.floatingActionButton);
         bt_float.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Toast.makeText(getContext(),"NO LO HICE TODAVIA PERA UN RATO LOCO PERA TODO YO TENGO QUE HACER",Toast.LENGTH_LONG).show();
             }
         });
        return view;
    }
}