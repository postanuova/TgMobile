package org.teenguard.child.dbdao;

import android.database.sqlite.SQLiteDatabase;

import org.teenguard.child.utils.MyApp;

/**
 * Created by chris on 18/10/16.
 */

public  class GenericDbDAO {
    public static SQLiteDatabase db; //only one instance if not android.database.sqlite.SQLiteDatabaseLockedException: database is locked (code 5)

    public GenericDbDAO() {
        ChildDbHelper childDbHelper = new ChildDbHelper(MyApp.getContext(), ChildDbHelper.CHILD_DB_NAME, null, ChildDbHelper.CHILD_DB_VERSION);
        db = childDbHelper.getWritableDatabase();
    }

    public void beginTransaction() {
        db.beginTransaction();
    }

    public void endTransaction(){
        db.endTransaction();
    }

    public void setTransactionSuccessful () {
        db.setTransactionSuccessful();
    }
}
