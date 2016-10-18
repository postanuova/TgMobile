package org.teenguard.child.dbdatatype;

/**
 * Created by chris on 18/10/16.
 */

public class DbContactEvent {
    public final static int CONTACT_EVENT_ADD = 0;
    public final static int CONTACT_EVENT_MODIFY = 1;
    public final static int CONTACT_EVENT_DELETE = 2;

    private int id; //autoincrement
    private int csId; //is the phone_id
    private int eventType; //add, modofy, delete
    private String serializedData; //json data

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCsId() {
        return csId;
    }

    public void setCsId(int csId) {
        this.csId = csId;
    }



    public String getSerializedData() {
        return serializedData;
    }

    public void setSerializedData(String serializedData) {
        this.serializedData = serializedData;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
