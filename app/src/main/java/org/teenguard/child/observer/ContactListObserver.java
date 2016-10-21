package org.teenguard.child.observer;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import org.teenguard.child.dao.DeviceContactDAO;
import org.teenguard.child.datatype.DeviceContact;
import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdao.DbContactDAO;
import org.teenguard.child.dbdao.DbContactEventDAO;
import org.teenguard.child.dbdatatype.DbContact;
import org.teenguard.child.dbdatatype.DbContactEvent;
import org.teenguard.child.utils.MyLog;
import org.teenguard.child.utils.ServerApiUtils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by chris on 04/10/16.
 */

public class ContactListObserver extends ContentObserver {

    DbContactDAO dbContactDAO = new DbContactDAO();
    DbContactEventDAO dbContactEventDAO = new DbContactEventDAO();
    ConcurrentHashMap<Integer,DeviceContact> deviceContactHM = new ConcurrentHashMap();
    ConcurrentHashMap<Integer,DbContact> dbContactHM = new ConcurrentHashMap();





    public ContactListObserver(Handler handler) {
        super(handler);
        dbContactHM = dbContactDAO.getDbContactHM();
        deviceContactHM = DeviceContactDAO.getDeviceContactHM();
        sendContactEventTableContent();
        if(dbContactHM.size() == 0) {
            MyLog.i(this,"dbHM =0 --> constructor empty DB: populate DB with user contact list");
            insertDeviceContactHMIntoDB();
        }
        MyLog.i(this,"invoking on change ContactObserver startup");
        onChange(false);
        MyLog.i(this,"flushing contact event table");
        flushContactEventTable();
    }


    public void flushContactEventTable() {
        DbContactEventDAO dbContactEventDAO = new DbContactEventDAO();
        ArrayList<DbContactEvent> dbContactEventAL = dbContactEventDAO.getList();
        StringBuilder addEventSB = new StringBuilder();
        StringBuilder updateEventSB = new StringBuilder();
        StringBuilder deleteEventSB = new StringBuilder();
        for (DbContactEvent dbContactEvent : dbContactEventAL) {
            dbContactEvent.dump();
            switch(dbContactEvent.getEventType()) {
                case DbContactEvent.CONTACT_EVENT_ADD: {
                    addEventSB.append(dbContactEvent.getSerializedData() + ",");
                    break;
                }
                case DbContactEvent.CONTACT_EVENT_MODIFY: {
                    updateEventSB.append(dbContactEvent.getSerializedData() + ",");
                    break;
                }
                case DbContactEvent.CONTACT_EVENT_DELETE: {
                    deleteEventSB.append("\"" + dbContactEvent.getCsId() + "\"" + ",");
                    break;
                }
            }
        }//fine for
        String addDataBulkSTR = addEventSB.toString();
        if(addDataBulkSTR.endsWith(",")) addDataBulkSTR = addDataBulkSTR.substring(0,addDataBulkSTR.length()-1);
        addDataBulkSTR = "[" + addDataBulkSTR + "]";
        String updateEventSTR = updateEventSB.toString();
        if(updateEventSTR.endsWith(",")) updateEventSTR = updateEventSTR.substring(0,updateEventSTR.length()-1);
        updateEventSTR = "[" + addDataBulkSTR + "]";
        String deleteDataBulkSTR = deleteEventSB.toString();
        if(deleteDataBulkSTR.endsWith(",")) deleteDataBulkSTR = deleteDataBulkSTR.substring(0,deleteDataBulkSTR.length()-1);
        deleteDataBulkSTR = "[" + deleteDataBulkSTR + "]";
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.i("ContactListObserver", "onChange Old: API < 16");
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        System.out.println("selfChange = " + selfChange);
        MyLog.i(this,"<<<< USER CONTACT LIST CHANGED >>>>");

//load user_contact_list
        dbContactHM = dbContactDAO.getDbContactHM();
        deviceContactHM = DeviceContactDAO.getDeviceContactHM();

        MyLog.i(this," deviceContactHM.size = " + deviceContactHM.size() + " dbContactHM.size = " + dbContactHM.size());

        if(dbContactHM.size() == 0) {
            MyLog.i(this,"dbHM =0 --> onChange empty DB: populate DB with user contact list");
            insertDeviceContactHMIntoDB();
        }

        if((dbContactHM.size() >0) && (deviceContactHM.size() == dbContactHM.size())) {
            MyLog.i(this,"HM sizes are the same: contact modified");
            manageContactModified();
        }

        if((dbContactHM.size() >0) && (deviceContactHM.size() > dbContactHM.size())) {
            MyLog.i(this,"userHM > dbHM : contact added");
            manageContactAdded();
        }
        

        if((dbContactHM.size() >0) && (deviceContactHM.size() < dbContactHM.size())) {
            MyLog.i(this,"userHM < dbHM : contact deleted");
            manageContactDeleted();
        }
    }

