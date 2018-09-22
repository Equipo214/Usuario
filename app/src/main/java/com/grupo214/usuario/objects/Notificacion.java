package com.grupo214.usuario.objects;

import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;

public class Notificacion {
    private final long id;
    private long time;
    private String label;
    private SparseBooleanArray allDays;
    private boolean isEnabled;

    public Notificacion(long id, long time, String label, SparseBooleanArray allDays, boolean isEnabled) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.allDays = allDays;
        this.isEnabled = isEnabled;
    }

    public long getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public @NonNull String getLabel() {
        return label;
    }

    public SparseBooleanArray getAllDays() {
        return allDays;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
