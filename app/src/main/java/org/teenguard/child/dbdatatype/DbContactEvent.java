package org.teenguard.child.dbdatatype;

import org.teenguard.child.dbdao.DbContactEventDAO;

/**
 * Created by chris on 18/10/16.
 */

public class DbContactEvent {
    public final static int CONTACT_EVENT_ADD = 0;
    public final static int CONTACT_EVENT_MODIFY = 1;
    public final static int CONTACT_EVENT_DELETE = 2;

    private long id; //autoincrement
    private long csId; //is contact._id
    private int eventType; //add, modofy, delete
    private String serializedData; //json data

    public DbContactEvent(long id, long csId,int eventType, String serializedData) {
        this.id = id;//autoincrement
        this.csId = csId;//contact.contact_id
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

    public void deleteMe() {
        DbContactEventDAO dbContactEventDAO = new DbContactEventDAO();
        dbContactEventDAO.delete(this);
    }

    public void dump() {
        System.out.println("-------------DB CONTACT EVENT DUMP-------------");
        System.out.println("id = " + id);
        System.out.println("csId = " + csId);
        System.out.println("eventType = " + eventType);
        System.out.println("serializedData = " + serializedData);
    }
}
