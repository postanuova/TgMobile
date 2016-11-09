package org.teenguard.child.dbdatatype;

import android.location.Location;

import org.teenguard.child.dbdao.DbLocationEventDAO;

import static org.teenguard.child.utils.TypeConverter.doubleTrunkTwoDigit;

/**
 * Created by chris on 30/10/16.
 */

public class DbLocationEvent implements InterfaceDbDatatype {
    private long id;
    private long date;
    private double latitude;
    private double longitude;
    private double accuracy;
    private int trigger;

    public DbLocationEvent(long id, long date, double latitude, double longitude, double accuracy, int trigger) {
        this.id = id;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.trigger = trigger;
    }


    public void dump() {
        System.out.println("-------------DB LOCATION EVENT DUMP-------------");
        System.out.println("id = " + id);
        System.out.println("date = " + date);
        System.out.println("latitude = " + latitude);
        System.out.println("longitude = " + longitude);
        System.out.println("accuracy = " + doubleTrunkTwoDigit(accuracy));
        System.out.println("trigger = " + trigger);
    }

    public long getId() {
        return id;
    }

    public DbLocationEvent(Location location) {
        this.id = 0;
        this.date = location.getTime();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.accuracy = location.getAccuracy();
        this.trigger = -1;
    }

    public String buildSerializedDataString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append("\"id\":" + this.getId());
        stringBuilder.append("\"date\":" + this.getDate());
        stringBuilder.append("\"latitude\":" + this.getLatitude());
        stringBuilder.append("\"longitude\":" + this.getLongitude());
        stringBuilder.append("\"accuracy\":" + this.getAccuracy());
        stringBuilder.append("\"trigger\":" + this.getTrigger());
        stringBuilder.append("}");
        return stringBuilder.toString();
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

    public long writeMe() {
        DbLocationEventDAO dbLocationEventDAO = new DbLocationEventDAO();
        return dbLocationEventDAO.upsert(this);
    }

    public void deleteMe() {
        DbLocationEventDAO dbLocationEventDAO = new DbLocationEventDAO();
        dbLocationEventDAO.delete(this.getId());
    }

    public void setId(long id) {
        System.out.println("setting id = " + id);
        this.id = id;
    }
}
