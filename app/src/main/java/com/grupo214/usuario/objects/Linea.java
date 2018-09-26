package com.grupo214.usuario.objects;

import java.util.ArrayList;
import java.util.List;

public class Linea {

    private String idLinea;
    private String linea;
    private ArrayList<Ramal> ramales;

    public String getIdLinea() {
        return idLinea;
    }

    public Linea(String idLinea, String linea, ArrayList<Ramal> ramales) {
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
            ramalesNombres.add(r.getDescripcion());

        return ramalesNombres;
    }

    @Override
    public String toString() {
        StringBuilder ramales_string = new StringBuilder("[ ");

        for (Ramal r : ramales)
            ramales_string.append(r.toString()).append(" , ");

        ramales_string.append(" ]");

        return "idLinea: " + idLinea +" linea: " + linea + ramales_string;
    }

    public static Linea getByID(ArrayList<Linea> mLinea, String idLinea) {
        for(Linea l : mLinea){
            if(l.getIdLinea().equals(idLinea))
                return l;
        }
        return null;
    }
}
