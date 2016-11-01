package org.teenguard.child.dbdao;

import android.content.ContentValues;
import android.database.Cursor;

import org.teenguard.child.dbdatatype.DbLocationEvent;
import org.teenguard.child.utils.MyLog;

import java.util.ArrayList;


/**
 * Created by chris on 30/10/16.
 */

public class DbLocationEventDAO extends GenericDbDAO {
    public final static String TABLE = "location_event";
    public final static String LOCATION_EVENT_ID = "_id"; //table primary key: IS THE CLIENT-SIDE ID
    public final static String LOCATION_EVENT_DATE = "date";
    public final static String LOCATION_EVENT_LATITUDE = "latitude";
    public final static String LOCATION_EVENT_LONGITUDE = "longitude";
    public final static String LOCATION_EVENT_ACCURACY = "accuracy";
    public final static String LOCATION_EVENT_TRIGGER = "trigger";

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
            System.out.println("id = 0: insert");
            values.putNull(LOCATION_EVENT_ID);
            values.put(LOCATION_EVENT_DATE,date);
            values.put(LOCATION_EVENT_LATITUDE,latitude);
            values.put(LOCATION_EVENT_LONGITUDE, longitude);
            values.put(LOCATION_EVENT_ACCURACY, accuracy);
            values.put(LOCATION_EVENT_TRIGGER, trigger);
            return db.insert(TABLE, null, values);
        } else {
            values.put(LOCATION_EVENT_DATE,date);
            values.put(LOCATION_EVENT_LATITUDE,latitude);
            values.put(LOCATION_EVENT_LONGITUDE, longitude);
            values.put(LOCATION_EVENT_ACCURACY, accuracy);
            values.put(LOCATION_EVENT_TRIGGER, trigger);
            return db.update(TABLE,values,LOCATION_EVENT_ID +"=" + id,null);
        }
    }

    public long upsert(DbLocationEvent dbLocationEvent) {
        return upsert(dbLocationEvent.getId(),dbLocationEvent.getDate(),dbLocationEvent.getLatitude(),dbLocationEvent.getLongitude(),dbLocationEvent.getAccuracy(),dbLocationEvent.getTrigger());
    }

    public void delete(long id) {
        String deleteQuery = "DELETE FROM " + TABLE + " WHERE " + LOCATION_EVENT_ID + "=" + id + ";";
        MyLog.i(this,"deleting location id = " + id + " query " + deleteQuery);
        db.execSQL(deleteQuery);

    }



    public Cursor getDbLocationEventCursor() {
        String[] cols = new String[] {LOCATION_EVENT_ID,LOCATION_EVENT_DATE,LOCATION_EVENT_LATITUDE,LOCATION_EVENT_LONGITUDE,LOCATION_EVENT_ACCURACY,LOCATION_EVENT_ACCURACY};
        //Cursor mCursor = db.rawQuery("select _id,phone_id,name,last_modified from contact",null);
        Cursor mCursor = db.query(true, TABLE,cols,null, null, null, null, null, null);
        return mCursor; // iterate to get each value.
    }

    public void delete(String idList) {
        String deleteQuery = " DELETE FROM " + TABLE + " WHERE " + LOCATION_EVENT_ID  + " IN ("   + idList + ");";
        System.out.println("deleteQuery = " + deleteQuery);
        db.execSQL(deleteQuery);
    }

    public ArrayList<DbLocationEvent> getList() {
        Cursor cursor = getDbLocationEventCursor();
        //String columnAR [] = cursor.getColumnNames();
        /*for (String column: columnAR) {
            System.out.println("column = " + column);
        }*/
        ArrayList <DbLocationEvent> dbLocationEventAL = new ArrayList<DbLocationEvent>();
        DbLocationEvent dbLocationEvent;
        if (cursor != null) {
            try {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndex(LOCATION_EVENT_ID));
                        long date = cursor.getLong(cursor.getColumnIndex(LOCATION_EVENT_DATE));
                        double latitude = cursor.getDouble(cursor.getColumnIndex(LOCATION_EVENT_LATITUDE));
                        double longitude = cursor.getDouble(cursor.getColumnIndex(LOCATION_EVENT_LONGITUDE));
                        double accuracy = cursor.getDouble(cursor.getColumnIndex(LOCATION_EVENT_ACCURACY));
                        int trigger = cursor.getInt(cursor.getColumnIndex(LOCATION_EVENT_TRIGGER));
                        dbLocationEvent = new DbLocationEvent(id,date,latitude,longitude,accuracy,trigger);
                        dbLocationEventAL.add(dbLocationEvent);
                    }
                } finally {
                    if (!cursor.isClosed()) {
                        cursor.close();
                    }
        }
        } else {
            MyLog.i(this,"WARNING: CURSOR IS NULL");
        }
        return dbLocationEventAL;
    }

    public boolean emptyTable() {
        db.execSQL("DELETE FROM " + TABLE + ";");
        return true;
        //throw new UnsupportedOperationException("emptyContactTable not implemented");
    }

}
