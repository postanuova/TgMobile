package org.teenguard.child.dbdao;

import android.content.ContentValues;
import android.database.Cursor;

import org.teenguard.child.dbdatatype.DbMediaEvent;
import org.teenguard.child.utils.MyLog;

import java.util.ArrayList;

import static org.teenguard.child.dbdao.DbMediaDAO.MEDIA_TABLE;

/**
 * Created by chris on 18/10/16.
 */

public class DbMediaEventDAO extends GenericDbDAO {
    //contact table
    public final static String MEDIA_EVENT_TABLE = "media_event";
    public final static String MEDIA_EVENT_ID = "_id";
    public final static String MEDIA_EVENT_CS_ID = "cs_id"; //foreign key references contact._id
    public final static String MEDIA_EVENT_TYPE = "event_type";
    public final static String MEDIA_EVENT_SERIALIZED_DATA = "serialized_data";
    public final static String MEDIA_EVENT_PATH = "path";
    public final static String MEDIA_EVENT_COMPRESSED_MEDIA_PATH = "compressed_media_path";


    public DbMediaEventDAO() {
        super();
    }

    private long upsert(long id, long csId, int eventType, String serializedData, String path, String compressedMediaPath) {
        ContentValues values = new ContentValues();

        if (id == 0) {
            values.putNull(MEDIA_EVENT_ID);
            values.put(MEDIA_EVENT_CS_ID, csId);
            values.put(MEDIA_EVENT_TYPE, eventType);
            values.put(MEDIA_EVENT_SERIALIZED_DATA, serializedData);
            values.put(MEDIA_EVENT_PATH,path);
            values.put(MEDIA_EVENT_COMPRESSED_MEDIA_PATH, compressedMediaPath);
            return db.insert(MEDIA_EVENT_TABLE, null, values);
        } else {
            values.put(MEDIA_EVENT_CS_ID, csId);
            values.put(MEDIA_EVENT_TYPE, eventType);
            values.put(MEDIA_EVENT_SERIALIZED_DATA, serializedData);
            values.put(MEDIA_EVENT_PATH,path);
            values.put(MEDIA_EVENT_COMPRESSED_MEDIA_PATH, compressedMediaPath);
            return db.update(MEDIA_EVENT_TABLE, values, MEDIA_EVENT_ID + "=" + id, null);
        }
    }

    public long upsert(DbMediaEvent dbMediaEvent) {
        long id = dbMediaEvent.getId();
        long csId = dbMediaEvent.getCsId();
        int eventType = dbMediaEvent.getEventType();
        String serializedData = dbMediaEvent.getSerializedData();
        String path = dbMediaEvent.getPath();
        String compressedMediaPath = dbMediaEvent.getCompressedMediaPath();
        return upsert(id, csId, eventType, serializedData, path, compressedMediaPath);
    }

    public void delete(DbMediaEvent dbMediaEvent) {
        String deleteQuery = " DELETE FROM " + MEDIA_EVENT_TABLE + " WHERE " + MEDIA_EVENT_ID + "=" + dbMediaEvent.getId() + ";";
        MyLog.i(this, "deleting mediaEvent = " + dbMediaEvent.getId() + " serialized data " + dbMediaEvent.getSerializedData());
        db.execSQL(deleteQuery);
    }

    public boolean emptyTable() {
        db.execSQL("DELETE FROM " + MEDIA_TABLE + ";");
        return true;
    }

    public ArrayList<DbMediaEvent> getList() {
        Cursor cursor = db.rawQuery("SELECT _id,cs_id,event_type,serialized_data FROM media_event",null);
        ArrayList <DbMediaEvent> dbMediaEventAL = new <DbMediaEvent> ArrayList();
        DbMediaEvent dbMediaEvent;
        if (cursor != null) {
            MyLog.i(this,"<<<<<<<<<<<<<<<< dbMediaEvent cursor.count" + cursor.getCount());
            int addCounter = 0;
            while (cursor.moveToNext()) {
                addCounter++;
                //dbMedia = new DbMedia(cursor);
                int id = cursor.getInt(cursor.getColumnIndex(MEDIA_EVENT_ID));
                int csId = cursor.getInt(cursor.getColumnIndex(MEDIA_EVENT_CS_ID));
                int eventType = cursor.getInt(cursor.getColumnIndex(MEDIA_EVENT_TYPE));
                String serializedData = cursor.getString(cursor.getColumnIndex(MEDIA_EVENT_SERIALIZED_DATA));
                String path = cursor.getString(cursor.getColumnIndex(MEDIA_EVENT_PATH));
                String compressedMediaPath = cursor.getString(cursor.getColumnIndex(MEDIA_EVENT_COMPRESSED_MEDIA_PATH));
                dbMediaEvent = new DbMediaEvent(id, csId, eventType,serializedData,path , compressedMediaPath);
                dbMediaEventAL.add(dbMediaEvent);
            }
            MyLog.i(this,"dbMediaEventAL.size " + dbMediaEventAL.size());
            if(!cursor.isClosed()) {
                cursor.close();
            }
        } else {
            MyLog.i(this,"WARNING: CURSOR IS NULL");
        }
        return dbMediaEventAL;
    }



    /**
     *
     * @param idList csv list of id to delete
     * @return
     */
    public boolean delete(String idList) {
        db.execSQL("DELETE FROM " + MEDIA_EVENT_TABLE + " WHERE _id IN(" + idList + ");");
        return true;
    }

}