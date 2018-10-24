package com.grupo214.usuario.objects;

import com.grupo214.usuario.Util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Linea {

    public static Comparator<Linea> COMPARATOR = new Comparator<Linea>() {
        @Override
        public int compare(Linea o1, Linea o2) {
            if (Util.isNumeric(o1.getLinea()) && Util.isNumeric(o2.getLinea()))
                return Integer.parseInt(o1.getLinea()) - Integer.parseInt(o2.getLinea());
            else
                return o1.getLinea().compareTo(o2.getLinea());
        }
    };
    private String idLinea;
    private String linea;
    private ArrayList<Ramal> ramales;

    public Linea(String idLinea, String linea, ArrayList<Ramal> ramales) {
        this.idLinea = idLinea;
        this.linea = linea;
        this.ramales = ramales;
    }

    public static Linea getByID(ArrayList<Linea> mLinea, String idLinea) {
        for (Linea l : mLinea) {
            if (l.getIdLinea().equals(idLinea))
                return l;
        }
        return null;
    }

    public String getIdLinea() {
        return idLinea;
    }

    public String getLinea() {
        return linea;
    }

    public ArrayList<Ramal> getRamales() {
        return ramales;
    }

    public List<String> getRamalesNombres() {
        ArrayList<String> ramalesNombres = new ArrayList<>();

        for (Ramal r : ramales)
            ramalesNombres.add(r.getDescripcion());

        return ramalesNombres;
    }

    @Override
    public String toString() {
        StringBuilder ramales_string = new StringBuilder("[ ");

        for (Ramal r : ramales)
            ramales_string.append(r.toString()).append(" , ");

        ramales_string.append(" ]");

        return "idLinea: " + idLinea + " linea: " + linea + ramales_string;
    }


}
