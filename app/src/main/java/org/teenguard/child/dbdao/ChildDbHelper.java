package org.teenguard.child.dbdao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.teenguard.child.utils.MyLog;

/**
 * Created by chris on 11/10/16.
 */

public class ChildDbHelper extends SQLiteOpenHelper {


    public static final String CHILD_DB_NAME = "contactDB";
    public static final int CHILD_DB_VERSION = 9;


    private static final String  CREATE_DATABASE=
            "CREATE TABLE contact (" +
            "_id integer primary key autoincrement," + // In SQLite a column declared INTEGER PRIMARY KEY will autoincrement by itself (int is not ok)
            "phone_id int unique not null," +
            "name varchar(255)," +
            "last_modified int," +
            "serialized_data" +
            ");" +
            "CREATE TABLE media (" +
            "_id integer primary key autoincrement," + // In SQLite a column declared INTEGER PRIMARY KEY will autoincrement by itself (int is not ok)
            "phone_id int unique not null);";
    /*
+---------------+--------------+------+-----+---------+----------------+
| Field         | Type         | Null | Key | Default | Extra          |
+---------------+--------------+------+-----+---------+----------------+
| _id            | int(11)      | NO   | PRI | NULL    | auto_increment |
| phone_id         | int(11)      | NO   | UNI | NULL    |                |
| name          | varchar(255) | YES  |     | NULL    |                |
| last_modified | int(11)      | YES  |     | NULL    |                |
+---------------+--------------+------+-----+---------+----------------+
            */

    private static final String DROP_TABLES = "DROP TABLE IF EXISTS contact" +
            "DROP TABLE IF EXISTS media";



    public ChildDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MyLog.i(this,"creating DB: " + CREATE_DATABASE);
        db.execSQL(CREATE_DATABASE);
        MyLog.i(this, "created db:"  + CHILD_DB_NAME + " version " + CHILD_DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MyLog.i(this, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        //throw new UnsupportedOperationException("onUpgrade DB not yet implemented");
        db.execSQL(DROP_TABLES);
        onCreate(db);
    }

    public void resetDatabase(SQLiteDatabase db) {
        MyLog.i(this, "destroyDatabase");
        //throw new UnsupportedOperationException("onUpgrade DB not yet implemented");
        db.execSQL(DROP_TABLES);
        onCreate(db);
    }


    public static void main(String args[]) {
        System.out.println(CREATE_DATABASE);
    }



}
