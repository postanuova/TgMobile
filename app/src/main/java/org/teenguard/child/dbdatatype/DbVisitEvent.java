package org.teenguard.child.dbdatatype;

/**
 * Created by chris on 02/11/16.
 */

public class DbVisitEvent {
    private long id;
    private long arrivalDate;
    private long departureDate;
    private float latitude;
    private float longitude;
    private float accuracy;

    public DbVisitEvent(long arrivalDate, long departureDate, float latitude, float longitude,float accuracy) {
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }

    public void dump() {
        System.out.println("--------- DB VISIT EVENT ---------");
        System.out.println("id = " + id);
        System.out.println("arrivalDate = " + arrivalDate);
        System.out.println("departureDate = " + departureDate);
        System.out.println("latitude = " + latitude);
        System.out.println("longitude = " + longitude);
        System.out.println("accuracy = " + accuracy);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(long arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public long getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(long departureDate) {
        this.departureDate = departureDate;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}
