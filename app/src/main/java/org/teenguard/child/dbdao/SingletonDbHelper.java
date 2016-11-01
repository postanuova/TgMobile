package org.teenguard.child.dbdao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.teenguard.child.utils.MyLog;

/**
 * Created by chris on 01/11/16.
 * //http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
 */

public class SingletonDbHelper extends SQLiteOpenHelper {
    private static SingletonDbHelper singleInstance;
    public static final String CHILD_DB_NAME = "contactDB";
    public static final int CHILD_DB_VERSION = 19;

    private static final String  CREATE_TABLE_CONTACT=
            "CREATE TABLE contact (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," + // In SQLite a column declared INTEGER PRIMARY KEY will autoincrement by itself (int is not ok)
                    "phone_id INTEGER UNIQUE NOT NULL," +
                    "name TEXT," +
                    "last_modified INTEGER," +
                    "serialized_data TEXT" +
                    ");";
    private static final String CREATE_TABLE_CONTACT_EVENT =
            "CREATE TABLE contact_event (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "cs_id INTEGER," +
                    "event_type INTEGER," +
                    "serialized_data TEXT"+
                    ");";

    private static final String  CREATE_TABLE_MEDIA=
            "CREATE TABLE media (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," + // In SQLite a column declared INTEGER PRIMARY KEY will autoincrement by itself (int is not ok)
                    "phone_id INTEGER UNIQUE NOT NULL" +
                    ");";

    private static final String CREATE_TABLE_MEDIA_EVENT =
            "CREATE TABLE media_event (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "cs_id INTEGER," +
                    "event_type INTEGER," +
                    "serialized_data TEXT,"+
                    "path TEXT," +
                    "compressed_media_path TEXT" +
                    ");";

    private static final String CREATE_TABLE_LOCATION_EVENT =
            "CREATE TABLE location_event (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "date INTEGER," +
                    "latitude REAL," +
                    "longitude REAL," +
                    "accuracy REAL," +
                    "trigger INTEGER" +
                    ");";


    private static final String DROP_TABLE_CONTACT = "DROP TABLE IF EXISTS contact;";
    private static final String DROP_TABLE_CONTACT_EVENT = "DROP TABLE IF EXISTS contact_event;";
    private static final String DROP_TABLE_MEDIA = "DROP TABLE IF EXISTS media;";
    private static final String DROP_TABLE_MEDIA_EVENT = "DROP TABLE IF EXISTS media_event;";
    private static final String DROP_TABLE_LOCATION_EVENT =  "DROP TABLE IF EXISTS location_event;";

    private SingletonDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized SingletonDbHelper getInstance(Context context) {
        if (singleInstance == null) {
            singleInstance = new SingletonDbHelper(context.getApplicationContext(),CHILD_DB_NAME,null,CHILD_DB_VERSION);
        }
        return singleInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MyLog.i(this,"creating DB:");
        db.execSQL(CREATE_TABLE_CONTACT);
        db.execSQL(CREATE_TABLE_CONTACT_EVENT);
        db.execSQL(CREATE_TABLE_MEDIA);
        db.execSQL(CREATE_TABLE_MEDIA_EVENT);
        db.execSQL(CREATE_TABLE_LOCATION_EVENT);
        MyLog.i(this, "created db:"  + CHILD_DB_NAME + " version " + CHILD_DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MyLog.i(this, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        //throw new UnsupportedOperationException("onUpgrade DB not yet implemented");
        db.execSQL(DROP_TABLE_CONTACT);
        db.execSQL(DROP_TABLE_CONTACT_EVENT);
        db.execSQL(DROP_TABLE_MEDIA);
        db.execSQL(DROP_TABLE_MEDIA_EVENT);
        db.execSQL(DROP_TABLE_LOCATION_EVENT);
        onCreate(db);
    }

    public void resetDatabase(SQLiteDatabase db) {
        MyLog.i(this, "destroyDatabase");
        db.execSQL(DROP_TABLE_CONTACT);
        db.execSQL(DROP_TABLE_CONTACT_EVENT);
        db.execSQL(DROP_TABLE_MEDIA);
        db.execSQL(DROP_TABLE_MEDIA_EVENT);
        db.execSQL(DROP_TABLE_LOCATION_EVENT);
        onCreate(db);
    }

    public static void main(String args[]) {
        System.out.println(CREATE_TABLE_CONTACT);
        System.out.println(CREATE_TABLE_MEDIA);
    }

}
