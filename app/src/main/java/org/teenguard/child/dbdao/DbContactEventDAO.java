package org.teenguard.child.dbdao;

import android.content.ContentValues;
import android.database.Cursor;

import org.teenguard.child.dbdatatype.DbContactEvent;
import org.teenguard.child.utils.MyLog;

import java.util.ArrayList;


/**
 * Created by chris on 18/10/16.
 */

public class DbContactEventDAO extends GenericDbDAO {
    //contact table
    public final static String CONTACT_EVENT_TABLE = "contact_event";
    public final static String CONTACT_EVENT_ID = "_id";
    public final static String CONTACT_EVENT_CS_ID = "cs_id"; //foreign key references contact._id
    public final static String CONTACT_EVENT_TYPE="event_type";
    public final static String CONTACT_EVENT_SERIALIZED_DATA = "serialized_data";

    public DbContactEventDAO() {
        super();
    }

    private long upsert(long id, long csId, int eventType, String serializedData){
        ContentValues values = new ContentValues();

        if(id == 0) {
            values.putNull(CONTACT_EVENT_ID);
            values.put(CONTACT_EVENT_CS_ID,csId);
            values.put(CONTACT_EVENT_TYPE, eventType);
            values.put(CONTACT_EVENT_SERIALIZED_DATA,serializedData);
            return db.insert (CONTACT_EVENT_TABLE, null, values);
        } else {
            values.put(CONTACT_EVENT_CS_ID,csId);
            values.put(CONTACT_EVENT_TYPE, eventType);
            values.put(CONTACT_EVENT_SERIALIZED_DATA,serializedData);
            return db.update(CONTACT_EVENT_TABLE,values,CONTACT_EVENT_ID +"=" + id,null);
        }
    }

    public long upsert(DbContactEvent dbContactEvent) {
        long id = dbContactEvent.getId();
        long csId = dbContactEvent.getCsId();
        int eventType = dbContactEvent.getEventType();
        String serializedData = dbContactEvent.getSerializedData();
        return upsert(id,csId,eventType,serializedData);
    }

    public void delete(DbContactEvent dbContactEvent) {
        String deleteQuery = "DELETE FROM " + CONTACT_EVENT_TABLE + " WHERE " + CONTACT_EVENT_ID + "=" + dbContactEvent.getId() + ";";
        MyLog.i(this,"deleting contactEvent = " + dbContactEvent.getId() + " serialized data " + dbContactEvent.getSerializedData());
        db.execSQL(deleteQuery);
    }


    public boolean emptyTable() {
        db.execSQL("DELETE FROM " + CONTACT_EVENT_TABLE + ";");
        return true;
    }

    /**
     *
     * @param idList csv list of id to delete
     * @return
     */
    public boolean delete(String idList) {
        db.execSQL("DELETE FROM " + CONTACT_EVENT_TABLE + " WHERE _id IN(" + idList + ");");
        return true;
    }


    public ArrayList<DbContactEvent> getList() {
        Cursor cursor = db.rawQuery("SELECT _id,cs_id,event_type,serialized_data FROM contact_event",null);
        ArrayList <DbContactEvent> dbContactEventAL = new <DbContactEvent> ArrayList();
        DbContactEvent dbContactEvent;
        if (cursor != null) {
            try {
                MyLog.i(this,"<<<<<<<<<<<<<<<< dbContactEvent cursor.count" + cursor.getCount());
                int addCounter = 0;
                while (cursor.moveToNext()) {
                    addCounter++;
                    //dbContact = new DbContact(cursor);
                    int id = cursor.getInt(cursor.getColumnIndex(CONTACT_EVENT_ID));
                    int csId = cursor.getInt(cursor.getColumnIndex(CONTACT_EVENT_CS_ID));
                    int eventType = cursor.getInt(cursor.getColumnIndex(CONTACT_EVENT_TYPE));
                    String serializedData = cursor.getString(cursor.getColumnIndex(CONTACT_EVENT_SERIALIZED_DATA));
                    dbContactEvent = new DbContactEvent(id, csId, eventType,serializedData);
                    dbContactEventAL.add(dbContactEvent);
                }
                MyLog.i(this,"putCounter " + addCounter);
            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        } else {
            MyLog.i(this,"WARNING: CURSOR IS NULL");
        }

        return dbContactEventAL;
    }



}