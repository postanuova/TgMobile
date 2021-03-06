package org.teenguard.child.dbdao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.teenguard.child.datatype.DeviceMedia;
import org.teenguard.child.dbdatatype.DbMedia;
import org.teenguard.child.utils.MyLog;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chris on 16/10/16.
 */

public class DbMediaDAO extends GenericDbDAO{


    //contact table
    public final static String MEDIA_TABLE = "media";
    public final static String MEDIA_ID = "_id"; //table primary key: IS THE CLIENT-SIDE ID
    public final static String MEDIA_PHONE_ID = "phone_id"; // internal device id for contacts

    public DbMediaDAO() {
        super();
    }

    public long upsert(long id, long phoneId) {
        ContentValues values = new ContentValues();

        if (id == 0) {
            values.putNull(MEDIA_ID);
            values.put(MEDIA_PHONE_ID, phoneId);
            return db.insert(MEDIA_TABLE, null, values);
        } else {
            values.put(MEDIA_PHONE_ID, phoneId);
            return db.update(MEDIA_TABLE, values, MEDIA_ID + "=" + id, null);
        }
    }

    public long upsert(DbMedia dbMedia) {
        long id = dbMedia.getId();
        long phoneId = dbMedia.getPhoneId();

        return upsert(id, phoneId);
    }

    public boolean emptyMediaTable() {
        db.execSQL("DELETE FROM " + MEDIA_TABLE + ";");
        return true;
        //throw new UnsupportedOperationException("emptyContactTable not implemented");
    }


    public Cursor getDbMediaCursor() {
        String[] cols = new String[]{MEDIA_ID, MEDIA_PHONE_ID};
        Cursor mCursor = db.query(true, MEDIA_TABLE, cols, null, null, null, null, null, null);
        return mCursor; // iterate to get each value.
    }

   /* public dumpDbMediaHM() {

    }*/

    public  ConcurrentHashMap<Integer, DbMedia> getDbMediaHM() {
        Cursor cursor = getDbMediaCursor();
        String columnAR[] = cursor.getColumnNames();
        /*for (String column: columnAR) {
            System.out.println("column = " + column);
        }*/
        ConcurrentHashMap dbMediaHM = new ConcurrentHashMap();
        DbMedia dbMedia;
        if (cursor != null) {
            try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MEDIA_ID));
                int phoneId = cursor.getInt(cursor.getColumnIndex(MEDIA_PHONE_ID));
                dbMedia = new DbMedia(id, phoneId);
                dbMediaHM.put(phoneId, dbMedia);
            }
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        } else {
            MyLog.i(this, "WARNING: CURSOR IS NULL");
        }
        return dbMediaHM;
    }


    public void removeMedia(DbMedia dbMedia) {
        String deleteQuery = "DELETE FROM " + MEDIA_TABLE + " WHERE " + MEDIA_ID + "=" + dbMedia.getId() + ";";
        MyLog.i(this, "deleting media id= " + dbMedia.getId() + " phoneId = " + dbMedia.getPhoneId() + " query " + deleteQuery);
        db.execSQL(deleteQuery);
    }

    public boolean emptyTable() {
        db.execSQL("DELETE FROM " +MEDIA_TABLE + ";");
        return true;
    }

/*
    private int phoneId;
    private long dateTaken;
    private int mediaType;
    private int mediaDuration;
    private float latitude;
    private float longitude;
    private float accuracy;
    private String uri;
    private String path;
 */
    public long bulkInsert(ConcurrentHashMap<Integer,DeviceMedia> deviceMediaHM) {
        long nInserted = 0;
        String sql = "insert into media values(?,?)";
        SQLiteStatement sqLiteStatement = db.compileStatement(sql);
        beginTransaction();
        try {
            for (DeviceMedia deviceMedia : deviceMediaHM.values()) {
                sqLiteStatement.clearBindings();
                sqLiteStatement.bindNull(1);
                sqLiteStatement.bindLong(2,deviceMedia.getPhoneId());
                /*sqLiteStatement.bindLong(3,deviceMedia.getDateTaken());
                sqLiteStatement.bindLong(4,deviceMedia.getMediaType());
                sqLiteStatement.bindLong(5,deviceMedia.getMediaDuration());
                sqLiteStatement.bindDouble(6,deviceMedia.getLatitude());
                sqLiteStatement.bindDouble(7,deviceMedia.getLongitude());
                sqLiteStatement.bindDouble(8,deviceMedia.getAccuracy());
                sqLiteStatement.bindString(9,deviceMedia.getUri());
                sqLiteStatement.bindString(10,deviceMedia.getPath());*/
                sqLiteStatement.executeInsert();
                nInserted ++;
            }
            setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("ERROR: BULK INSERT FAILED");
            e.printStackTrace();
        }
        finally {
            endTransaction();
        }
        return nInserted;
    }

}
