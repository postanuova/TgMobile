package org.teenguard.child.dbdatatype;

import android.database.Cursor;

import org.teenguard.child.utils.JSon;

import static org.teenguard.child.dbdao.DbContactDAO.CONTACT_ID;
import static org.teenguard.child.dbdao.DbContactDAO.CONTACT_LAST_MODIFIED;
import static org.teenguard.child.dbdao.DbContactDAO.CONTACT_NAME;
import static org.teenguard.child.dbdao.DbContactDAO.CONTACT_PHONE_ID;
import static org.teenguard.child.dbdao.DbContactDAO.CONTACT_SERIALIZED_DATA;

/**
 * Created by chris on 15/10/16.
 */

public class DbContact {
    private int id;
    private int phoneId;
    private String name;
    private long lastModified;
    private String serializedData; //contains serialized numbers separated by a comma

    public DbContact(int id, int phoneId, String name, long lastModified,String serializedData) {
        this.id = id;
        this.phoneId = phoneId;
        this.name = name;
        this.lastModified = lastModified;
        this.setSerializedData(serializedData);
    }

    public DbContact (Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(CONTACT_ID));
        int phoneId = cursor.getInt(cursor.getColumnIndex(CONTACT_PHONE_ID));
        String name = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
        long lastModified = cursor.getLong(cursor.getColumnIndex(CONTACT_LAST_MODIFIED));
        String serializedData = cursor.getString(cursor.getColumnIndex(CONTACT_SERIALIZED_DATA));
        DbContact dbContact = new DbContact(id, phoneId, name, lastModified, serializedData);
    }



    public void dump() {
        System.out.println("-------------DB CONTACT DUMP-------------");
        System.out.println("id = " + id);
        System.out.println("phoneId = " + phoneId);
        System.out.println("name = " + name);
        System.out.println("lastModified = " + lastModified);
        System.out.println("serializedData = " + getSerializedData());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getSerializedData() {
        return serializedData;
    }

    public void setSerializedData(String serializedData) {
        this.serializedData = serializedData;
    }
}

