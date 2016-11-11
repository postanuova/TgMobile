package org.teenguard.child.dbdao;

import android.content.ContentValues;
import android.database.Cursor;

import org.teenguard.child.dbdatatype.DbGeofenceEvent;
import org.teenguard.child.utils.MyLog;

import java.util.ArrayList;

/**
 * Created by chris on 09/11/16.
 * private long id;
 private String geofenceId;
 private long date;
 private int event;
 */

public class DbGeofenceEventDAO extends GenericDbDAO implements InterfaceDbDAO {
    public final static String TABLE ="geofence_event";
    public final static String GEOFENCE_EVENT_ID = "_id";
    public final static String GEOFENCE_EVENT_GEOFENCE_ID = "geofence_id";
    public final static String GEOFENCE_EVENT_DATE = "date";
    public final static String GEOFENCE_EVENT_EVENT = "event";


    public long upsert(DbGeofenceEvent object) {
        ContentValues values = new ContentValues();

        if (object.getId() == 0) {
            values.putNull(GEOFENCE_EVENT_ID);
            values.put(GEOFENCE_EVENT_GEOFENCE_ID,object.getGeofenceId());
            values.put(GEOFENCE_EVENT_DATE,object.getDate());
            values.put(GEOFENCE_EVENT_EVENT,object.getEvent());
            return db.insert(TABLE, null, values);
        } else {
            values.put(GEOFENCE_EVENT_GEOFENCE_ID,object.getGeofenceId());
            values.put(GEOFENCE_EVENT_DATE,object.getDate());
            values.put(GEOFENCE_EVENT_EVENT,object.getEvent());
            return db.update(TABLE, values, GEOFENCE_EVENT_ID + "=" + object.getId(), null);
        }
    }

    @Override
    public ArrayList<DbGeofenceEvent> getList(String whereSQL) {
        String query = "SELECT * FROM " + TABLE;
        if((whereSQL != null) && (whereSQL != "")) {
            query += whereSQL;
        }
        Cursor cursor = db.rawQuery(query,null);
        ArrayList <DbGeofenceEvent> objectAL = new <DbGeofenceEvent> ArrayList();
        DbGeofenceEvent object;
        if (cursor != null) {
            try {
                MyLog.i(this,"<<<<<<<<<<<<<<<< " + this.getClass() + " cursor.count" + cursor.getCount());
                int addCounter = 0;
                while (cursor.moveToNext()) {
                    addCounter++;
                    int id = cursor.getInt(cursor.getColumnIndex(GEOFENCE_EVENT_ID));
                    String geofenceId = cursor.getString(cursor.getColumnIndex(GEOFENCE_EVENT_GEOFENCE_ID));
                    long date = cursor.getLong(cursor.getColumnIndex(GEOFENCE_EVENT_DATE));
                    int event = cursor.getInt(cursor.getColumnIndex(GEOFENCE_EVENT_DATE));
                    object = new DbGeofenceEvent(id, geofenceId,date,event);
                    objectAL.add(object);
                }
                MyLog.i(this,"DbGeofenceEventAL.size " + objectAL.size());
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

    public ArrayList<DbGeofenceEvent> getList() {
        return getList(null);
    }

    public boolean delete(String idList) {
        if(idList != null) {
            db.execSQL("DELETE FROM " + TABLE + " WHERE _id IN(" + idList + ");");
        } else {
            db.execSQL("DELETE FROM " + TABLE + ";");
        }
        return true;
    }

    public boolean delete() {
        return delete(null);
    }
}