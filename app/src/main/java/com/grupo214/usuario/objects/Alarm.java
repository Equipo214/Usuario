package com.grupo214.usuario.objects;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseBooleanArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class Alarm {
    public static final int MON = 1;
    public static final int TUES = 2;
    public static final int WED = 3;
    public static final int THURS = 4;
    public static final int FRI = 5;
    public static final int SAT = 6;
    public static final int SUN = 7;
    private final long id;
    private long time;
    private String label;
    private SparseBooleanArray allDays;
    private boolean isEnabled;
    private ArrayList<ParadaAlarma> paradaAlarmas;

    public Alarm(long id, long time, String label, SparseBooleanArray allDays, boolean isEnabled) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.allDays = allDays;
        this.isEnabled = isEnabled;
        this.paradaAlarmas = new ArrayList<>();
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

    public long getId() {
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

    public void addParada(ParadaAlarma paradaAlarma) {
        this.paradaAlarmas.add(paradaAlarma);
    }

    public ParadaAlarma getParada(int i) {
        return paradaAlarmas.get(i);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MON, TUES, WED, THURS, FRI, SAT, SUN})
    @interface Days {
    }
}
