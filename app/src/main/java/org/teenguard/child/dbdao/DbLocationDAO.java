package org.teenguard.child.dbdao;

import android.content.ContentValues;

import org.teenguard.child.dbdatatype.DbLocation;


/**
 * Created by chris on 30/10/16.
 */

public class DbLocationDAO extends GenericDbDAO {
    public final static String LOCATION_TABLE = "location";
    public final static String LOCATION_ID = "_id"; //table primary key: IS THE CLIENT-SIDE ID
    public final static String LOCATION_DATE = "date";
    public final static String LOCATION_LATITUDE = "latitude";
    public final static String LOCATION_LONGITUDE = "longitude";
    public final static String LOCATION_ACCURACY = "accuracy";
    public final static String LOCATION_TRIGGER = "trigger";

/*
private long id;
    private long date;
    private double latitude;
    private double longitude;
    private double accuracy;
    private int trigger;
 */

    private long upsert(long id, long date, double latitude, double longitude, double accuracy, int trigger){
        ContentValues values = new ContentValues();

        if(id == 0) {
            values.putNull(LOCATION_ID);
            values.put(LOCATION_DATE,date);
            values.put(LOCATION_LATITUDE,latitude);
            values.put(LOCATION_LONGITUDE, longitude);
            values.put(LOCATION_ACCURACY, accuracy);
            values.put(LOCATION_TRIGGER, trigger);
            return db.insert (LOCATION_TABLE, null, values);
        } else {
            values.put(LOCATION_DATE,date);
            values.put(LOCATION_LATITUDE,latitude);
            values.put(LOCATION_LONGITUDE, longitude);
            values.put(LOCATION_ACCURACY, accuracy);
            values.put(LOCATION_TRIGGER, trigger);
            return db.update(LOCATION_TABLE,values,LOCATION_ID +"=" + id,null);
        }
    }

    public long upsert(DbLocation dbLocation) {
        return upsert(dbLocation.getId(),dbLocation.getDate(),dbLocation.getLatitude(),dbLocation.getLongitude(),dbLocation.getAccuracy(),dbLocation.getTrigger());
    }
}
