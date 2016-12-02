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

import static org.teenguard.child.utils.ServerApiUtils.addContactToServer;
import static org.teenguard.child.utils.ServerApiUtils.updateContactIntoServer;


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
        if(dbContactHM.size() == 0) {
            MyLog.i(this,"dbHM =0 --> constructor empty DB: populate DB with user contact list: BULK INSERT!!!!!!!!!!");
            //insertDeviceContactHMIntoDB();
            dbContactDAO.bulkInsert(deviceContactHM);
        }
       /* MyLog.i(this,"invoking on change ContactObserver on startup");
        onChange(false);*/


    }



    @Override
    public void onChange(boolean selfChange) {
        Log.i("ContactListObserver", " onChange Old: API < 16");
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
            //insertDeviceContactHMIntoDB();
            dbContactDAO.bulkInsert(deviceContactHM);
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
            MyLog.i(this," userHM < dbHM : contact deleted");
            manageContactDeleted();
        }
        flushContactEventTable();
    }



    private void manageContactAdded() {
        //per ogni deviceContactHM.phoneId (key) che non esiste in dbContactHM aggiungilo al db alla coda addedContactAL dei contatti da aggiungere
        int counter = 0;
        ArrayList <DeviceContact> addedDeviceContactAL = new ArrayList();
        for (int key : deviceContactHM.keySet()) {
            if(!dbContactHM.containsKey(key)) {
                MyLog.i(this,"manageContactAdded: added key (phoneId)= " + key);
                addedDeviceContactAL.add(deviceContactHM.get(key));
                counter ++;
            }
        }
        MyLog.i(this,"added contact counter= " + counter);
        //DbContactDAO dbContactDAO = new DbContactDAO();
        for (DeviceContact deviceContact:addedDeviceContactAL) {//build dbContacts from deviceContact
            DbContact dbContact = new DbContact(0,deviceContact.getPhoneId(),deviceContact.getName(),deviceContact.getLastModified(),deviceContact.getNumbersJSonAR());
            dbContactDAO.beginTransaction(); //>>>>>>>>>>>>>>>>START TRANSACTION>>>>>>>>>>>>>>>>>>
            try {
                long contactId = dbContactDAO.upsert(dbContact);//insert into db.contact
                deviceContact.dump();
                MyLog.i(this, "inserted into contact _id: " + contactId);
                dbContact.setId(contactId);
                DbContactEvent dbContactEvent = new DbContactEvent(0, dbContact.getId(), DbContactEvent.CONTACT_EVENT_ADD, dbContact.getJson().getJSonString());
                MyLog.i(this, "inserting into contact_event json " + dbContact.getJson().getJSonString());
                long contactEventId = dbContactEventDAO.upsert(dbContactEvent);
                dbContactEvent.setId(contactEventId);
                MyLog.i(this, "inserted into contact_event._id  " + dbContactEvent.getId());
                MyLog.i(this, "committing transaction");
                dbContactDAO.setTransactionSuccessful();    //>>>>>>>>>>>>>>>>COMMIT TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this, "ending transaction");
                dbContactDAO.endTransaction();              //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this,"SENDING NEW USER CONTACT TO SERVER");
                MyServerResponse myServerResponse = addContactToServer("[" + dbContactEvent.getSerializedData() + "]");
                myServerResponse.dump();
                if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                    MyLog.i(this,"SENT NEW USER CONTACT TO SERVER");
                    dbContactEvent.deleteMe();
                }
               /* flushContactEventTable();*/

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("finally");
                if(dbContactDAO.db.inTransaction()) {
                    dbContactDAO.endTransaction(); //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                    System.out.println("closed transaction");
                }
                /*dbContactDAO.close();
                dbContactEventDAO.close();
                System.out.println("closed DAO");*/
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

                DbContactEvent dbContactEvent = new DbContactEvent(0, dbContact.getId(), DbContactEvent.CONTACT_EVENT_MODIFY, dbContact.getJson().getJSonString());
                MyLog.i(this, "inserting into contact_event json " + dbContact.getJson().getJSonString());
                long contactEventId = dbContactEventDAO.upsert(dbContactEvent);
                dbContactEvent.setId(contactEventId);
                MyLog.i(this, "inserted into contact_event._id  " + dbContactEvent.getId());
                dbContactDAO.setTransactionSuccessful();    //>>>>>>>>>>>>>>>>COMMIT TRANSACTION>>>>>>>>>>>>>>>>>>
                dbContactDAO.endTransaction();              //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this,"SENDING UPDATED CONTACT TO SERVER");
                MyServerResponse myServerResponse = updateContactIntoServer("[" + dbContactEvent.getSerializedData() + "]");
                myServerResponse.dump();
                if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                    MyLog.i(this,"SENT UPDATED CONTACT TO SERVER");
                    dbContactEvent.deleteMe();
                }
                /*flushContactEventTable();*/
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<finally in esecuzioneeeeeeeeeeee");
                if(dbContactDAO.db.inTransaction()) {
                    dbContactDAO.endTransaction(); //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                    System.out.println("closed transaction");
                }
                /*dbContactDAO.close();
                dbContactEventDAO.close();
                System.out.println("closed DAO");*/
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
                DbContactEvent dbContactEvent = new DbContactEvent(0, dbContact.getId(), DbContactEvent.CONTACT_EVENT_DELETE, dbContact.getJson().getJSonString());
                MyLog.i(this, "inserting into contact_event json " + dbContact.getJson().getJSonString());
                long contactEventId = dbContactEventDAO.upsert(dbContactEvent);
                dbContactEvent.setId(contactEventId);
                MyLog.i(this, "inserted into contact_event._id  " + dbContactEvent.getId());
                MyLog.i(this, "committing transaction");
                dbContactDAO.setTransactionSuccessful();    //>>>>>>>>>>>>>>>>COMMIT TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this, "ending transaction");
                dbContactDAO.endTransaction();
                MyLog.i(this," SEND REMOVED CONTACT TO SERVER");
                MyServerResponse myServerResponse = new MyServerResponse();
                myServerResponse = ServerApiUtils.deleteContactFromServer(String.valueOf(dbContactEvent.getCsId()));//csId contiene contact._id
                //VolleyConnectionUtils.doRequest(Request.Method.POST,"http://92.222.83.28/api.php","[" + dbContactEvent.getSerializedData() + "]");
                myServerResponse.dump();
                if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                    MyLog.i(this,"REMOVED CONTACT FROM SERVER");
                    dbContactEvent.deleteMe();
                    MyLog.i(this,"REMOVED CONTACT EVENT FROM DB");
                }
                /*flushContactEventTable();
                System.out.println("flushed");*/
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<finally in esecuzioneeeeeeeeeeee");
                if(dbContactDAO.db.inTransaction()) {
                    System.out.println("finally closing transaction");
                    dbContactDAO.endTransaction(); //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                    System.out.println("finally closed transaction");
                }
               /*dbContactDAO.close();
                dbContactEventDAO.close();
                System.out.println("closed DAO");*/
            }
        }
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean insertDeviceContactHMIntoDB() {
        // TODO: 25/10/16 bulk insert 
        MyLog.i(this,"deviceContactHM" + deviceContactHM.size() + " inserting into contact table: wait...");
            //emptyContactTable();
            long nInserted = 0;
            for (DeviceContact deviceContact : deviceContactHM.values()) {
                //MyLog.i(this,"inserting deviceContact = " + deviceContact.getName() + " phoneId = " + deviceContact.getPhoneId());
                DbContact dbContact = new DbContact(0,deviceContact.getPhoneId(),deviceContact.getName(),deviceContact.getLastModified(),deviceContact.getNumbersJSonAR());
                long idInserted = dbContactDAO.upsert(dbContact);
                //System.out.println("_idInserted = " + idInserted);
                nInserted ++;
            }
            MyLog.i(this,"Inserted " +  nInserted + " records into contact table");
        return true;
    }




    /**
     * transmit events to server and cleanup events table
     */
    public static void flushContactEventTable() {
        System.out.println("FLUSHING contact event table");
        DbContactEventDAO dbContactEventDAO = new DbContactEventDAO();
        ArrayList<DbContactEvent> dbContactEventAL = dbContactEventDAO.getList();
        if(dbContactEventAL.size() == 0 ) {
            System.out.println("no contacts events to flush: return");
            return;
        }
        StringBuilder addEventSB = new StringBuilder();
        StringBuilder updateEventSB = new StringBuilder();
        StringBuilder deleteEventSB = new StringBuilder();
        String addEventIdToRemoveList = "";         //lista degli eventi ADD da rimuovere dal db dopo l'ok del server
        String updateEventIdToRemoveList = "";      //lista degli eventi UPDATE da rimuovere dal db dopo l'ok del server
        String deleteEventIdToRemoveList = "";      //lista degli eventi DELETE da rimuovere dal db dopo l'ok del server

        for (DbContactEvent dbContactEvent : dbContactEventAL) {
            dbContactEvent.dump();
            switch (dbContactEvent.getEventType()) {
                case DbContactEvent.CONTACT_EVENT_ADD: {
                    addEventSB.append(dbContactEvent.getSerializedData() + ",");
                    addEventIdToRemoveList += dbContactEvent.getId() + ",";
                    break;
                }
                case DbContactEvent.CONTACT_EVENT_MODIFY: {
                    updateEventSB.append(dbContactEvent.getSerializedData() + ",");
                    updateEventIdToRemoveList += dbContactEvent.getId() + ",";
                    break;
                }
                case DbContactEvent.CONTACT_EVENT_DELETE: {
                    deleteEventSB.append("\"" + dbContactEvent.getCsId() + "\"" + ",");
                    deleteEventIdToRemoveList += dbContactEvent.getId() + ",";
                    break;
                }
            }
        }//fine for

        /////add
        String addDataBulkSTR = addEventSB.toString();
        if (addDataBulkSTR.length() > 0) {//ci sono eventi add
            if (addDataBulkSTR.endsWith(",")) {
                addDataBulkSTR = addDataBulkSTR.substring(0, addDataBulkSTR.length() - 1);
            }
            if (addEventIdToRemoveList.endsWith(",")) {
                addEventIdToRemoveList = addEventIdToRemoveList.substring(0, addEventIdToRemoveList.length() - 1);
            }
            System.out.println("addDataBulkSTR = " + addDataBulkSTR);
            addDataBulkSTR = "[" + addDataBulkSTR + "]";
                MyServerResponse myServerResponse = ServerApiUtils.addContactToServer(addDataBulkSTR);

            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                System.out.println("ADD BULK CONTACT SENT SUCCESFULLY TO SERVER: DELETING FROM DB");
                dbContactEventDAO.delete(addEventIdToRemoveList);
                System.out.println("deleted from events list " + addEventIdToRemoveList);
            }
        }
        /////update
        String updateEventSTR = updateEventSB.toString();
        if (updateEventSTR.length() > 0) { //ci sono eventi update
            if (updateEventSTR.endsWith(",")) {
                updateEventSTR = updateEventSTR.substring(0, updateEventSTR.length() - 1);
            }
            if (updateEventIdToRemoveList.endsWith(",")) {
                updateEventIdToRemoveList = updateEventIdToRemoveList.substring(0, updateEventIdToRemoveList.length() - 1);
            }
            System.out.println("updateEventSTR = " + updateEventSTR);
            updateEventSTR = "[" + updateEventSTR + "]";
            MyServerResponse myServerResponse = ServerApiUtils.updateContactIntoServer(updateEventSTR);
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                System.out.println("UPDATE BULK CONTACT SENT SUCCESFULLY TO SERVER: DELETING FROM DB");
                dbContactEventDAO.delete(updateEventIdToRemoveList);
                System.out.println("deleted from events list " + updateEventIdToRemoveList);
            }
        }
        /////delete
        String deleteDataBulkSTR = deleteEventSB.toString();
        if (deleteDataBulkSTR.length() > 0) { //ci sono eventi delete
            if (deleteDataBulkSTR.endsWith(",")) {
                deleteDataBulkSTR = deleteDataBulkSTR.substring(0, deleteDataBulkSTR.length() - 1);
            }
            if (deleteEventIdToRemoveList.endsWith(",")) {
                deleteEventIdToRemoveList = deleteEventIdToRemoveList.substring(0, deleteEventIdToRemoveList.length() - 1);
            }
            System.out.println("deleteDataBulkSTR = " + deleteDataBulkSTR);
            deleteDataBulkSTR = "[" + deleteDataBulkSTR + "]";
            MyServerResponse myServerResponse = ServerApiUtils.deleteContactFromServer(deleteDataBulkSTR);
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                System.out.println("DELETE BULK CONTACT SENT SUCCESFULLY TO SERVER: DELETING FROM DB");
                dbContactEventDAO.delete(deleteEventIdToRemoveList);
                System.out.println("deleted from events list " + deleteEventIdToRemoveList);
            }
        }

    }
}