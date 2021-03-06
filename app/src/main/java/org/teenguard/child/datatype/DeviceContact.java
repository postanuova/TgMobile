package org.teenguard.child.datatype;

import android.net.Uri;
import android.provider.ContactsContract;

import org.teenguard.child.dao.DeviceContactDAO;
import org.teenguard.child.utils.JSon;

import java.util.ArrayList;

/**
 * Created by chris on 15/10/16.
 */

public class DeviceContact {
    public static final Uri CONTACTS_URI = ContactsContract.Contacts.CONTENT_URI;
    public static final Uri CONTACTS_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
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
        setNumberAL(DeviceContactDAO.getDeviceContactNumberALFromLookupKey(lookupKey));
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
        for (String number : getNumberAL()) {
            System.out.println("number = " + number);
        }
    }

    public  String getNumbersJSonAR() {
        StringBuilder serializedData = new StringBuilder();
        for (String number : getNumberAL()) {
            number = number.trim();
            serializedData.append("\"" + number +"\"" + ",");
        }
        String serializedDataSTR = serializedData.toString();
        if(serializedDataSTR.endsWith(",")) serializedDataSTR = serializedDataSTR.substring(0,serializedDataSTR.length()-1);
        serializedDataSTR = "[" + serializedDataSTR + "]";
        return serializedDataSTR;
    }

    public String getJson() {
        JSon json = new JSon();
        return json.toString();
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
        return JSon.purifyFromQuoteWithSubstitution(name);
    }

    public void setName(String name) {
        this.name = JSon.purifyFromQuoteWithSubstitution(name);
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


    public ArrayList<String> getNumberAL() {
        return numberAL;
    }

    public void setNumberAL(ArrayList<String> numberAL) {
        this.numberAL = numberAL;
    }

    public static String purifyNumber(String number) {
        //System.out.println("DeviceContact.purifyNumber " + number);
        return number.replaceAll("[^0-9]","");
        /*number = number.replaceAll("\\+","");
        number = number.trim();*/
        //System.out.println("DeviceContact.purifyNumber purified " + number);
    }
}

