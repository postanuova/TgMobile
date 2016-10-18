package org.teenguard.child.datatype;


import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by chris on 16/10/16.
 */

public class DeviceMedia {
    public static final Uri PHOTO_INTERNAL_CONTENT_URI = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
    public static final Uri PHOTO_EXTERNAL_CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    
    private int phoneId;
    private String dateTaken;

    public DeviceMedia(int phoneId, String dateTaken) {
        this.phoneId = phoneId;
        this.dateTaken = dateTaken;

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
}
