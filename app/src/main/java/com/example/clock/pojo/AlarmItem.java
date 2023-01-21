package com.example.clock.pojo;

import android.app.PendingIntent;

import java.util.Calendar;
import java.util.Set;

public class AlarmItem {
    private Calendar time;
    private boolean isOn = true;
    private PendingIntent pendingIntent;

    public AlarmItem(Calendar time, boolean isOn, PendingIntent pendingIntent) {
        this.time = time;
        this.isOn = isOn;
        this.pendingIntent = pendingIntent;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }
}
