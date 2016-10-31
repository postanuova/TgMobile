package org.teenguard.child.dbdatatype;

import android.location.Location;

/**
 * Created by chris on 30/10/16.
 */

public class DbLocation {
    private long id;
    private long date;
    private double latitude;
    private double longitude;
    private double accuracy;
    private int trigger;


    public void dump() {
        System.out.println("-------------DB LOCATION DUMP-------------");
        System.out.println("id = " + id);
        System.out.println("date = " + date);
        System.out.println("latitude = " + latitude);
        System.out.println("longitude = " + longitude);
        System.out.println("accuracy = " + accuracy);
        System.out.println("trigger = " + trigger);
    }

    public long getId() {
        return id;
    }

    public DbLocation(Location location) {
        this.id = 0;
        this.date = location.getTime();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.accuracy = location.getAccuracy();
        this.trigger = -1;
    }

    public void setId(long Id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public  double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public int getTrigger() {
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }
}
