package org.teenguard.child.dbdao;

import android.content.ContentValues;
import android.database.Cursor;

import org.teenguard.child.dbdatatype.DbVisitEvent;
import org.teenguard.child.utils.MyLog;

import java.util.ArrayList;

/**
 * Created by chris on 03/11/16.
 */
public class DbVisitEventDAO extends GenericDbDAO {
    public final static String TABLE = "visit_event";
    public final static String VISIT_EVENT_ID = "_id"; //table primary key: IS THE CLIENT-SIDE ID
    public final static String VISIT_EVENT_ARRIVAL_DATE = "arrival_date";
    public final static String VISIT_EVENT_DEPARTURE_DATE = "departure_date";
    public final static String VISIT_EVENT_LATITUDE = "latitude";
    public final static String VISIT_EVENT_LONGITUDE = "longitude";
    public final static String VISIT_EVENT_ACCURACY = "accuracy";


    public long upsert(DbVisitEvent dbVisitEvent) {
        return upsert(dbVisitEvent.getId(),dbVisitEvent.getArrivalDate(),dbVisitEvent.getDepartureDate(),dbVisitEvent.getLatitude(),dbVisitEvent.getLongitude(),dbVisitEvent.getAccuracy());
    }

    public void delete(long id) {
        String deleteQuery = "DELETE FROM " + TABLE + " WHERE " + VISIT_EVENT_ID + "=" + id + ";";
        MyLog.i(this,"deleting location id = " + id + " query " + deleteQuery);
        db.execSQL(deleteQuery);

    }

    public void delete() {
        String deleteQuery = "DELETE FROM " + TABLE;
        db.execSQL(deleteQuery);
    }

    private long upsert(long id, long arrivalDate,long departureDate, double latitude, double longitude, double accuracy){
        ContentValues values = new ContentValues();

        if(id == 0) {
            System.out.println("id = 0: insert");
            values.putNull(VISIT_EVENT_ID);
            values.put(VISIT_EVENT_ARRIVAL_DATE,arrivalDate);
            values.put(VISIT_EVENT_DEPARTURE_DATE,departureDate);
            values.put(VISIT_EVENT_LATITUDE,latitude);
            values.put(VISIT_EVENT_LONGITUDE, longitude);
            values.put(VISIT_EVENT_ACCURACY, accuracy);
            return db.insert(TABLE, null, values);
        } else {
            values.put(VISIT_EVENT_ARRIVAL_DATE,arrivalDate);
            values.put(VISIT_EVENT_DEPARTURE_DATE,departureDate);
            values.put(VISIT_EVENT_LATITUDE,latitude);
            values.put(VISIT_EVENT_LONGITUDE, longitude);
            values.put(VISIT_EVENT_ACCURACY, accuracy);
            return db.update(TABLE,values,VISIT_EVENT_ID +"=" + id,null);
        }
    }

    public Cursor getDbVisitEventCursor() {
        String[] cols = new String[] {VISIT_EVENT_ID,VISIT_EVENT_ARRIVAL_DATE,VISIT_EVENT_DEPARTURE_DATE,VISIT_EVENT_LATITUDE,VISIT_EVENT_LONGITUDE,VISIT_EVENT_ACCURACY,VISIT_EVENT_ACCURACY};
        Cursor mCursor = db.query(true, TABLE,cols,null, null, null, null, null, null);
        return mCursor; // iterate to get each value.
    }

    public void delete(String idList) {
        String deleteQuery = " DELETE FROM " + TABLE + " WHERE " + VISIT_EVENT_ID  + " IN ("   + idList + ");";
        System.out.println("deleteQuery = " + deleteQuery);
        db.execSQL(deleteQuery);
    }

    public ArrayList<DbVisitEvent> getList() {
        Cursor cursor = getDbVisitEventCursor();
        //String columnAR [] = cursor.getColumnNames();
        /*for (String column: columnAR) {
            System.out.println("column = " + column);
        }*/
        ArrayList <DbVisitEvent> dbVisitEventAL = new ArrayList<DbVisitEvent>();
        DbVisitEvent dbVisitEvent;
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(VISIT_EVENT_ID));
                    long arrivalDate = cursor.getLong(cursor.getColumnIndex(VISIT_EVENT_ARRIVAL_DATE));
                    long departureDate = cursor.getLong(cursor.getColumnIndex(VISIT_EVENT_DEPARTURE_DATE));
                    double latitude = cursor.getDouble(cursor.getColumnIndex(VISIT_EVENT_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(VISIT_EVENT_LONGITUDE));
                    double accuracy = cursor.getDouble(cursor.getColumnIndex(VISIT_EVENT_ACCURACY));
                    dbVisitEvent = new DbVisitEvent(id,arrivalDate,departureDate,latitude,longitude,accuracy);
                    dbVisitEventAL.add(dbVisitEvent);
                }
            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        } else {
            MyLog.i(this,"WARNING: CURSOR IS NULL");
        }
        return dbVisitEventAL;
    }

    public boolean emptyTable() {
        db.execSQL("DELETE FROM " + TABLE + ";");
        return true;
        //throw new UnsupportedOperationException("emptyContactTable not implemented");
    }

}
