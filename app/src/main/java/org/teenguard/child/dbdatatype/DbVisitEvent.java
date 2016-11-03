package org.teenguard.child.dbdatatype;

import org.teenguard.child.dbdao.DbVisitEventDAO;

import java.util.Date;

/**
 * Created by chris on 02/11/16.
 */

public class DbVisitEvent {
    private long id;
    private long arrivalDate;
    private long departureDate;
    private double latitude;
    private double longitude;
    private double accuracy;

    public DbVisitEvent(){};
    
    
    public DbVisitEvent(long id,long arrivalDate, long departureDate, double latitude, double longitude,double accuracy) {
        this.id= id;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }



    public void dump() {
        System.out.println("--------- DB VISIT EVENT ---------");
        System.out.println("id = " + id);
        System.out.println("arrivalDate = "  + new Date(arrivalDate));
        System.out.println("departureDate = " + new Date(departureDate));
        System.out.println("latitude = " + latitude);
        System.out.println("longitude = " + longitude);
        System.out.println("accuracy = " + accuracy);
    }



    public void deleteMe() {
        DbVisitEventDAO dbVisitEventDAO = new DbVisitEventDAO();
        dbVisitEventDAO.delete(this.getId());
    }

    public String buildSerializedDataString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
//        stringBuilder.append("\"id\":" + this.getId());
        stringBuilder.append("\"arrival_date\":" + this.getArrivalDate());
        stringBuilder.append("\"departure_date\":" + this.getDepartureDate());
        stringBuilder.append("\"latitude\":" + this.getLatitude());
        stringBuilder.append("\"longitude\":" + this.getLongitude());
        stringBuilder.append("\"accuracy\":" + this.getAccuracy());
        stringBuilder.append("}");
        return stringBuilder.toString();
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

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public long writeMe() {
        DbVisitEventDAO dbVisitEventDAO = new DbVisitEventDAO();
        return dbVisitEventDAO.upsert(this);
    }
}
