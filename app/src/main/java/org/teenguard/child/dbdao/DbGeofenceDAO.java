package org.teenguard.child.dbdao;

import android.content.ContentValues;
import android.database.Cursor;

import org.teenguard.child.dbdatatype.DbGeofence;
import org.teenguard.child.utils.MyLog;

import java.util.ArrayList;

/*private long id;
private String geofenceId;
private double latitude;
private double longitude;
private int radius;
private boolean enter;
private boolean leave;*/

/**
 * Created by chris on 09/11/16.
 */

public class DbGeofenceDAO extends GenericDbDAO implements InterfaceDbDAO {
    public final static String TABLE ="geofence";
    public final static String GEOFENCE_ID = "_id";
    public final static String GEOFENCE_GEOFENCE_ID = "geofence_id";
    public final static String GEOFENCE_LATITUDE = "latitude";
    public final static String GEOFENCE_LONGITUDE = "longitude";
    public final static String GEOFENCE_RADIUS = "radius";
    public final static String GEOFENCE_ENTER = "enter";
    public final static String GEOFENCE_LEAVE = "leave";


    private long upsert(DbGeofence object) {
        ContentValues values = new ContentValues();

        if (object.getId() == 0) {
            values.putNull(GEOFENCE_ID);
            values.put(GEOFENCE_GEOFENCE_ID,object.getGeofenceId());
            values.put(GEOFENCE_LATITUDE,object.getLatitude());
            values.put(GEOFENCE_LONGITUDE,object.getLongitude());
            values.put(GEOFENCE_RADIUS,object.getRadius());
            values.put(GEOFENCE_ENTER,object.getEnter());
            values.put(GEOFENCE_LEAVE,object.getLeave());
            return db.insert(TABLE, null, values);
        } else {
            values.put(GEOFENCE_GEOFENCE_ID,object.getGeofenceId());
            values.put(GEOFENCE_LATITUDE,object.getLatitude());
            values.put(GEOFENCE_LONGITUDE,object.getLongitude());
            values.put(GEOFENCE_RADIUS,object.getRadius());
            values.put(GEOFENCE_ENTER,object.getEnter());
            values.put(GEOFENCE_LEAVE,object.getLeave());
            return db.update(TABLE, values, GEOFENCE_ID + "=" + object.getId(), null);
        }
    }

    @Override
    public ArrayList <DbGeofence> getList(String whereSQL) {
        String query = "SELECT * FROM " + TABLE;
        if((whereSQL != null) && (whereSQL != "")) {
            query += whereSQL;
        }
        Cursor cursor = db.rawQuery(query,null);
        ArrayList <DbGeofence> objectAL = new <DbGeofence> ArrayList();
        DbGeofence object;
        if (cursor != null) {
            try {
                MyLog.i(this,"<<<<<<<<<<<<<<<< " + this.getClass() + " cursor.count" + cursor.getCount());
                int addCounter = 0;
                while (cursor.moveToNext()) {
                    addCounter++;
                    int id = cursor.getInt(cursor.getColumnIndex(GEOFENCE_ID));
                   String geofenceId = cursor.getString(cursor.getColumnIndex(GEOFENCE_GEOFENCE_ID));
                    double latitude = cursor.getDouble(cursor.getColumnIndex(GEOFENCE_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(GEOFENCE_LONGITUDE));
                    int radius = cursor.getInt(cursor.getColumnIndex(GEOFENCE_RADIUS));
                    int enter = cursor.getInt(cursor.getColumnIndex(GEOFENCE_ENTER));
                    int leave = cursor.getInt(cursor.getColumnIndex(GEOFENCE_LEAVE));

                    object = new DbGeofence(id, geofenceId, latitude,longitude,radius,enter,leave);
                    objectAL.add(object);
                }
                MyLog.i(this,"dbMediaEventAL.size " + objectAL.size());
            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        } else {
            MyLog.i(this,"WARNING: CURSOR IS NULL");
        }
        return objectAL;
    }

    public ArrayList<DbGeofence> getList() {
        return getList(null);
    }

    public boolean delete(String idList) {
        db.execSQL("DELETE FROM " + TABLE + " WHERE _id IN(" + idList + ");");
        return true;
    }
}
