package org.teenguard.child.dao;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import org.teenguard.child.datatype.DeviceMedia;
import org.teenguard.child.utils.Constant;
import org.teenguard.child.utils.ImageUtils;
import org.teenguard.child.utils.MyApp;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chris on 16/10/16.
 */

public class DeviceMediaDAO {

    public static ConcurrentHashMap<Integer, DeviceMedia> getDeviceMediaHM() {
        ConcurrentHashMap deviceMediaHM = new ConcurrentHashMap();
        String[] projection = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DISPLAY_NAME
        };

        ////////1466808913000
        long maxMillisAgo = System.currentTimeMillis() - Constant.MAX_DAYS_AGO_MILLIS;
        System.out.println("maxMillisAgo = " + maxMillisAgo);
        String selection = "(" + MediaStore.Images.ImageColumns.DATE_TAKEN + ">" + maxMillisAgo + ")";
        System.out.println("selection = " + selection);
        ////////

        //String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
        ContentResolver contentResolver = MyApp.getInstance().getApplicationContext().getContentResolver();
        Cursor deviceMediaCursor = contentResolver.query(DeviceMedia.PHOTO_EXTERNAL_CONTENT_URI, projection, selection, null, null);

       // Log.i("DeviceMediaDAO", "getDeviceMediaHM : deviceMediaCursor columns " + deviceMediaCursor.getColumnCount() + " rows " + deviceMediaCursor.getCount());
        if (deviceMediaCursor != null) {
            String columnNamesAR[] = deviceMediaCursor.getColumnNames();
            /*for (int i = 0; i < columnNamesAR.length; i++) {
                Log.i("media column names", i + " " + columnNamesAR[i]);
            }*/
            int _idIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
            int dateTakenIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
            int latitudeIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE);
            int longitudeIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE);
            int dataPathIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            int displayNameIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
            //// TODO: 25/10/16 manca accuracy che risulta sempre a zero
            int deviceMediaCounter = 0;
            while (deviceMediaCursor.moveToNext()) {
                deviceMediaCounter++;
                int phoneId = deviceMediaCursor.getInt(_idIdx);
                long dateTaken = deviceMediaCursor.getLong(dateTakenIdx);
                float latitude = deviceMediaCursor.getFloat(latitudeIdx);
                float longitude = deviceMediaCursor.getFloat(longitudeIdx);
                float accuracy = 0;
                int mediaDuration = 0;
                int mediaType = DeviceMedia.MEDIA_TYPE_PHOTO;
                String dataPath = deviceMediaCursor.getString(dataPathIdx);
                String displayName = deviceMediaCursor.getString(displayNameIdx);
               /* System.out.println("dataPath = " + dataPath);
                System.out.println("displayName = " + displayName);*/
                String uriSTR = ImageUtils.uriFromFilename(displayName).getEncodedPath();
                DeviceMedia deviceMedia = new DeviceMedia(phoneId, dateTaken, mediaType, mediaDuration, latitude, longitude, accuracy, uriSTR,dataPath,displayName);
                deviceMediaHM.put(phoneId, deviceMedia);
                deviceMedia.dump();
                /*if (dataPath.contains("PHOTO_20161026")) {
                    Uri uri = ImageUtils.getUriFromPhoneId(phoneId);
                    System.out.println(">>>>>>>>>uri.getEncodedPath() = " + uri.getEncodedPath());
                    Bitmap bitmap = BitmapFactory.decodeFile(dataPath);
                    System.out.println("---original---");
                    ImageUtils.dump(bitmap);
                    System.out.println(" ---resized---");
                    bitmap = ImageUtils.scaleBitmap(bitmap,960);
                    ImageUtils.dump(bitmap);
                    *//*System.out.println("---compressed---");
                    bitmap = ImageUtils.compress(MyApp.getInstance().getApplicationContext(),bitmap);
                    ImageUtils.dump(bitmap);*//*
                    System.out.println("---saving---");
                    ImageUtils.storeImage(bitmap);
                    System.out.println("- ------------------");
                }*/

                // public DeviceMedia(int phoneId, long dateTaken, int mediaType,int mediaDuration,float latitude, float longitude, float accuracy, String uri, String path) {

            }
            if ((deviceMediaCursor != null) && (!deviceMediaCursor.isClosed())) {
                deviceMediaCursor.close();
            }
            Log.i("DeviceMediaDAO", "deviceMediaCounter " + deviceMediaCounter);
        }
        return deviceMediaHM;
    }



}

  