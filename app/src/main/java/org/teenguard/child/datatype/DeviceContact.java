package org.teenguard.child.datatype;

import org.teenguard.child.dao.DeviceContactDAO;

import java.util.ArrayList;

/**
 * Created by chris on 15/10/16.
 */

public class DeviceContact {
   // private int id;
    private int phoneId;
    private String name;
    private String lookupKey;
    private long lastModified;
    private ArrayList<String> numberAL;

    public DeviceContact(int id, int phoneId, String name, String lookupKey, long lastModified) {
      //  this.id = id;
        this.phoneId = phoneId;
        this.name = name;
        this.lookupKey = lookupKey;
        this.lastModified = lastModified;
        numberAL = DeviceContactDAO.getDeviceContactNumberALFromLookupKey(lookupKey);
    }


    public void dump() {
        System.out.println("-------------DEVICE CONTACT DUMP-------------");
      //  System.out.println("id = " + id);
        System.out.println("phoneId = " + phoneId);
        System.out.println("name = " + name);
        System.out.println("lookupKey = " + lookupKey);
        System.out.println("lastModified = " + lastModified);
        dumpDeviceContactNumberAL();
    }

    public void dumpDeviceContactNumberAL() {
        for (String number : numberAL) {
            System.out.println("number = " + number);
        }
    }

    public  String buildSerializedDataString() {
        StringBuilder serializedData = new StringBuilder();
        for (String number : numberAL) {
            serializedData.append(number + ";");
        }
        return serializedData.toString();
    }

  /*  public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

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

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }


}

