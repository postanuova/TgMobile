package org.teenguard.child.observer;

import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import org.teenguard.child.dao.DeviceMediaDAO;
import org.teenguard.child.datatype.DeviceMedia;
import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdao.DbMediaDAO;
import org.teenguard.child.dbdao.DbMediaEventDAO;
import org.teenguard.child.dbdatatype.DbMedia;
import org.teenguard.child.dbdatatype.DbMediaEvent;
import org.teenguard.child.utils.Constant;
import org.teenguard.child.utils.ImageUtils;
import org.teenguard.child.utils.MyLog;
import org.teenguard.child.utils.ServerApiUtils;
import org.teenguard.child.utils.TypeConverter;

import java.io.File;
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
            MyLog.i(this,"dbHM =0 --> constructor empty DB: populate DB with user media list:BULK INSERT!");
            //insertDeviceMediaHMIntoDB();
            MyLog.i(this,"deviceMediaHM" + deviceMediaHM.size() + " inserting into media table: wait...");
            long nInserted = dbMediaDAO.bulkInsert(deviceMediaHM);
            MyLog.i(this," Inserted " +  nInserted + " records into media table");
        }
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
            //insertDeviceMediaHMIntoDB();
            dbMediaDAO.bulkInsert(deviceMediaHM);
        }

        if((dbMediaHM.size() >0) && (deviceMediaHM.size() > dbMediaHM.size())) {
            MyLog.i(this,"deviceHM > dbHM : media added");
            manageMediaAdded();
        }


        if((dbMediaHM.size() >0) && (deviceMediaHM.size() < dbMediaHM.size())) {
            MyLog.i(this," userHM < dbHM : media deleted");
            manageMediaDeleted();
        }
        // TODO: 26/10/16 riattivare il flush!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
        //flushMediaEventTable();
    }

    private void manageMediaAdded() {
        //per ogni deviceMediaHM.phoneId (key) che non esiste in dbMediaHM aggiungilo al db alla coda addedMediaAL dei contatti da aggiungere
        int counter = 0;
        ArrayList<DeviceMedia> addedDeviceMediaAL = new ArrayList();
        for (int key : deviceMediaHM.keySet()) {
            if (!dbMediaHM.containsKey(key)) {
                MyLog.i(this, " added key (phoneId)= " + key);
                addedDeviceMediaAL.add(deviceMediaHM.get(key));
                counter++;
            }
        }
        MyLog.i(this, "added media counter= " + counter);
        for (DeviceMedia deviceMedia : addedDeviceMediaAL) {//build dbMedias from deviceMedia
            DbMedia dbMedia = new DbMedia(0, deviceMedia.getPhoneId());
            dbMediaDAO.beginTransaction();
            try {
                long mediaId = dbMediaDAO.upsert(dbMedia);//is always insert
                MyLog.i(this, "inserted into media table _id: " + mediaId);
                dbMedia.setId(mediaId);//il dbMedia in ram e' allineato con quello del db
                DbMediaEvent dbMediaEvent = new DbMediaEvent(0, deviceMedia.getPhoneId(), DbMediaEvent.MEDIA_EVENT_ADD, deviceMedia.getMetadataJsonSTR(),deviceMedia.getPath(),"not-compressed");
                MyLog.i(this, "inserting into media_event json "  + dbMediaEvent.getSerializedData());
                long mediaEventId = dbMediaEventDAO.upsert(dbMediaEvent);
                dbMediaEvent.setId(mediaEventId);
                MyLog.i(this, "inserted into media_event._id  " + dbMediaEvent.getId());
                MyLog.i(this, "committing transaction");
                dbMediaDAO.setTransactionSuccessful();    //>>>>>>>>>>>>>>>>COMMIT TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this, "ending transaction");
                dbMediaDAO.endTransaction();              //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this,"SENDING NEW USER MEDIA(METADATA) TO SERVER");
                MyServerResponse myServerResponse = ServerApiUtils.addMediaMetadataToServer("[" + dbMediaEvent.getSerializedData() + "]");
                myServerResponse.dump();
                if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                    MyLog.i(this,"SENT NEW USER MEDIA(METADATA) TO SERVER");
                    dbMediaEvent.setEventType(DbMediaEvent.MEDIA_EVENT_SENT_METADATA_ONLY);
                    dbMediaEventDAO.upsert(dbMediaEvent);
                }

                //resize e compressione
               Bitmap bitmap = ImageUtils.getBitmapFromDataPath(dbMediaEvent.getPath());
                //Bitmap bitmap = ImageUtils.getBitmapFromDataPath("/storage/emulated/0/Download/nebula.jpg");
                System.out.println("original bitmap");
                ImageUtils.dump(bitmap);
                bitmap = ImageUtils.myScaleBitmap(bitmap, Constant.MAX_IMAGE_SIZE);
                System.out.println("scaled bitmap");
                ImageUtils.dump(bitmap);
                //File imageFile = ImageUtils.storeImage(bitmap);

                //System.out.println(" imageFile.getAbsolutePath() = " + imageFile.getAbsolutePath());

                bitmap = ImageUtils.compress(bitmap,35);
                System.out.println("compressed bitmap");
                ImageUtils.dump(bitmap);
                // TODO: 27/10/16 disattivare la scrittura su file
                File imageFile = ImageUtils.storeImage(bitmap);
                System.out.println(" imageFile.getAbsolutePath() = " + imageFile.getAbsolutePath());
                //scrivi path di compressione
                //aggiorna flag mediaEvent
                dbMediaEvent.setEventType(DbMediaEvent.MEDIA_EVENT_COMPRESSED);
                dbMediaEvent.setCompressedMediaPath(imageFile.getAbsolutePath());
                dbMediaEventDAO.upsert(dbMediaEvent);
                //building header

                //invio file raw
                myServerResponse = ServerApiUtils.addMediaMetadataAndMediaDataToServer(deviceMedia.getJSonRequestHeader(), TypeConverter.bitMapToBase64String(bitmap));
                bitmap.recycle();
                myServerResponse.dump();
                //se response ok cancella da mediaEvent
                if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                    MyLog.i(this,"SENT NEW USER MEDIA(METADATA + MEDIA) TO SERVER");
                    dbMediaEvent.setEventType(DbMediaEvent.MEDIA_EVENT_SENT_METADATA_AND_MEDIA);
                    dbMediaEventDAO.upsert(dbMediaEvent);
                    // TODO: 27/10/16  dbMediaEvent.deleteMe(); attivareeeeeeee
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("finally");
                if (dbMediaDAO.db.inTransaction()) {
                    dbMediaDAO.endTransaction(); //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                    System.out.println("closed transaction");
                }
              /* dbMediaDAO.close();
                dbMediaEventDAO.close();
                System.out.println("closed DAO");*/
            }
        }
    }




    private void manageMediaDeleted() {
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
            DeviceMedia deviceMedia = deviceMediaHM.get(dbMedia.getPhoneId());
            if(deviceMedia == null) {
                throw new NullPointerException("deviceMedia is null");
            }
            dbMediaDAO.beginTransaction();
            try {
                MyLog.i(this, "removing from media _id: " + dbMedia.getId());
                 dbMediaDAO.removeMedia(dbMedia);
                DbMediaEvent dbMediaEvent = new DbMediaEvent(0, dbMedia.getPhoneId(), DbMediaEvent.MEDIA_EVENT_DELETE, dbMedia.getJson().getJSonString(),deviceMedia.getPath(),"unknown");
                MyLog.i(this, "inserting into media_event json "  + dbMediaEvent.getSerializedData());
                long mediaEventId = dbMediaEventDAO.upsert(dbMediaEvent);
                dbMediaEvent.setId(mediaEventId);
                MyLog.i(this, "inserted into media_event._id  " + dbMediaEvent.getId());
                MyLog.i(this, "committing transaction");
                dbMediaDAO.setTransactionSuccessful();    //>>>>>>>>>>>>>>>>COMMIT TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this, "ending transaction");
                dbMediaDAO.endTransaction();              //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this,"SENDING REMOVED MEDIA TO SERVER");
                MyServerResponse myServerResponse = ServerApiUtils.deleteMediaFromServer(String.valueOf(dbMediaEvent.getCsId()));
                myServerResponse.dump();
                if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                    MyLog.i(this,"REMOVED MEDIA FROM SERVER");
                    dbMediaEvent.deleteMe();
                }
                /*flushMediaEventTable();*/
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("finally");
                if (dbMediaDAO.db.inTransaction()) {
                    dbMediaDAO.endTransaction(); //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                    System.out.println(" closed transaction");
                }
               /* dbMediaDAO.close();
                dbMediaEventDAO.close();
                System.out.println("closed DAO");*/
            }
        }
    }

    /**
     * transmit events to server and cleanup events table
     */
    public void flushMediaEventTable() {
        // TODO: 27/10/16 not completely implemented flushing : sends metadata, missing media
        MyLog.i(this, "FLUSHING contact event table");
        //DbContactEventDAO dbContactEventDAO = new DbContactEventDAO();
        ArrayList<DbMediaEvent> dbMediaEventAL = dbMediaEventDAO.getList();
        if(dbMediaEventAL.size() == 0 ) {
            MyLog.i(this, " no events to flush: return");
            return;
        }
        StringBuilder addEventSB = new StringBuilder();
        StringBuilder deleteEventSB = new StringBuilder();
        String addEventIdToRemoveList = "";         //lista degli eventi ADD da rimuovere dal db dopo l'ok del server
        String deleteEventIdToRemoveList = "";      //lista degli eventi DELETE da rimuovere dal db dopo l'ok del server

        for (DbMediaEvent dbMediaEvent : dbMediaEventAL) {
            dbMediaEvent.dump();
            switch (dbMediaEvent.getEventType()) {
                case DbMediaEvent.MEDIA_EVENT_ADD: {
                    addEventSB.append(dbMediaEvent.getSerializedData() + ",");
                    addEventIdToRemoveList += dbMediaEvent.getId() + ",";
                    break;
                }
                case DbMediaEvent.MEDIA_EVENT_DELETE: {
                    deleteEventSB.append("\"" + dbMediaEvent.getCsId() + "\"" + ",");
                    deleteEventIdToRemoveList += dbMediaEvent.getId() + ",";
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
            MyServerResponse myServerResponse = ServerApiUtils.addMediaMetadataToServer(addDataBulkSTR);
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                MyLog.i(this, " ADD BULK MEDIA SENT SUCCESFULLY TO SERVER: DELETING FROM DB");
                dbMediaEventDAO.delete(addEventIdToRemoveList);
                MyLog.i(this, "deleted from events list " + addEventIdToRemoveList);
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
            MyServerResponse myServerResponse = ServerApiUtils.deleteMediaFromServer(deleteDataBulkSTR);
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                MyLog.i(this, "DELETE BULK MEDIA SENT SUCCESFULLY TO SERVER: DELETING FROM DB");
                dbMediaEventDAO.delete(deleteEventIdToRemoveList);
                MyLog.i(this, "deleted from events list " + deleteEventIdToRemoveList);
            }
        }
    }

    /**DEPRECATED
     * used with empty db
     * @return
     */
    private boolean insertDeviceMediaHMIntoDB() {
        //// TODO: 25/10/16 bulk insert
        MyLog.i(this,"deviceMediaHM" + deviceMediaHM.size() + " inserting into media table: wait...");
        long nInserted = 0;
        for (DeviceMedia deviceMedia : deviceMediaHM.values()) {
            //MyLog.i(this,"inserting deviceMedia = " + " phoneId = " + deviceMedia.getPhoneId());
            DbMedia dbMedia = new DbMedia(0,deviceMedia.getPhoneId());
            long idInserted = dbMediaDAO.upsert(dbMedia);
            //System.out.println("_idInserted = " + idInserted);
            nInserted ++;
        }
        MyLog.i(this," Inserted " +  nInserted + " records into media table");
        /*flushMediaEventTable();//not tested*/
        return true;
    }

}
