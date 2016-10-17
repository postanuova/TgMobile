package org.teenguard.child.dbdao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.teenguard.child.dbdatatype.DbMedia;
import org.teenguard.child.utils.MyLog;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chris on 16/10/16.
 */

public class DbMediaDAO {
    private SQLiteDatabase db;
    private ChildDbHelper childDbHelper;

    //contact table
    public final static String MEDIA_TABLE = "media";
    public final static String MEDIA_ID = "_id"; //table primary key: IS THE CLIENT-SIDE ID
    public final static String MEDIA_PHONE_ID = "phone_id"; // internal device id for contacts

    public DbMediaDAO(Context context) {
        childDbHelper = new ChildDbHelper(context, ChildDbHelper.CHILD_DB_NAME, null, ChildDbHelper.CHILD_DB_VERSION);
        db = childDbHelper.getWritableDatabase();
    }

    public static ConcurrentHashMap<Integer, DbMedia> getDbMediaHM() {
        throw new UnsupportedOperationException("getDbMediaHM not implemented");
    }

    public long upsertDbMedia(long id, long phoneId) {
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

    public long upsertDbMedia(DbMedia dbMedia) {
        long id = dbMedia.getId();
        long phoneId = dbMedia.getPhoneId();

        return upsertDbMedia(id, phoneId);
    }

    public boolean emptyMediaTable() {
        db.execSQL("DELETE FROM " + MEDIA_TABLE + ";");
        return true;
        //throw new UnsupportedOperationException("emptyContactTable not implemented");
    }


    public Cursor getDbContactCursor() {
        String[] cols = new String[]{MEDIA_ID, MEDIA_PHONE_ID};
        Cursor mCursor = db.query(true, MEDIA_TABLE, cols, null, null, null, null, null, null);
        return mCursor; // iterate to get each value.
    }

    public ConcurrentHashMap getDBMediaHM() {
        Cursor cursor = getDbContactCursor();
        String columnAR[] = cursor.getColumnNames();
        /*for (String column: columnAR) {
            System.out.println("column = " + column);
        }*/
        ConcurrentHashMap dbMediaHM = new ConcurrentHashMap();
        DbMedia dbMedia;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MEDIA_ID));
                int phoneId = cursor.getInt(cursor.getColumnIndex(MEDIA_PHONE_ID));
                dbMedia = new DbMedia(id, phoneId);
                dbMediaHM.put(phoneId, dbMedia);
            }
            if (!cursor.isClosed()) {
                cursor.close();
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

}
