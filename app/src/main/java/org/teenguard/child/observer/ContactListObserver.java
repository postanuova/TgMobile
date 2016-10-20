package org.teenguard.child.observer;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import org.teenguard.child.dao.DeviceContactDAO;
import org.teenguard.child.datatype.DeviceContact;
import org.teenguard.child.dbdao.DbContactDAO;
import org.teenguard.child.dbdao.DbContactEventDAO;
import org.teenguard.child.dbdatatype.DbContact;
import org.teenguard.child.dbdatatype.DbContactEvent;
import org.teenguard.child.utils.JSon;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.MyConnectionUtils;
import org.teenguard.child.utils.MyLog;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by chris on 04/10/16.
 */

public class ContactListObserver extends ContentObserver {

    DbContactDAO dbContactDAO = new DbContactDAO(MyApp.getContext());
    DbContactEventDAO dbContactEventDAO = new DbContactEventDAO(MyApp.getContext());
    ConcurrentHashMap<Integer,DeviceContact> deviceContactHM = new ConcurrentHashMap();
    ConcurrentHashMap<Integer,DbContact> dbContactHM = new ConcurrentHashMap();


    public ContactListObserver(Handler handler) {
        super(handler);
        dbContactHM = dbContactDAO.getDbContactHM();
        deviceContactHM = DeviceContactDAO.getDeviceContactHM();
        manageContactEventTable();
        if(dbContactHM.size() == 0) {
            MyLog.i(this,"dbHM =0 --> constructor empty DB: populate DB with user contact list");
            insertDeviceContactHMIntoDB();
        }

    }

    @Override
    public void onChange(boolean selfChange) {
        Log.i("ContactListObserver", "onChange Old: API < 16");
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
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

    private void manageContactEventTable() {
        Log.i("ContactListObserver.", "manageContactEventTable : CHECK ON contact_event: if contact_event.count > 0 SEND PREVIOUS CONTACT_EVENT TABLE CONTENT TO SERVER: NOT YET IMPLEMENTED ");
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
        DbContactDAO dbContactDAO = new DbContactDAO(MyApp.getContext());
        for (DeviceContact deviceContact:addedDeviceContactAL) {//build dbContacts from deviceContact
            DbContact dbContact = new DbContact(0,deviceContact.getPhoneId(),deviceContact.getName(),deviceContact.getLastModified(),deviceContact.buildSerializedDataString());
            dbContactDAO.beginTransaction(); //>>>>>>>>>>>>>>>>START TRANSACTION>>>>>>>>>>>>>>>>>>
            try {
                long contactId = dbContactDAO.upsert(dbContact);//insert into db.contact
                deviceContact.dump();
                MyLog.i(this, "inserted into contact _id: " + contactId);
                JSon jSon = new JSon();
                jSon.add("id", contactId);
                jSon.add("date", dbContact.getLastModified());
                jSon.add("first_name", dbContact.getName());
                jSon.add("last_name", "");
                jSon.addArray("phone_numbers", "[" + dbContact.getSerializedData() + "]");
                DbContactEvent dbContactEvent = new DbContactEvent(0, deviceContact.getPhoneId(), DbContactEvent.CONTACT_EVENT_ADD, jSon.getJSonString());
                MyLog.i(this, "inserting into contact_event json " + jSon.getJSonString());
                long contactEventId = dbContactEventDAO.upsert(dbContactEvent);
                dbContactEvent.setId(contactEventId);
                MyLog.i(this, "inserted into contact_event._id  " + dbContactEvent.getId());
                dbContactDAO.setTransactionSuccessful();    //>>>>>>>>>>>>>>>>COMMIT TRANSACTION>>>>>>>>>>>>>>>>>>

                ///////////////////////////////////////////////
                MyLog.i(this,"SENDING NEW USER CONTACT TO SERVER");
                //VolleyServerUtils.sendNewContactEventToServer(dbContactEvent);
                //MyConnectionUtils.doApachePost(dbContactEvent.getSerializedData());
                MyConnectionUtils.doAndroidPost(dbContactEvent.getSerializedData());

                //MyLog.i(this,"SENT NEW USER CONTACT TO SERVER");
                //////////////////////////////////////////////
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dbContactDAO.endTransaction(); //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
            }



        }
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void manageContactDeleted() {
        MyLog.i(this,"manageContactDeleted not COMPLETED");
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
            dbContactDAO.removeContact(dbContact);
            MyLog.i(this,"removed from db");
            MyLog.i(this,"SEND REMOVED CONTACT TO SERVER");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void manageContactModified() {
        MyLog.i(this, "manageContactModified not COMPLETED");
        //per ogni dbContactHM.phoneId (key) controlla lastmodified:se è diverso aggiorna il db e aggiungilo alla coda modifiedContactAL dei contatti da rimuovere
        int counter = 0;
        ArrayList<DbContact> modifiedDbContactAL = new ArrayList();
        for (int key : dbContactHM.keySet()) {
            DbContact dbContact = dbContactHM.get(key);
            DeviceContact deviceContact = deviceContactHM.get(key);
            if (!(dbContact.getName().equalsIgnoreCase(deviceContact.getName())) ||
                    !(dbContact.getSerializedData().equalsIgnoreCase(deviceContact.buildSerializedDataString()))) {
                MyLog.i(this, "modified key (phoneId)= " + key);
                dbContact.setName(deviceContact.getName());
                dbContact.setLastModified(deviceContact.getLastModified()); //ora il dbContact contiene lastUpdate aggiornato a quello di deviceContact
                dbContact.setSerializedData(deviceContact.buildSerializedDataString());
                modifiedDbContactAL.add(dbContact);
                counter++;
            } else {
                //MyLog.i(this, "name and numbers are the same:no modify");
            }
        }
        MyLog.i(this, "modified contact counter= " + counter);
        for (DbContact dbContact : modifiedDbContactAL) {
            dbContact.dump();
            dbContactDAO.upsert(dbContact);
            MyLog.i(this, "modified contact into db");
            MyLog.i(this, "SEND MODIFIED CONTACT TO SERVER");
        }

    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean insertDeviceContactHMIntoDB() {
        MyLog.i(this,"deviceContactHM" + deviceContactHM.size());
            //emptyContactTable();
            long nInserted = 0;
            for (DeviceContact deviceContact : deviceContactHM.values()) {
                MyLog.i(this,"inserting deviceContact = " + deviceContact.getName() + " phoneId = " + deviceContact.getPhoneId());
                DbContact dbContact = new DbContact(0,deviceContact.getPhoneId(),deviceContact.getName(),deviceContact.getLastModified(),deviceContact.buildSerializedDataString());
                long idInserted = dbContactDAO.upsert(dbContact);
                System.out.println("_idInserted = " + idInserted);
                nInserted ++;
            }
            MyLog.i(this," Inserted " +  nInserted + " records into contact");
        return true;
    }








}