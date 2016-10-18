package org.teenguard.child.dbdatatype;

/**
 * Created by chris on 18/10/16.
 */

public class DbMediaEvent {
    public final static int MEDIA_EVENT_ADD = 0;
    public final static int MEDIA_EVENT_DELETE = 1;
    public final static int MEDIA_EVENT_COMPRESSED = 2;

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

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getSerializedData() {
        return serializedData;
    }

    public void setSerializedData(String serializedData) {
        this.serializedData = serializedData;
    }
}

