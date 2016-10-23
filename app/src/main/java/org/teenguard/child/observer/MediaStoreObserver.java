package org.teenguard.child.observer;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import org.teenguard.child.dao.DeviceMediaDAO;
import org.teenguard.child.datatype.DeviceMedia;
import org.teenguard.child.dbdao.DbMediaDAO;
import org.teenguard.child.dbdao.DbMediaEventDAO;
import org.teenguard.child.dbdatatype.DbMedia;
import org.teenguard.child.dbdatatype.DbMediaEvent;
import org.teenguard.child.utils.MyLog;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chris on 09/10/16.
 */

//http://stackoverflow.com/questions/22012274/contentobserver-onchange-method-gets-called-many-times
public class MediaStoreObserver extends ContentObserver {
    DbMediaDAO dbMediaDAO = new DbMediaDAO();
    DbMediaEventDAO dbMediaEventDAO = new DbMediaEventDAO();
    ConcurrentHashMap<Integer,DeviceMedia> deviceMediaHM = new ConcurrentHashMap();
    ConcurrentHashMap<Integer,DbMedia> dbMediaHM = new ConcurrentHashMap();

    public MediaStoreObserver(Handler handler) {
        super(handler);
        deviceMediaHM = DeviceMediaDAO.getDeviceMediaHM();
        dbMediaHM = dbMediaDAO.getDbMediaHM();
        if (dbMediaHM.size() == 0) {
            MyLog.i(this,"dbHM =0 --> constructor empty DB: populate DB with user contact list");
            insertDeviceMediaHMIntoDB();
        }
        MyLog.i(this,"invoking on change MediaObserver on startup");
        onChange(false);


    }

    @Override
    public void onChange(boolean selfChange) {
        Log.i("MediaStoreObserver", "onChange Old: API < 16");
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        MyLog.i(this,"<<<< USER MEDIA LIST CHANGED >>>>");
        super.onChange(selfChange);
//load user_media_list
        deviceMediaHM = DeviceMediaDAO.getDeviceMediaHM();
        dbMediaHM = dbMediaDAO.getDbMediaHM();
        MyLog.i(this,"deviceMediaHM.size = " + deviceMediaHM.size() + " dbMediaHM.size = " + dbMediaHM.size());

        if(dbMediaHM.size() == 0) {
            MyLog.i(this,"dbHM =0 -->  empty DB: populate DB with user media list");
            insertDeviceMediaHMIntoDB();
        }

        if((dbMediaHM.size() >0) && (deviceMediaHM.size() > dbMediaHM.size())) {
            MyLog.i(this,"deviceHM > dbHM : media added");
            manageMediaAdded();
        }


        if((dbMediaHM.size() >0) && (deviceMediaHM.size() < dbMediaHM.size())) {
            MyLog.i(this,"userHM < dbHM : media deleted");
            manageMediaDeleted();
        }
        }

    private void manageEmptyDB() {
        insertDeviceMediaHMIntoDB();
    }

    private void manageMediaAdded() {
        MyLog.i(this, "manageMediaAdded not completed");
        //per ogni deviceMediaHM.phoneId (key) che non esiste in dbMediaHM aggiungilo al db alla coda addedMediaAL dei contatti da aggiungere
        int counter = 0;
        ArrayList<DeviceMedia> addedDeviceMediaAL = new ArrayList();
        for (int key : deviceMediaHM.keySet()) {
            if (!dbMediaHM.containsKey(key)) {
                MyLog.i(this, "added key (phoneId)= " + key);
                addedDeviceMediaAL.add(deviceMediaHM.get(key));
                counter++;
            }
        }
        MyLog.i(this, "added media counter= " + counter);
        for (DeviceMedia deviceMedia : addedDeviceMediaAL) {//build dbMedias from deviceMedia
            DbMedia dbMedia = new DbMedia(0, deviceMedia.getPhoneId());
            long idInserted = dbMediaDAO.upsert(dbMedia);//is insert
            MyLog.i(this, "inserted into db _id: " + idInserted);
            dbMedia.setId(idInserted);
            DbMediaEvent dbMediaEvent = new DbMediaEvent(0, deviceMedia.getPhoneId(), DbMediaEvent.MEDIA_EVENT_ADD, deviceMedia.getMetadataJsonSTR());
            dbMediaDAO.beginTransaction();
            try {
                throw new UnsupportedOperationException("manageMediaAdded try block not implemented");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<finally in esecuzioneeeeeeeeeeee");
                if (dbMediaDAO.db.inTransaction()) {
                    dbMediaDAO.endTransaction(); //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                    System.out.println("closed transaction");
                }
                /*dbContactDAO.close();
                dbContactEventDAO.close();*/
            }
        }
    }


    private void manageMediaDeleted() {
        MyLog.i(this,"manageMediaDeleted not COMPLETED");
        //per ogni dbMediaHM.phoneId (key) che non esiste in deviceMediaHM rimuovilo dal db alla coda removedMediaAL dei contatti da rimuovere
        int counter = 0;
        ArrayList <DbMedia> removedDbMediaAL = new ArrayList();
        for (int key : dbMediaHM.keySet()) {
            if(!deviceMediaHM.containsKey(key)) {
                MyLog.i(this,"removed key (phoneId)= " + key);
                removedDbMediaAL.add(dbMediaHM.get(key));
                MyLog.i(this,"removed media: dbId=" + dbMediaHM.get(key).getId() + " phoneId" + dbMediaHM.get(key).getPhoneId());
                counter ++;
            }
        }
        MyLog.i(this,"removed media counter= " + counter);
        for (DbMedia dbMedia:removedDbMediaAL) {
            dbMedia.dump();
            dbMediaDAO.removeMedia(dbMedia);
            MyLog.i(this,"removed from db");
            MyLog.i(this,"SEND REMOVED MEDIA TO SERVER");
        }
    }

    private boolean insertDeviceMediaHMIntoDB() {
        MyLog.i(this,"deviceMediaHM" + deviceMediaHM.size());
        long nInserted = 0;
        for (DeviceMedia deviceMedia : deviceMediaHM.values()) {
            MyLog.i(this,"inserting deviceMedia = " + " phoneId = " + deviceMedia.getPhoneId());
            DbMedia dbMedia = new DbMedia(0,deviceMedia.getPhoneId());
            long idInserted = dbMediaDAO.upsert(dbMedia);
            System.out.println("_idInserted = " + idInserted);
            nInserted ++;
        }
        MyLog.i(this," Inserted " +  nInserted + " records into media");
        return true;
    }








}
