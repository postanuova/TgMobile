package org.teenguard.child.dao;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import org.teenguard.child.datatype.DeviceContact;
import org.teenguard.child.utils.MyApp;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chris on 16/10/16.
 */

public class DeviceContactDAO {

    public static DeviceContact getDeviceContactFromPhoneId(int phoneId) {
        return null;
    }

    /**
     * @return numberAL of deviceContact list
     */
    public static ArrayList<String> getDeviceContactNumberALFromLookupKey(String lookupKey) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";

        String[] selectionArgs = {lookupKey};
        String sortOrder = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY;
        ContentResolver contentResolver = MyApp.getContext().getContentResolver();
        Cursor cursor = contentResolver.query(DeviceContact.CONTACTS_CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        //Log.i("test","Phone cursor rows count " + cursor.getCount());
        ArrayList numberAL = new ArrayList();
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            number = number.replaceAll("-","");
            numberAL.add(number);
        }
        cursor.close();
        return numberAL;
    }

    public static ConcurrentHashMap getDeviceContactHM() {
        ConcurrentHashMap tempDeviceContactHM = new ConcurrentHashMap();

        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String selection = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";

        String[] projection = {
                ContactsContract.Contacts._ID,              //0
                ContactsContract.Contacts.DISPLAY_NAME ,    //1
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
        };
        //String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
        ContentResolver contentResolver = MyApp.getContext().getContentResolver();
        Cursor deviceContactCursor = contentResolver.query(uri, projection, selection, null, null);

        Log.i("DeviceContactDAO", "getDeviceContactHM : deviceContactCursor columns " + deviceContactCursor.getColumnCount() + " rows " + deviceContactCursor.getCount());
        if(deviceContactCursor != null) {
            ////////////
            /*String columnNamesAR[] = deviceContactCursor.getColumnNames();
            for (int i = 0; i < columnNamesAR.length; i++) {
                Log.i("contactUri column names", i + " " + columnNamesAR[i]);
            }*/
            int _idIdx = deviceContactCursor.getColumnIndex(ContactsContract.Contacts._ID);
            int displayNameIdx = deviceContactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int lookupKeyIdx = deviceContactCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
            int lastUpdatedTimestampIdx = deviceContactCursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP);
            int deviceContactCounter = 0;
            while (deviceContactCursor.moveToNext()) {
                deviceContactCounter ++;
                int phoneId = deviceContactCursor.getInt(_idIdx);
                String displayName = deviceContactCursor.getString(displayNameIdx);
                String lookupKey = deviceContactCursor.getString(lookupKeyIdx);
                long lastUpdatedTimestamp = deviceContactCursor.getLong(lastUpdatedTimestampIdx);
                DeviceContact deviceContact = new DeviceContact(0,phoneId, displayName, lookupKey, lastUpdatedTimestamp);
                tempDeviceContactHM.put(phoneId, deviceContact);
                /*ArrayList<String> numberAL = getUserNumberAL(lookupKey);
                if(numberAL != null && numberAL.size()>0) {
                    Log.i( "Contacts","_ID " + _id + " DISPLAY_NAME " + displayName + " lastUpdatedTimestamp " + lastUpdatedTimestamp + " nameRawContactId " + nameRawContactId);
                    for(String number:numberAL) {
                        Log.i("number " , number);
                    }
                }*/
            }
            if((deviceContactCursor != null)&&(!deviceContactCursor.isClosed())) {
                deviceContactCursor.close();
            }
            Log.i("DeviceContactDAO","deviceContactCounter " + deviceContactCounter);
        }
        return tempDeviceContactHM;
    }
}
