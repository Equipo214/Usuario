package com.grupo214.usuario.objects;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.HashMap;

public class Alarm {
    public static final int MON     =   Calendar.MONDAY;
    public static final int TUES    =   Calendar.TUESDAY;
    public static final int WED     =   Calendar.WEDNESDAY;
    public static final int THURS   =   Calendar.THURSDAY;
    public static final int FRI     =   Calendar.FRIDAY;
    public static final int SAT     =   Calendar.SATURDAY;
    public static final int SUN     =   Calendar.SUNDAY;
    private final int id;
    private long time;
    private String label;
    private SparseBooleanArray allDays;
    private boolean isEnabled;
    private HashMap<String,ParadaAlarma> paradaAlarmas;

    public Alarm(int id, long time, String label, SparseBooleanArray allDays, boolean isEnabled) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.allDays = allDays;
        this.isEnabled = isEnabled;
        this.paradaAlarmas = new HashMap<>();
    }


    public boolean getDay(@Days int day) {
        return allDays.get(day);
    }

    public SparseBooleanArray getAllDays() {
        return allDays;
    }

    public void setAllDays(SparseBooleanArray allDays) {
        this.allDays = allDays;
    }

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public @NonNull
    String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void putParadaAlarma(String idRamal, ParadaAlarma paradaAlarma) {
        this.paradaAlarmas.put(idRamal,paradaAlarma);
    }

    public ParadaAlarma getParadaAlarma(String idParada) {
        return paradaAlarmas.get(idParada);
    }

    public HashMap<String,ParadaAlarma> getParadaAlarmas() {
        return paradaAlarmas;
    }

    public void setParadaAlarmas(HashMap<String,ParadaAlarma> paradaAlarmas) {
        this.paradaAlarmas = paradaAlarmas;
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MON, TUES, WED, THURS, FRI, SAT, SUN})
    @interface Days {
    }
}
