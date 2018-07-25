package com.grupo214.usuario.objetos;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.grupo214.usuario.Util.Util;

import java.util.ArrayList;
import java.util.List;

public class Linea {
    private int id;
    private String linea;
    private String ramal;
    private boolean check;

    private Recorrido recorrido; //esta no
    private PolylineOptions polylineOptions; // no deberia existir
    private Polyline polyline; //esta si
    private List<Marker> paradas;


    private int color;
    private int i;
    private int j;
    private int sentido_i;
    private int sentido_j;

    public Linea(int id, String linea, String ramal, Recorrido recorrido, int color) {
        super();
        this.paradas = new ArrayList<>();
        this.i = 0;
        this.id = id;
        this.linea = linea;
        this.ramal = ramal;
        this.check = false;
        this.recorrido = recorrido;
        this.color = color;

    }

    public static ArrayList<Linea> listHardCodeTest() {
        ArrayList<Linea> mLineas = new ArrayList<>();
        Recorrido r = new Recorrido();
        ColorRuta cr = new ColorRuta();

        r.add(new Punto(true, new LatLng(-34.613346, -58.367064)));
        r.add(new Punto(false, new LatLng(-34.613565, -58.369925)));
        r.add(new Punto(true, new LatLng(-34.626190, -58.422135)));
        r.add(new Punto(false, new LatLng(-34.630605, -58.451507)));
        r.add(new Punto(true, new LatLng(-34.628788, -58.454536)));
        r.add(new Punto(true, new LatLng(-34.638853, -58.482724)));
        r.add(new Punto(true, new LatLng(-34.640636, -58.564095)));
        mLineas.add(new Linea(1, "4", "ramal 1", r, cr.nextColor()));
        Recorrido r1 = new Recorrido();
        r1.add(new Punto(true, new LatLng(-34.607313, -58.363046)));
        r1.add(new Punto(false, new LatLng(-34.611779, -58.362380)));
        r1.add(new Punto(true, new LatLng(-34.621185, -58.361426)));
        r1.add(new Punto(true, new LatLng(-34.698222, -58.391971)));
        r1.add(new Punto(true, new LatLng(-34.758188, -58.409164)));
        mLineas.add(new Linea(2, "4", "ramal 2", r1, cr.nextColor()));

        Recorrido r2 = new Recorrido();
        r2.add(new Punto(true, new LatLng(-34.603435, -58.370056)));
        r2.add(new Punto(false, new LatLng(-34.610275, -58.369286)));
        r2.add(new Punto(true, new LatLng(-34.617771, -58.368680)));
        r2.add(new Punto(false, new LatLng(-34.620578, -58.368439)));
        r2.add(new Punto(true, new LatLng(-34.687849, -58.475321)));
        mLineas.add(new Linea(2, "4", "ramal 3", r2, cr.nextColor()));

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

        //        r3.add(new Punto(true, new LatLng(-34.680438, -58.551687)));
        //        r3.add(new Punto(true, new LatLng(-34.686466, -58.558697)));
        //        r3.add(new Punto(true, new LatLng(-34.682602, -58.554564)));
        mLineas.add(new Linea(3, "406", "Ramos Mejia", r3, cr.nextColor()));

/*
        mLineas.add(new Linea(3,"406", "Ramos Mejia",false));
        mLineas.add(new Linea(4,"406", "San Justo",false));
        mLineas.add(new Linea(5,"338", "que se yo",false));
        mLineas.add(new Linea(6,"338", "ramal",false));
        mLineas.add(new Linea(7,"338", "asdqwe",false));
        mLineas.add(new Linea(8,"96", "San Justo",false));
        mLineas.add(new Linea(9,"66", "Centro",false));
        mLineas.add(new Linea(10,"46", "Constitucion",false));
        mLineas.add(new Linea(11,"113", "asdsaq",false));
        mLineas.add(new Linea(12,"51", "que se yo",false));
        mLineas.add(new Linea(13,"16", "Lomas de Zamora",false));
        mLineas.add(new Linea(14,"14", "Burzaco",false));
        mLineas.add(new Linea(15,"4", "Ramos Mejia",false));
        mLineas.add(new Linea(16,"7", "San Justo",false));
        mLineas.add(new Linea(17,"143", "que se yo",false));
        mLineas.add(new Linea(18,"123", "ramal",false));
        mLineas.add(new Linea(19,"132", "asdqwe",false));
        mLineas.add(new Linea(20,"52", "San Justo",false));
        mLineas.add(new Linea(21,"78", "Centro",false));
        mLineas.add(new Linea(22,"87", "Constitucion",false));
        mLineas.add(new Linea(23,"83", "asdsaq",false));
        mLineas.add(new Linea(24,"91", "que se yo",false));
        */
        return mLineas;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
        this.j = this.polyline.getPoints().size() - 1;
    }

    public void agregarParada(Marker mk) {
        paradas.add(mk);
    }

    public int getId() {
        return id;
    }

    public String getLinea() {
        return linea;
    }

    public String getRamal() {
        return ramal;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getColor() {
        return color;
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public void setPolylineOptions(PolylineOptions polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    public int getColorRuta() {
        return color;
    }

    public LatLng getNextPointDemo() {
        i += sentido_i;

        if (i == this.polyline.getPoints().size() - 1)
            sentido_i = -1;
        if (i == 0)
            sentido_i = 1;

        return polyline.getPoints().get(i);
    }

    public LatLng getPreviousPointDemo() {
        j += sentido_j;

        if (j == this.polyline.getPoints().size() - 1)
            sentido_j = -1;
        if (j == 0)
            sentido_j = 1;

        return polyline.getPoints().get(j);
    }

    public void hide() {
        polyline.setVisible(false);
        for (Marker mk : paradas) {
            mk.setVisible(false);
        }
    }

    public void show() {
        polyline.setVisible(true);
        for (Marker mk : paradas) {
            mk.setVisible(true);
        }
    }

    public LatLng paraMasCercana(LatLng userStart) {
        LatLng parada = recorrido.get(0).getLatLng(); // guardo el primero como minimo
        double distancia = Util.calculateDistance(userStart, parada);

        for (Punto punto : recorrido) {
            if (Util.calculateDistance(userStart, punto.getLatLng()) < distancia) { // si no hay otro minimo es este
                parada = punto.getLatLng();
            }
        }
        return parada;
    }
}