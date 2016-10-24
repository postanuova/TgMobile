package org.teenguard.child.dbdatatype;

import org.teenguard.child.utils.JSon;

/**
 * Created by chris on 16/10/16.
 */

public class DbMedia {
    private long id;
    private int phoneId;


    public DbMedia(int id, int phoneId) {
        this.setId(id);
        this.setPhoneId(phoneId);
    }

    public JSon getJson() {
        JSon jSon = new JSon();
        jSon.add("id", this.getId());
        jSon.add("phoneId", this.phoneId);
        return  jSon;
    }


    public void dump() {
        System.out.println("-------------DB MEDIA DUMP-------------");
        System.out.println("id = " + getId());
        System.out.println("phoneId = " + getPhoneId());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }
}
