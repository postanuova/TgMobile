package org.teenguard.child.datatype;


/**
 * Created by chris on 16/10/16.
 */

public class DeviceMedia {
    private int phoneId;
    private String lookupKey;

    public DeviceMedia(int phoneId, String lookupKey) {
        this.phoneId = phoneId;
        this.lookupKey = lookupKey;

    }

    public void dump() {
        System.out.println("-------------DEVICE CONTACT DUMP-------------");
        //  System.out.println("id = " + id);
        System.out.println("phoneId = " + phoneId);
        System.out.println("lookupKey = " + lookupKey);
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }
}
