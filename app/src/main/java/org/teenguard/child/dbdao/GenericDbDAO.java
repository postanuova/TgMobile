package org.teenguard.child.dbdao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by chris on 18/10/16.
 */

public  class GenericDbDAO {
    public  SQLiteDatabase db;
    public ChildDbHelper childDbHelper;

    public GenericDbDAO(Context context) {
        ChildDbHelper childDbHelper = new ChildDbHelper(context, ChildDbHelper.CHILD_DB_NAME, null, ChildDbHelper.CHILD_DB_VERSION);
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
