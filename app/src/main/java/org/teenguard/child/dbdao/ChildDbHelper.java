package org.teenguard.child.dbdao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.teenguard.child.utils.MyLog;

/**
 * Created by chris on 11/10/16.
 */

// W/SQLiteConnectionPool: A SQLiteConnection object for database '/data/data/org.teenguard.localizationappnew/databases/contactDB' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
// TODO: 25/10/16 gestione transazioni centralizzata
// TODO: 25/10/16  rendere l'helper un singleton
//http://stackoverflow.com/questions/21644132/trying-to-increase-speed-of-bulk-inserts-in-sqlite
public class ChildDbHelper extends SQLiteOpenHelper {


    public static final String CHILD_DB_NAME = "contactDB";
    public static final int CHILD_DB_VERSION = 14;

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
                    "serialized_data TEXT"+
            ");";

    private static final String DROP_TABLE_CONTACT = "DROP TABLE IF EXISTS contact;";
    private static final String DROP_TABLE_CONTACT_EVENT = "DROP TABLE IF EXISTS contact_event;";
    private static final String DROP_TABLE_MEDIA = "DROP TABLE IF EXISTS media;";
    private static final String DROP_TABLE_MEDIA_EVENT = "DROP TABLE IF EXISTS media_event;";

    public ChildDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MyLog.i(this,"creating DB:");
        db.execSQL(CREATE_TABLE_CONTACT);
        db.execSQL(CREATE_TABLE_CONTACT_EVENT);
        db.execSQL(CREATE_TABLE_MEDIA);
        db.execSQL(CREATE_TABLE_MEDIA_EVENT);
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
        onCreate(db);
    }

    public void resetDatabase(SQLiteDatabase db) {
        MyLog.i(this, "destroyDatabase");
        db.execSQL(DROP_TABLE_CONTACT);
        db.execSQL(DROP_TABLE_CONTACT_EVENT);
        db.execSQL(DROP_TABLE_MEDIA);
        db.execSQL(DROP_TABLE_MEDIA_EVENT);
        onCreate(db);
    }

    public static void main(String args[]) {
        System.out.println(CREATE_TABLE_CONTACT);
        System.out.println(CREATE_TABLE_MEDIA);
    }

}
