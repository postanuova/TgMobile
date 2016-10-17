package org.teenguard.child.dao;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import org.teenguard.child.datatype.DeviceMedia;
import org.teenguard.child.utils.MyApp;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chris on 16/10/16.
 */

public class DeviceMediaDAO {
    public static ConcurrentHashMap<Integer,DeviceMedia> getDeviceMediaHM() {
        ConcurrentHashMap tempDeviceMediaHM = new ConcurrentHashMap();



       /* String selection = "((" + MediaStore.Images.ImageColumns._ID + " NOTNULL) AND ("
                + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";*/

        String[] projection = {  MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN };
        //String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
        ContentResolver contentResolver = MyApp.getContext().getContentResolver();
        Cursor deviceMediaCursor = contentResolver.query(DeviceMedia.PHOTO_EXTERNAL_CONTENT_URI, projection, null, null, null);

        Log.i("DeviceMediaDAO", "getDeviceMediaHM : deviceMediaCursor columns " + deviceMediaCursor.getColumnCount() + " rows " + deviceMediaCursor.getCount());
        if(deviceMediaCursor != null) {
            String columnNamesAR[] = deviceMediaCursor.getColumnNames();
            for (int i = 0; i < columnNamesAR.length; i++) {
                Log.i("media column names", i + " " + columnNamesAR[i]);
            }
            int _idIdx = deviceMediaCursor.getColumnIndex( MediaStore.Images.ImageColumns._ID);
            int dateTakenIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
            int deviceMediaCounter = 0;
            while (deviceMediaCursor.moveToNext()) {
                deviceMediaCounter ++;
                int phoneId = deviceMediaCursor.getInt(_idIdx);
                String dateTaken = deviceMediaCursor.getString(dateTakenIdx);
                DeviceMedia deviceMedia = new DeviceMedia(phoneId, dateTaken);
                tempDeviceMediaHM.put(phoneId, deviceMedia);
            }
            if((deviceMediaCursor != null)&&(!deviceMediaCursor.isClosed())) {
                deviceMediaCursor.close();
            }
            Log.i("DeviceMediaDAO","deviceMediaCounter " + deviceMediaCounter);
        }
        return tempDeviceMediaHM;
    }
}