    private void sendContactEventTableContent() {
        Log.i("ContactListObserver.", "sendContactEventTableContent : CHECK ON contact_event: if contact_event.count > 0 SEND PREVIOUS CONTACT_EVENT TABLE CONTENT TO SERVER: NOT YET IMPLEMENTED");
    }

    private void manageContactAdded() {
        //per ogni deviceContactHM.phoneId (key) che non esiste in dbContactHM aggiungilo al db alla coda addedContactAL dei contatti da aggiungere
        int counter = 0;
        ArrayList <DeviceContact> addedDeviceContactAL = new ArrayList();
        ArrayList <DbContactEvent> dbAddContactEventAL = new ArrayList();
        for (int key : deviceContactHM.keySet()) {
            if(!dbContactHM.containsKey(key)) {
                MyLog.i(this,"manageContactAdded: added key (phoneId)= " + key);
                addedDeviceContactAL.add(deviceContactHM.get(key));
                counter ++;
            }
        }
        MyLog.i(this,"added contact counter= " + counter);
        DbContactDAO dbContactDAO = new DbContactDAO();
        for (DeviceContact deviceContact:addedDeviceContactAL) {//build dbContacts from deviceContact
            DbContact dbContact = new DbContact(0,deviceContact.getPhoneId(),deviceContact.getName(),deviceContact.getLastModified(),deviceContact.getNumbersJSonAR());
            dbContactDAO.beginTransaction(); //>>>>>>>>>>>>>>>>START TRANSACTION>>>>>>>>>>>>>>>>>>
            try {
                long contactId = dbContactDAO.upsert(dbContact);//insert into db.contact
                deviceContact.dump();
                MyLog.i(this, "inserted into contact _id: " + contactId);
                dbContact.setId(contactId);
                DbContactEvent dbContactEvent = new DbContactEvent(0, deviceContact.getPhoneId(), DbContactEvent.CONTACT_EVENT_ADD, dbContact.getJson().getJSonString());
                MyLog.i(this, "inserting into contact_event json " + dbContact.getJson().getJSonString());
                long contactEventId = dbContactEventDAO.upsert(dbContactEvent);
                dbContactEvent.setId(contactEventId);
                MyLog.i(this, "inserted into contact_event._id  " + dbContactEvent.getId());
                dbContactDAO.setTransactionSuccessful();    //>>>>>>>>>>>>>>>>COMMIT TRANSACTION>>>>>>>>>>>>>>>>>>
                dbContactDAO.endTransaction();              //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this,"SENDING NEW USER CONTACT TO SERVER");
                MyServerResponse myServerResponse = ServerApiUtils.addContactToServer(dbContactEvent);
                myServerResponse.dump();
                if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                    MyLog.i(this,"SENT NEW USER CONTACT TO SERVER");
                    dbContactEvent.deleteMe();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dbContactDAO.endTransaction();              //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void manageContactModified() {
        //per ogni dbContactHM.phoneId (key) controlla lastmodified:se è diverso aggiorna il db e aggiungilo alla coda modifiedContactAL dei contatti da rimuovere
        int counter = 0;
        ArrayList<DbContact> modifiedDbContactAL = new ArrayList();
        for (int key : dbContactHM.keySet()) {
            DbContact dbContact = dbContactHM.get(key);
            DeviceContact deviceContact = deviceContactHM.get(key);
            if (!(dbContact.getName().equalsIgnoreCase(deviceContact.getName())) ||
                    !(dbContact.getSerializedData().equalsIgnoreCase(deviceContact.getNumbersJSonAR()))) {
                MyLog.i(this, "modified key (phoneId)= " + key);
                dbContact.setName(deviceContact.getName());
                dbContact.setLastModified(deviceContact.getLastModified()); //ora il dbContact contiene lastUpdate aggiornato a quello di deviceContact
                dbContact.setSerializedData(deviceContact.getNumbersJSonAR());
                modifiedDbContactAL.add(dbContact);
                counter++;
            } else {
                //MyLog.i(this, "name and numbers are the same:no modify");
            }
        }
        MyLog.i(this, "modified contact counter= " + counter);
        for (DbContact dbContact : modifiedDbContactAL) {
            //DbContact dbContact = new DbContact(0,deviceContact.getPhoneId(),deviceContact.getName(),deviceContact.getLastModified(),deviceContact.getNumbersJSonAR());
            dbContactDAO.beginTransaction(); //>>>>>>>>>>>>>>>>START TRANSACTION>>>>>>>>>>>>>>>>>>
            try {
                long contactId = dbContactDAO.upsert(dbContact);//insert into db.contact
                dbContact.dump();
                MyLog.i(this, "updated into contact _id: " + contactId);

                DbContactEvent dbContactEvent = new DbContactEvent(0, dbContact.getPhoneId(), DbContactEvent.CONTACT_EVENT_MODIFY, dbContact.getJson().getJSonString());
                MyLog.i(this, "inserting into contact_event json " + dbContact.getJson().getJSonString());
                long contactEventId = dbContactEventDAO.upsert(dbContactEvent);
                dbContactEvent.setId(contactEventId);
                MyLog.i(this, "inserted into contact_event._id  " + dbContactEvent.getId());
                dbContactDAO.setTransactionSuccessful();    //>>>>>>>>>>>>>>>>COMMIT TRANSACTION>>>>>>>>>>>>>>>>>>
                dbContactDAO.endTransaction();              //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this,"SENDING UPDATED CONTACT TO SERVER");
                MyServerResponse myServerResponse = ServerApiUtils.updateContactIntoServer(dbContactEvent);
                myServerResponse.dump();
                if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                    MyLog.i(this,"SENT UPDATED CONTACT TO SERVER");
                    dbContactEvent.deleteMe();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dbContactDAO.endTransaction();              //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void manageContactDeleted() {
        //per ogni dbContactHM.phoneId (key) che non esiste in deviceContactHM rimuovilo dal db alla coda removedContactAL dei contatti da rimuovere
        int counter = 0;
        ArrayList <DbContact> removedDbContactAL = new ArrayList();
        for (int key : dbContactHM.keySet()) {
            if(!deviceContactHM.containsKey(key)) {
                MyLog.i(this,"removed key (phoneId)= " + key);
                removedDbContactAL.add(dbContactHM.get(key));
                counter ++;
            }
        }
        MyLog.i(this,"removed contact counter= " + counter);
        for (DbContact dbContact:removedDbContactAL) {
            dbContact.dump();
            dbContactDAO.beginTransaction(); //>>>>>>>>>>>>>>>>START TRANSACTION>>>>>>>>>>>>>>>>>>
            try {
                dbContactDAO.removeContact(dbContact);
                MyLog.i(this,"removed from db");
                DbContactEvent dbContactEvent = new DbContactEvent(0, dbContact.getPhoneId(), DbContactEvent.CONTACT_EVENT_DELETE, dbContact.getJson().getJSonString());
                MyLog.i(this, "inserting into contact_event json " + dbContact.getJson().getJSonString());
                long contactEventId = dbContactEventDAO.upsert(dbContactEvent);
                dbContactEvent.setId(contactEventId);
                MyLog.i(this, "inserted into contact_event._id  " + dbContactEvent.getId());
                dbContactDAO.setTransactionSuccessful();    //>>>>>>>>>>>>>>>>COMMIT TRANSACTION>>>>>>>>>>>>>>>>>>
                dbContactDAO.endTransaction();
                MyLog.i(this,"SEND REMOVED CONTACT TO SERVER");
                MyServerResponse myServerResponse = new MyServerResponse();
                myServerResponse = ServerApiUtils.deleteContactFromServer(String.valueOf(dbContactEvent.getCsId()));
                //VolleyConnectionUtils.doRequest(Request.Method.POST,"http://92.222.83.28/api.php","[" + dbContactEvent.getSerializedData() + "]");
                myServerResponse.dump();
                if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                    MyLog.i(this,"REMOVED CONTACT FROM SERVER");
                    dbContactEvent.deleteMe();
                    MyLog.i(this,"REMOVED CONTACT EVENT FROM DB");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dbContactDAO.endTransaction(); //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
            }
        }
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean insertDeviceContactHMIntoDB() {
        MyLog.i(this,"deviceContactHM" + deviceContactHM.size());
            //emptyContactTable();
            long nInserted = 0;
            for (DeviceContact deviceContact : deviceContactHM.values()) {
                MyLog.i(this,"inserting deviceContact = " + deviceContact.getName() + " phoneId = " + deviceContact.getPhoneId());
                DbContact dbContact = new DbContact(0,deviceContact.getPhoneId(),deviceContact.getName(),deviceContact.getLastModified(),deviceContact.getNumbersJSonAR());
                long idInserted = dbContactDAO.upsert(dbContact);
                System.out.println("_idInserted = " + idInserted);
                nInserted ++;
            }
            MyLog.i(this," Inserted " +  nInserted + " records into contact");
        return true;
    }








}