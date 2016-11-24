package org.teenguard.child.dbdatatype;

import org.teenguard.child.utils.CalendarUtils;
import org.teenguard.child.utils.JSon;

/**
 * Created by chris on 15/10/16.
 */

public class DbContact {
    private long id;
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

    /*public DbContact (Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(CONTACT_ID));
        int phoneId = cursor.getInt(cursor.getColumnIndex(CONTACT_PHONE_ID));
        String name = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
        long lastModified = cursor.getLong(cursor.getColumnIndex(CONTACT_LAST_MODIFIED));
        String serializedData = cursor.getString(cursor.getColumnIndex(CONTACT_SERIALIZED_DATA));
    }*/



    public JSon getJson() {
            JSon jSon = new JSon();
            jSon.add("id", this.getId());
            jSon.add("date", CalendarUtils.serverTimeFormat(this.getLastModified()));
            jSon.add("first_name", this.getName());
            jSon.add("last_name", "");
            jSon.addArray("phone_numbers", "[" + this.getSerializedData() + "]");
        return  jSon;
    }


    public void dump() {
        System.out.println("-------------DB CONTACT DUMP-------------");
        System.out.println("id = " + id);
        System.out.println("phoneId = " + phoneId);
        System.out.println("name = " + name);
        System.out.println("lastModified = " + lastModified);
        System.out.println("serializedData = " + getSerializedData());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

