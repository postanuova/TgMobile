package org.teenguard.child.datatype;


import android.net.Uri;
import android.provider.MediaStore;

import org.teenguard.child.utils.JSon;

/**
 * Created by chris on 16/10/16.
 */

public class DeviceMedia {
    public static final Uri PHOTO_INTERNAL_CONTENT_URI = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
    public static final Uri PHOTO_EXTERNAL_CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    
    private int phoneId;
    private String dateTaken;
    private int mediaType;
    private int mediaDuration;
    private float latitude;
    private float longitude;
    private int accuracy;

    public DeviceMedia(int phoneId, String dateTaken) {
        this.phoneId = phoneId;
        this.dateTaken = dateTaken;

    }

    public String getMetadataJsonSTR() {
        JSon jSon = new JSon();
        jSon.add("id",phoneId);
        jSon.add("date", dateTaken);
        jSon.add("media_type",mediaType);
        jSon.add("media_duration",mediaDuration);
        jSon.add("latitude",latitude);
        jSon.add("longitude",longitude);
        jSon.add("accuracy",accuracy);
        return jSon.getJSonString();
    }

    public void dump() {
        System.out.println("-------------DEVICE MEDIA DUMP-------------");
        //  System.out.println("id = " + id);
        System.out.println("phoneId = " + phoneId);
        System.out.println("dateTaken = " + dateTaken);
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }

    public String getLookupKey() {
        return dateTaken;
    }

    public void setLookupKey(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getMediaDuration() {
        return mediaDuration;
    }

    public void setMediaDuration(int mediaDuration) {
        this.mediaDuration = mediaDuration;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }
}
