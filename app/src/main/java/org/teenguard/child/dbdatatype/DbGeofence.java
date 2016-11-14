package org.teenguard.child.dbdatatype;

import org.teenguard.child.dbdao.DbGeofenceDAO;
import org.teenguard.child.utils.TypeConverter;

/**
 * Created by chris on 01/11/16.
 */
//http://92.222.83.28/child/beat.php
    /*
    { "data": { "geofences": [ { "id": "Lincontro", "latitude": 28.120483, "longitude": -16.7775494, "radius": 100, "enter": true, "leave": true }, { "id": "Ale", "latitude": 28.1250742, "longitude": -16.7779788, "radius": 100, "enter": true, "leave": true }, { "id": "SiamMall", "latitude": 28.0690565, "longitude": -16.7249978, "radius": 100, "enter": true, "leave": true }, { "id": "Michele", "latitude": 28.1251502, "longitude": -16.7394207, "radius": 100, "enter": true, "leave": true }, { "id": "ChiesaLosCristianos", "latitude": 28.0521532, "longitude": -16.7177612, "radius": 100, "enter": true, "leave": true } ] }, "t": 3600, "h": "6f4ef2a89f7a834a65c1d6bc4147a4a792504848" }
     https://developer.android.com/training/location/geofencing.html#Troubleshooting
     */
public class DbGeofence implements InterfaceDbDatatype {
    private long id;
    private String geofenceId;
    private double latitude;
    private double longitude;
    private int radius;
    private int enter;
    private int leave;

    public DbGeofence(int id, String geofenceId, double latitude, double longitude, int radius, int enter, int leave) {
        this.id = id;
        this.geofenceId = geofenceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.enter = enter;
        this.leave = leave;
    }

    public DbGeofence() {};

    public DbGeofence(int id, String geofenceId, double latitude, double longitude, int radius, boolean enter, boolean leave) {
        this.id = id;
        this.geofenceId = geofenceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.enter = TypeConverter.booleanToInt(enter);
        this.leave = TypeConverter.booleanToInt(leave);
    }

    @Override
    public void dump() {
        System.out.println("--------- DB GEOFENCE DUMP -----------");
        System.out.println("id = " + id);
        System.out.println("geofenceId = " + geofenceId);
        System.out.println("latitude = " + latitude);
        System.out.println("longitude = " + longitude);
        System.out.println("radius = " + radius);
        System.out.println("enter = " + enter);
        System.out.println("leave = " + leave);
    }

    @Override
    public void deleteMe() {
        DbGeofenceDAO objectDAO = new DbGeofenceDAO();
        objectDAO.delete(""+this.getId());
    }

    @Override
    public long writeMe() {
        DbGeofenceDAO objectDAO = new DbGeofenceDAO();
        return objectDAO.upsert(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


    public String getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(String geofenceId) {
        this.geofenceId = geofenceId;
    }

    public int getEnter() {
        return enter;
    }

    public void setEnter(int enter) {
        this.enter = enter;
    }

    public int getLeave() {
        return leave;
    }

    public void setLeave(int leave) {
        this.leave = leave;
    }
}
