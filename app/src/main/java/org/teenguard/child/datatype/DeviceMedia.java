package org.teenguard.child.datatype;


import android.net.Uri;
import android.provider.MediaStore;

import org.json.JSONException;
import org.json.JSONObject;
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
    private long dateTaken;
    private int mediaType;
    private int mediaDuration;
    private float latitude;
    private float longitude;
    private float accuracy;
    private String displayName;
    private String uri;
    private String path;

    public DeviceMedia(int phoneId, long dateTaken, int mediaType,int mediaDuration,float latitude, float longitude, float accuracy, String uri, String path, String displayName) {
        this.setPhoneId(phoneId);
        this.setDateTaken(dateTaken);
        this.setMediaType(mediaType);
        this.setMediaDuration(mediaDuration);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setAccuracy(accuracy);
        this.setUri(uri);
        this.setPath(path);
        this.displayName = displayName;
    }

public DeviceMedia(String serializedData) {

}

    /**
     *
     * @return a String to put into serialized_data of media_event.serialized_data table;
     */
    public String getMetadataJsonSTR() {
        JSon jSon = new JSon();
        jSon.add("id", getPhoneId());
        jSon.add("date", getDateTaken());
        jSon.add("media_type", getMediaType());
        jSon.add("media_duration", getMediaDuration());
        jSon.add("latitude", getLatitude());
        jSon.add("longitude", getLongitude());
        jSon.add("accuracy", getAccuracy());
        return jSon.getJSonString();
    }

    /**
     *
     * @return a JSon object used to populate header of a request which sends matadata end raw image to server
     */
    public JSONObject getJSonRequestHeader() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", getPhoneId());
            json.put("date", getDateTaken());
            json.put("media_type", getMediaType());
            json.put("media_duration", getMediaDuration());
            json.put("latitude", getLatitude());
            json.put("longitude", getLongitude());
            json.put("accuracy", getAccuracy());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }




    public void dump() {
        System.out.println("-------------DEVICE MEDIA DUMP-------------");
        System.out.println("id = " + id);
        System.out.println("phoneId = " + getPhoneId());
        System.out.println("dateTaken = " + getDateTaken());
        System.out.println("mediaType = " + getMediaType());
        System.out.println("mediaDuration = " + getMediaDuration());
        System.out.println("latitude = " + getLatitude());
        System.out.println("longitude = " + getLongitude());
        System.out.println("accuracy = " + getAccuracy());
        System.out.println("uri = " + getUri());
        System.out.println("path = " + getPath());
        System.out.println("displayName = " + displayName);
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
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

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
