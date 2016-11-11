package org.teenguard.child.dbdatatype;

import org.teenguard.child.dbdao.DbGeofenceEventDAO;

/**
 * Created by chris on 09/11/16.
 */

public class DbGeofenceEvent implements InterfaceDbDatatype {
    public static final int DB_GEOFENCE_EVENT_ENTER = 0;
    public static final int DB_GEOFENCE_EVENT_LEAVE = 1;
    private long id;
    private String geofenceId;
    private long date;
    private int event;

    public DbGeofenceEvent(int id, String geofenceId, long date, int event) {
        this.id = id;
        this.geofenceId = geofenceId;
        this.date = date;
        this.event = event;
    }

    @Override
    public void dump() {
        System.out.println("---------- DB GEOFENCE EVENT DUMP ----------");
        System.out.println("id = " + id);
        System.out.println("geofenceId = " + geofenceId);
        System.out.println("date = " + date);
        System.out.println("event = " + event);
    }

    @Override
    public void deleteMe() {
        DbGeofenceEventDAO objectDAO = new DbGeofenceEventDAO();
        objectDAO.delete(""+this.getId());
    }

    @Override
    public long writeMe() {
        DbGeofenceEventDAO objectDAO = new DbGeofenceEventDAO();
        return objectDAO.upsert(this);
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(String geofenceId) {
        this.geofenceId = geofenceId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }
}
