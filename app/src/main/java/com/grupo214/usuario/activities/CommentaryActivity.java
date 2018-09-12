package com.grupo214.usuario.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;

public class CommentaryActivity extends AppCompatActivity {

    private static final int CANT_CARACTERES = 10;
    private RequestQueue requestQueue_enviarComentario;
    private ArrayList<String> lineas_nombres; // lineas_nombres;
    private ArrayList<String> lineas_id_nombres; // lineas id
    private ArrayList<String> ramal_nombres;
    private ArrayList<String> ramal_id_nombres;
    private ArrayAdapter<String> myAdapterLinea;
    private ArrayAdapter<String> myAdapterRamal;

    private Spinner spinner_linea;
    private Spinner spinner_ramal;
    private EditText tx_comentario;
    private ArrayList<Linea> mLinea;
    private String idLinea;
    private String idRamal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentary);

        ImageButton bt_enviar = (ImageButton) findViewById(R.id.bt_enviar);
        ImageButton bt_backComm = (ImageButton) findViewById(R.id.bt_backComm);
        tx_comentario = findViewById(R.id.tx_comentario);

        spinner_linea = findViewById(R.id.spinner_linea);
        spinner_ramal = findViewById(R.id.spinner_ramal);

        cargarDatos();

        bt_backComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentaryActivity.super.onBackPressed();
            }
        });
        bt_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comentario = tx_comentario.getText().toString();
                if (idLinea != null && idRamal != null)
                    if (comentario.length() < CANT_CARACTERES) {
                        Toast.makeText(getApplicationContext(), "Ingresar  un minimo de " + CANT_CARACTERES + " caracteres.", Toast.LENGTH_LONG).show();
                    } else {

                        if (requestQueue_enviarComentario != null) {
                            requestQueue_enviarComentario.stop();
                            requestQueue_enviarComentario = null;
                        }
                        String url = "https://virginal-way.000webhostapp.com/appPasajero/addComentario.php?idLinea=" + idLinea + "&idRamal=" + idRamal + "&comentario=" + comentario;

                        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, null, null);
                        requestQueue_enviarComentario = Volley.newRequestQueue(getApplicationContext());
                        requestQueue_enviarComentario.add(jsonRequest);
                        Toast.makeText(getApplicationContext(), "Enviado", Toast.LENGTH_LONG).show();
                        CommentaryActivity.super.onBackPressed();
                }
            }
        });


        spinner_linea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int position, long lng) {
                String item = adapter.getItemAtPosition(position).toString();
                idLinea = lineas_id_nombres.get(lineas_nombres.indexOf(item));
                traerRamal(idLinea);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spinner_ramal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                idRamal = ramal_id_nombres.get(ramal_nombres.indexOf(item));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cargarDatos() {
        mLinea = SplashScreen.mLineas; // <-- esta negrada si se puede ver.

        lineas_nombres = new ArrayList<>();
        lineas_id_nombres = new ArrayList<>();
        for (Linea l : mLinea) {
            lineas_nombres.add(l.getLinea());
            lineas_id_nombres.add(l.getIdLinea());
        }
        myAdapterLinea = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lineas_nombres);
        myAdapterLinea.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_linea.setAdapter(myAdapterLinea);
    }


    private void traerRamal(String idLinea) {
        Linea linea = Linea.getByID(mLinea, idLinea);
        ramal_nombres = new ArrayList<>();
        ramal_id_nombres = new ArrayList<>();

        for (Ramal r : linea.getRamales()) {
            ramal_nombres.add(r.getDescripcion());
            ramal_id_nombres.add(r.getIdRamal());
        }

        myAdapterRamal = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, ramal_nombres);
        myAdapterRamal.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_ramal.setAdapter(myAdapterRamal);
    }
}
