package com.grupo214.usuario.objects;

public class Ramal {
    private int idRamal;
    private String ramal;
    private Boolean check;
    private Recorrido recorrido;
    private Recorrido recorridoAux;

    public Ramal(int idRamal, String ramal,Recorrido recorrido) {
        this.idRamal = idRamal;
        this.ramal = ramal;
        this.recorrido = recorrido;
    }

    public String getRamal() {
        return ramal;
    }
}
