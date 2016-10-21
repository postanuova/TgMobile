package org.teenguard.child.dbdao;

import android.content.ContentValues;

import org.teenguard.child.dbdatatype.DbMediaEvent;
import org.teenguard.child.utils.MyLog;

import static org.teenguard.child.dbdao.DbMediaDAO.MEDIA_TABLE;

/**
 * Created by chris on 18/10/16.
 */

public class DbMediaEventDAO extends GenericDbDAO {
    //contact table
    public final static String MEDIA_EVENT_TABLE = "contact_event";
    public final static String MEDIA_EVENT_ID = "_id";
    public final static String MEDIA_EVENT_CS_ID = "cs_id"; //foreign key references contact._id
    public final static String MEDIA_EVENT_TYPE = "event_type";
    public final static String MEDIA_EVENT_SERIALIZED_DATA = "serialized_data";

    public DbMediaEventDAO() {
        super();
    }

    private long upsert(long id, long csId, int eventType, String serializedData) {
        ContentValues values = new ContentValues();

        if (id == 0) {
            values.putNull(MEDIA_EVENT_ID);
            values.put(MEDIA_EVENT_CS_ID, csId);
            values.put(MEDIA_EVENT_TYPE, eventType);
            values.put(MEDIA_EVENT_SERIALIZED_DATA, serializedData);
            return db.insert(MEDIA_EVENT_TABLE, null, values);
        } else {
            values.put(MEDIA_EVENT_CS_ID, csId);
            values.put(MEDIA_EVENT_TYPE, eventType);
            values.put(MEDIA_EVENT_SERIALIZED_DATA, serializedData);
            return db.update(MEDIA_EVENT_TABLE, values, MEDIA_EVENT_ID + "=" + id, null);
        }
    }

    public long upsert(DbMediaEvent dbMediaEvent) {
        long id = dbMediaEvent.getId();
        long csId = dbMediaEvent.getCsId();
        int eventType = dbMediaEvent.getEventType();
        String serializedData = dbMediaEvent.getSerializedData();
        return upsert(id, csId, eventType, serializedData);
    }

    public void remove(DbMediaEvent dbMediaEvent) {
        String deleteQuery = "DELETE FROM " + MEDIA_EVENT_TABLE + " WHERE " + MEDIA_EVENT_ID + "=" + dbMediaEvent.getId() + ";";
        MyLog.i(this, "deleting contactEvent = " + dbMediaEvent.getId() + " serialized data " + dbMediaEvent.getSerializedData());
        db.execSQL(deleteQuery);
    }

    public boolean emptyTable() {
        db.execSQL("DELETE FROM " + MEDIA_TABLE + ";");
        return true;
    }
}