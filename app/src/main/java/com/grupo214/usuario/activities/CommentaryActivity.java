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

import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;

public class CommentaryActivity extends AppCompatActivity {

    private static final int CANT_CARACTERES = 10;
    ArrayList<String> lineas_nombres; // lineas_nombres;
    ArrayList<String> lineas_id_nombres; // lineas id
    ArrayList<String> ramal_nombres;
    ArrayList<String> ramal_id_nombres;
    ArrayAdapter<String> myAdapterLinea;
    ArrayAdapter<String> myAdapterRamal;
    private Spinner spinner_linea;
    private Spinner spinner_ramal;
    private EditText tx_comentario;
    private ArrayList<Linea> mLinea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentary);

        ImageButton bt_enviar = (ImageButton) findViewById(R.id.bt_enviar);
        tx_comentario = findViewById(R.id.tx_comentario);

        spinner_linea = findViewById(R.id.spinner_linea);
        spinner_ramal = findViewById(R.id.spinner_ramal);

        cargarDatos();


        bt_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tx_comentario.getText().toString().length() < CANT_CARACTERES){
                    Toast.makeText(getApplicationContext(), "Ingresar  un minimo de " + CANT_CARACTERES + " caracteres.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Enviado", Toast.LENGTH_LONG).show();
                }
            }
        });


        spinner_linea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int position, long lng) {
                String item = adapter.getItemAtPosition(position).toString();
                String idLinea = lineas_id_nombres.get(lineas_nombres.indexOf(item));
                traerRamal(idLinea);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void cargarDatos() {
        mLinea = new ArrayList<Linea>();
        /* ALTO HARCODEO FAST NO CRASH NEVER PONY **/
        ArrayList<Ramal> r = new ArrayList<Ramal>();
        r.add(new Ramal("2","Lomas de Zamora","",null));
        mLinea.add(new Linea("1","405",r));


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
