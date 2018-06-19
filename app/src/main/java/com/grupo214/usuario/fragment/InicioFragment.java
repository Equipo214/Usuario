package com.grupo214.usuario.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.grupo214.usuario.R;

/**
 * Created by User on 2/28/2017.
 */

public class InicioFragment extends Fragment {
    private static final String TAG = "InicioFragment";

    private Button btnTEST;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);

        return view;
    }
}