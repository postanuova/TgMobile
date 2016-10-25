package org.teenguard.child.dbdatatype;

import org.teenguard.child.dbdao.DbMediaEventDAO;

/**
 * Created by chris on 18/10/16.
 */

public class DbMediaEvent {
    public final static int MEDIA_EVENT_ADD = 0;
    public final static int MEDIA_EVENT_DELETE = 1;
    public final static int MEDIA_EVENT_COMPRESSED = 2;

    private long id; //autoincrement
    private long csId; //is the phone_id
    private int eventType; //add, modify, delete
    private String serializedData; //json data che verranno inviati al server

    public DbMediaEvent(long id, int csId, int eventType, String serializedData) {
        this.id = id;
        this.csId = csId;//media._id
        this.eventType = eventType;
        this.serializedData = serializedData;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCsId() {
        return csId;
    }

    public void setCsId(long csId) {
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

    public void deleteMe() {
        DbMediaEventDAO dbMediaEventDAO = new DbMediaEventDAO();
        dbMediaEventDAO.delete(this);
    }

    public void dump() {
        System.out.println("-------------DB MEDIA EVENT DUMP-------------");
        System.out.println("id = " + id);
        System.out.println("csId = " + csId);
        System.out.println("eventType = " + eventType);
        System.out.println("serializedData = " + serializedData);
    }

}

