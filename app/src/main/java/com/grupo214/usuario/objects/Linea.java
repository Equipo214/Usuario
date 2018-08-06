package com.grupo214.usuario.objects;

import java.util.ArrayList;
import java.util.List;

public class Linea {

    private int idLinea;
    private String linea;
    private ArrayList<Ramal> ramales;


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
}
