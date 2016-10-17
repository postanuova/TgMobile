package org.teenguard.child.dbdatatype;

/**
 * Created by chris on 16/10/16.
 */

public class DbMedia {
    private int id;
    private int phoneId;


    public DbMedia(int id, int phoneId) {
        this.setId(id);
        this.setPhoneId(phoneId);
    }

    public void dump() {
        System.out.println("-------------DB MEDIA DUMP-------------");
        System.out.println("id = " + getId());
        System.out.println("phoneId = " + getPhoneId());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }
}
