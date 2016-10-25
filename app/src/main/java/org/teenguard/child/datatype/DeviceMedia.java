package org.teenguard.child.datatype;


import android.net.Uri;
import android.provider.MediaStore;

import org.teenguard.child.utils.JSon;

import static android.R.attr.id;

/**
 * Created by chris on 16/10/16.
 */

public class DeviceMedia {
    public static final Uri PHOTO_INTERNAL_CONTENT_URI = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
    public static final Uri PHOTO_EXTERNAL_CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public static final int MEDIA_TYPE_PHOTO = 0;
    public static final int MEDIA_TYPE_VIDEO = 1;
    public static final int MEDIA_TYPE_AUDIO = 2;

    
    private int phoneId;
    private String dateTaken;
    private int mediaType;
    private int mediaDuration;
    private float latitude;
    private float longitude;
    private float accuracy;
    private String uri;

    public DeviceMedia(int phoneId, String dateTaken, int mediaType,int mediaDuration,float latitude, float longitude, float accuracy, String uri) {
        this.phoneId = phoneId;
        this.dateTaken = dateTaken;
        this.mediaType = mediaType;
        this.mediaDuration = mediaDuration;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.uri = uri;

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
        System.out.println("id = " + id);
        System.out.println("phoneId = " + phoneId);
        System.out.println("dateTaken = " + dateTaken);
        System.out.println("phoneId = " + phoneId);
        System.out.println("dateTaken = " + dateTaken);
        System.out.println("mediaType = " + mediaType);
        System.out.println("mediaDuration = " + mediaDuration);
        System.out.println("latitude = " + latitude);
        System.out.println("longitude = " + longitude);
        System.out.println("accuracy = " + accuracy);
        System.out.println("uri = " + uri);
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

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
