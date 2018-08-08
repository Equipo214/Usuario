package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Linea {

    private int idLinea;
    private String linea;
    private ArrayList<Ramal> ramales;

    public Linea(int idLinea, String linea, ArrayList<Ramal> ramales) {
        this.idLinea = idLinea;
        this.linea = linea;
        this.ramales = ramales;
    }

    public String getLinea() {
        return linea;
    }

    public ArrayList<Ramal> getRamales() {
        return ramales;
    }

    public List<String> getRamalesNombres() {
        ArrayList<String> ramalesNombres = new ArrayList<>();

        for ( Ramal r: ramales)
            ramalesNombres.add(r.getRamal());

        return ramalesNombres;
    }

    public static ArrayList<Linea> listHardCodeTest() {
        ArrayList<Linea> mLinea = new ArrayList<>();
        ColorRuta cr = new ColorRuta();
        ArrayList<Ramal> ra1 = new ArrayList<>();
        Recorrido r0 = new Recorrido();
        r0.add(new Punto(true, new LatLng(-34.613346, -58.367064)));
        r0.add(new Punto(false, new LatLng(-34.613565, -58.369925)));
        r0.add(new Punto(true, new LatLng(-34.626190, -58.422135)));
        r0.add(new Punto(false, new LatLng(-34.630605, -58.451507)));
        r0.add(new Punto(true, new LatLng(-34.628788, -58.454536)));
        r0.add(new Punto(true, new LatLng(-34.638853, -58.482724)));
        r0.add(new Punto(true, new LatLng(-34.640636, -58.564095)));


        Recorrido r1 = new Recorrido();
        r1.add(new Punto(true, new LatLng(-34.607313, -58.363046)));
        r1.add(new Punto(false, new LatLng(-34.611779, -58.362380)));
        r1.add(new Punto(true, new LatLng(-34.621185, -58.361426)));
        r1.add(new Punto(true, new LatLng(-34.698222, -58.391971)));
        r1.add(new Punto(true, new LatLng(-34.758188, -58.409164)));


        ra1.add( new Ramal(1,"Lomas de Zamora",r0) );
        ra1.add( new Ramal( 2,"Burzaco",r1) );
        ra1.add( new Ramal( 3,"Ramos Mejia",r0) );

        mLinea.add(new Linea(1,"406",ra1));


        ArrayList<Ramal> ra2 = new ArrayList<>();
        Recorrido r2 = new Recorrido();
        r2.add(new Punto(true, new LatLng(-34.603435, -58.370056)));
        r2.add(new Punto(false, new LatLng(-34.610275, -58.369286)));
        r2.add(new Punto(true, new LatLng(-34.617771, -58.368680)));
        r2.add(new Punto(false, new LatLng(-34.620578, -58.368439)));
        r2.add(new Punto(true, new LatLng(-34.687849, -58.475321)));


        ra2.add(new Ramal(4,"Correo Central",r2));
        ra2.add(new Ramal(5,"Constitucion",r2));
        ra2.add(new Ramal(9,"Balnearo",r2));

        mLinea.add(new Linea(2,"4",ra2));

        Recorrido r3 = new Recorrido();
        r3.add(new Punto(true, new LatLng(-34.759334, -58.400633)));  //1
        r3.add(new Punto(true, new LatLng(-34.760338, -58.402877))); //2
        r3.add(new Punto(true, new LatLng(-34.759612, -58.408275)));  //3
        r3.add(new Punto(true, new LatLng(-34.767752, -58.410225))); //4
        r3.add(new Punto(true, new LatLng(-34.765143, -58.429020)));  //5
        r3.add(new Punto(true, new LatLng(-34.767960, -58.437517)));  //6
        r3.add(new Punto(true, new LatLng(-34.778570, -58.457853)));  //7
        r3.add(new Punto(true, new LatLng(-34.686466, -58.558697)));  //8
        r3.add(new Punto(true, new LatLng(-34.640636, -58.564095)));  //9

        ArrayList<Ramal> ra3 = new ArrayList<>();
        ra3.add(new Ramal(6,"5 - Lomas de Zamora",r3));
        ra3.add(new Ramal(7,"5 - Burzaco",r3));
        ra3.add(new Ramal(8,"4 - Temperley",r3));
        mLinea.add(new Linea(3, "266",ra3));


        return mLinea;
    }
}
