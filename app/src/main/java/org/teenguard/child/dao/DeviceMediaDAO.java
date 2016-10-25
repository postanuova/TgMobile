package org.teenguard.child.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.teenguard.child.datatype.DeviceMedia;
import org.teenguard.child.utils.MyApp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
                MediaStore.Images.ImageColumns.DATA
        };
        //String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
        ContentResolver contentResolver = MyApp.getContext().getContentResolver();
        Cursor deviceMediaCursor = contentResolver.query(DeviceMedia.PHOTO_EXTERNAL_CONTENT_URI, projection, null, null, null);

        Log.i("DeviceMediaDAO", "getDeviceMediaHM : deviceMediaCursor columns " + deviceMediaCursor.getColumnCount() + " rows " + deviceMediaCursor.getCount());
        if (deviceMediaCursor != null) {
            String columnNamesAR[] = deviceMediaCursor.getColumnNames();
            for (int i = 0; i < columnNamesAR.length; i++) {
                Log.i("media column names", i + " " + columnNamesAR[i]);
            }
            // TODO: 25/10/16 solo ultimi tre mesi
            int _idIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
            int dateTakenIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
            int latitudeIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE);
            int longitudeIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE);
            int dataPathIdx = deviceMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            //// TODO: 25/10/16 mancano accuracy,
            int deviceMediaCounter = 0;
            while (deviceMediaCursor.moveToNext()) {
                deviceMediaCounter++;
                int phoneId = deviceMediaCursor.getInt(_idIdx);
                String dateTaken = deviceMediaCursor.getString(dateTakenIdx);
                float latitude = deviceMediaCursor.getFloat(latitudeIdx);
                float longitude = deviceMediaCursor.getFloat(longitudeIdx);
                float accuracy = 0;
                int mediaDuration = 0;
                int mediaType = DeviceMedia.MEDIA_TYPE_PHOTO;
                String dataPath = deviceMediaCursor.getString(dataPathIdx);
                System.out.println("dataPath = " + dataPath);
                if (dataPath.contains("chris")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(dataPath);
                    System.out.println("bitmap.getHeight() = " + bitmap.getHeight());
                    System.out.println("bitmap.getWidth() = " + bitmap.getWidth());
                    Uri uri = compressBitmap(MyApp.getContext(), bitmap);
                    System.out.println(">>>>>>>>>>>>>>>>>>> compressed uri " + uri.getEncodedPath());
                }
                String uriSTR = getImageUriFromPhoneId(phoneId).getEncodedPath();
                //public DeviceMedia(int phoneId, String dateTaken, int mediaType,int mediaDuration,float latitude, float longitude, float accuracy, String uri)
                DeviceMedia deviceMedia = new DeviceMedia(phoneId, dateTaken, mediaType, mediaDuration, latitude, longitude, accuracy, uriSTR);
                deviceMediaHM.put(phoneId, deviceMedia);
                deviceMedia.dump();
                //getImageBitmapFromURI(Uri.parse(uriSTR));
            }
            if ((deviceMediaCursor != null) && (!deviceMediaCursor.isClosed())) {
                deviceMediaCursor.close();
            }
            Log.i("DeviceMediaDAO", "deviceMediaCounter " + deviceMediaCounter);
        }
        return deviceMediaHM;
    }


    public static Uri getImageUriFromPhoneId(int phoneId) {
        Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Integer.toString(phoneId));
        return uri;
    }

    public static Bitmap getImageBitmapFromURI(Uri uri) {
        Bitmap bitmap = null;
        try {
            //bitmap = MediaStore.Images.Media.getBitmap(MyApp.getContext().getContentResolver(),uri);
            Bitmap bm = BitmapFactory.decodeStream(MyApp.getContext().getContentResolver().openInputStream(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<< bitmap uri = " + uri);
        //System.out.println("bitmap.getAllocationByteCount() = " + bitmap.getAllocationByteCount());
        System.out.println("bitmap.getHeight() = " + bitmap.getHeight());
        System.out.println("bitmap.getWidth() = " + bitmap.getWidth());

        return bitmap;
    }

    // TODO: 25/10/16  return bitmap.compress();
  /*public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/
    /*public static Bitmap getBitmapFromPhoneId(int phoneId) {
        final Resources resources = getResources();
        final Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resId))
                .appendPath(resources.getResourceTypeName(resId))
                .appendPath(resources.getResourceEntryName(resId))
                .build();
    }*/

    public static Uri compressBitmap(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        System.out.println("<<<<<<<<<<<<<<<<<<compressed height"  + inImage.getAllocationByteCount());
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "chris_compresso", null);
        return Uri.parse(path);

    }
}

  