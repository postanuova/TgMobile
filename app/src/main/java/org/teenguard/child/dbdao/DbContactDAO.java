package org.teenguard.child.dbdao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.teenguard.child.datatype.DeviceContact;
import org.teenguard.child.dbdatatype.DbContact;
import org.teenguard.child.utils.JSon;
import org.teenguard.child.utils.MyLog;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by chris on 12/10/16.
 */

public class DbContactDAO extends GenericDbDAO{

    //contact table
    public final static String CONTACT_TABLE = "contact";
    public final static String CONTACT_ID = "_id"; //table primary key: IS THE CLIENT-SIDE ID
    public final static String CONTACT_PHONE_ID = "phone_id"; // internal device id for contacts
    public final static String CONTACT_NAME="name";
    public final static String CONTACT_LAST_MODIFIED = "last_modified";
    public final static String CONTACT_SERIALIZED_DATA = "serialized_data";
    //(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)

    //
   public DbContactDAO() {
       super();
   }



    /**
     *
     * @param id
     * @param phoneId
     * @param name
     * @param lastModified
     * @return the upserted id
     */
    private long upsert(long id, long phoneId, String name, long lastModified, String serializedData){
        ContentValues values = new ContentValues();

        if(id == 0) {
            values.putNull(CONTACT_ID);
            values.put(CONTACT_PHONE_ID,phoneId);
            values.put(CONTACT_NAME, name);
            values.put(CONTACT_LAST_MODIFIED,lastModified);
            values.put(CONTACT_SERIALIZED_DATA,serializedData);
            return db.insert (CONTACT_TABLE, null, values);
        } else {
            values.put(CONTACT_PHONE_ID,phoneId);
            values.put(CONTACT_NAME, name);
            values.put(CONTACT_LAST_MODIFIED,lastModified);
            values.put(CONTACT_SERIALIZED_DATA,serializedData);
            return db.update(CONTACT_TABLE,values,CONTACT_ID +"=" + id,null);
        }
    }

    public long upsert(DbContact dbContact) {
        long id = dbContact.getId();
        long phoneId = dbContact.getPhoneId();
        String name = JSon.purifyFromQuoteWithSubstitution(dbContact.getName());
        long lastModified = dbContact.getLastModified();
        String serializedData = dbContact.getSerializedData();
        return upsert(id,phoneId,name,lastModified,serializedData);
    }

    public boolean emptyTable() {
        db.execSQL("DELETE FROM " + CONTACT_TABLE + ";");
        return true;
        //throw new UnsupportedOperationException("emptyContactTable not implemented");
    }



    public Cursor getDbContactCursor() {
        String[] cols = new String[] {CONTACT_ID,CONTACT_PHONE_ID, CONTACT_NAME,CONTACT_LAST_MODIFIED, CONTACT_SERIALIZED_DATA};
        //Cursor mCursor = db.rawQuery("select _id,phone_id,name,last_modified from contact",null);
        Cursor mCursor = db.query(true, CONTACT_TABLE,cols,null, null, null, null, null, null);
        return mCursor; // iterate to get each value.
    }

    public ConcurrentHashMap getDbContactHM() {
        Cursor cursor = getDbContactCursor();
        ConcurrentHashMap dbContactHM = new ConcurrentHashMap();
        DbContact dbContact;
        if (cursor != null) {
            try {
                MyLog.i(this, "getDbContactHM cursor.count" + cursor.getCount());
                int putCounter = 0;
                while (cursor.moveToNext()) {
                    putCounter++;
                    //dbContact = new DbContact(cursor);
                    int id = cursor.getInt(cursor.getColumnIndex(CONTACT_ID));
                    int phoneId = cursor.getInt(cursor.getColumnIndex(CONTACT_PHONE_ID));
                    String name = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
                    long lastModified = cursor.getLong(cursor.getColumnIndex(CONTACT_LAST_MODIFIED));
                    String serializedData = cursor.getString(cursor.getColumnIndex(CONTACT_SERIALIZED_DATA));
                    dbContact = new DbContact(id, phoneId, JSon.purifyFromQuoteWithSubstitution(name), lastModified, JSon.purifyFromQuoteWithSubstitution(serializedData));
                    dbContactHM.put(dbContact.getPhoneId(), dbContact);
                }
                MyLog.i(this, "Added " + putCounter + " contacts to dbContactHM");

            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        } else {
            MyLog.i(this,"WARNING: CURSOR IS NULL");
        }
        return dbContactHM;
    }


    public void removeContact(DbContact dbContact) {
        String deleteQuery = "DELETE FROM " + CONTACT_TABLE + " WHERE " + CONTACT_ID + "=" + dbContact.getId() + ";";
        MyLog.i(this,"deleting contact = " + dbContact.getName() + " phoneId = " + dbContact.getPhoneId() + " query " + deleteQuery);
        db.execSQL(deleteQuery);
    }

    /*this.id = id;
    this.phoneId = phoneId;
    this.name = name;
    this.lastModified = lastModified;
    this.setSerializedData(serializedData);*/

    /*

     */
    public boolean bulkInsert(ConcurrentHashMap<Integer,DeviceContact> deviceContactHM) {
        String sql = "insert into contact values(?,?,?,?,?);";
        SQLiteStatement sqLiteStatement = db.compileStatement(sql);
        beginTransaction();
        try {
            for (DeviceContact deviceContact : deviceContactHM.values()) {
                sqLiteStatement.clearBindings();
                sqLiteStatement.bindNull(1);
                sqLiteStatement.bindLong(2,deviceContact.getPhoneId());
                sqLiteStatement.bindString(3,deviceContact.getName());
                sqLiteStatement.bindLong(4,deviceContact.getLastModified());
                sqLiteStatement.bindString(5,deviceContact.getNumbersJSonAR());
                sqLiteStatement.executeInsert();
            }
            setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("DbContactDAO.bulkInsert  ERROR: BULK INSERT FAILED");
            e.printStackTrace();
        }
            finally {
                endTransaction();
            }
        return true;
    }

    public DbContact getDbContactFromPhoneId (int phoneId) {
        String query = "SELECT _id,phone_id,name,last_modified FROM " + CONTACT_TABLE + " WHERE " +CONTACT_PHONE_ID + "=" + phoneId;
        DbContact dbContact = null;
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null) {
            try{
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(CONTACT_ID));
                    //int phoneId = cursor.getInt(cursor.getColumnIndex(CONTACT_PHONE_ID));
                    String name = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
                    long lastModified = cursor.getLong(cursor.getColumnIndex(CONTACT_LAST_MODIFIED));
                    String serializedData = cursor.getString(cursor.getColumnIndex(CONTACT_SERIALIZED_DATA));
                    dbContact = new DbContact(id, phoneId, name, lastModified, serializedData);
                }
            } finally {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        } else {
            MyLog.i(this,"WARNING: CURSOR IS NULL");
        }
        return dbContact;
    }

    public static void main (String args[]) {

    }


}
