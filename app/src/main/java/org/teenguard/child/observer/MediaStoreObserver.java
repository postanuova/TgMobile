package org.teenguard.child.observer;

import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.teenguard.child.dao.DeviceMediaDAO;
import org.teenguard.child.datatype.DeviceMedia;
import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdao.DbMediaDAO;
import org.teenguard.child.dbdao.DbMediaEventDAO;
import org.teenguard.child.dbdatatype.DbMedia;
import org.teenguard.child.dbdatatype.DbMediaEvent;
import org.teenguard.child.service.FlushService;
import org.teenguard.child.utils.Constant;
import org.teenguard.child.utils.ImageUtils;
import org.teenguard.child.utils.MyLog;
import org.teenguard.child.utils.ServerApiUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static org.teenguard.child.dbdao.GenericDbDAO.db;
import static org.teenguard.child.dbdatatype.DbMediaEvent.DEBUG_MEDIA_EVENT_SENT_METADATA_ONLY;
import static org.teenguard.child.utils.TypeConverter.fileToByteAR;

/**
 * Created by chris on 09/10/16.
 */

//http://stackoverflow.com/questions/22012274/contentobserver-onchange-method-gets-called-many-times
public class MediaStoreObserver extends ContentObserver {
    DbMediaDAO  dbMediaDAO = new DbMediaDAO();
    DbMediaEventDAO dbMediaEventDAO = new DbMediaEventDAO();
    ConcurrentHashMap<Integer,DeviceMedia> deviceMediaHM = new ConcurrentHashMap();
    ConcurrentHashMap<Integer,DbMedia> dbMediaHM = new ConcurrentHashMap();

    public MediaStoreObserver(Handler handler) {
        super(handler);
        deviceMediaHM = DeviceMediaDAO.getDeviceMediaHM();
        dbMediaHM = dbMediaDAO.getDbMediaHM();
        if (dbMediaHM.size() == 0) {
            MyLog.i(this,"dbHM =0 --> constructor  empty DB: populate DB with user media list:BULK INSERT!");
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

        if(/*(dbMediaHM.size() >0) && */(deviceMediaHM.size() > dbMediaHM.size())) {
            MyLog.i(this,"deviceHM > dbHM : media added");
            manageMediaAdded();
        }


        if((dbMediaHM.size() >0) && (deviceMediaHM.size() < dbMediaHM.size())) {
            MyLog.i(this," userHM < dbHM : media deleted");
            manageMediaDeleted();
        }

        if(dbMediaHM.size() == 0) {
            MyLog.i(this,"dbHM =0 -->  empty DB: populate DB with user media list");
            //insertDeviceMediaHMIntoDB();
            dbMediaDAO.bulkInsert(deviceMediaHM);
            MyLog.i(this,"populated DB with user media list");
            dbMediaHM = dbMediaDAO.getDbMediaHM();//ripopolo dbMediaHM con i dati appena inseriti nel db
        }

        System.out.println("MediaStoreObserver.onChange calling flushing");
        FlushService.flushMediaEventTable();
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
                DbMediaEvent dbMediaEvent = new DbMediaEvent(0, deviceMedia.getPhoneId(), DbMediaEvent.MEDIA_EVENT_ADD, deviceMedia.getMetadataJsonSTR(), deviceMedia.getPath(), "not-compressed");
                MyLog.i(this, "inserting into media_event json " + dbMediaEvent.getSerializedData());
                long mediaEventId = dbMediaEventDAO.upsert(dbMediaEvent);
                dbMediaEvent.setId(mediaEventId);
                MyLog.i(this, "inserted into media_event._id  " + dbMediaEvent.getId());
                MyLog.i(this, "committing transaction");
                dbMediaDAO.setTransactionSuccessful();    //>>>>>>>>>>>>>>>>COMMIT TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this, "ending transaction");
                dbMediaDAO.endTransaction();              //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                MyLog.i(this, "SENDING NEW USER MEDIA(METADATA) TO SERVER");
                MyServerResponse myServerMetaResponse = ServerApiUtils.addMediaMetadataToServer("[" + dbMediaEvent.getSerializedData() + "]");
                myServerMetaResponse.dump();
                if (myServerMetaResponse.getResponseCode() > 199 && myServerMetaResponse.getResponseCode() < 300) {
                    MyLog.i(this, "SENT NEW USER MEDIA(METADATA) TO SERVER");
                    dbMediaEvent.setEventType(DEBUG_MEDIA_EVENT_SENT_METADATA_ONLY);
                    dbMediaEventDAO.upsert(dbMediaEvent);
                }
                //now try to send media wich has to be compressed before
                sendMetadataAndMedia(dbMediaEvent);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("finally");
                if (db.inTransaction()) {
                    dbMediaDAO.endTransaction(); //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                    System.out.println("closed transaction");
                }
            }
        }
    }

    /**
     * resize,compress and send meta data and media to server: used when
     * 1-new media has been added
     * 2-flushing add event (uncompressed)
     * @param dbMediaEvent
     * @throws JSONException
     */
    public static void sendMetadataAndMedia(DbMediaEvent dbMediaEvent)  {
        DbMediaEventDAO dbMediaEventDAO = new DbMediaEventDAO();
        System.out.println("sendMetadataAndMedia getEventTypeSTR = " + dbMediaEvent.getEventTypeSTR());
        File resizedImageFile = null;
        if(dbMediaEvent.getEventType() == DbMediaEvent.MEDIA_EVENT_ADD || dbMediaEvent.getEventType() == DEBUG_MEDIA_EVENT_SENT_METADATA_ONLY) {
            /////////resizeCompressAndSetMediaEventPath//////////////
            Bitmap bitmap = ImageUtils.getBitmapFromDataPath(dbMediaEvent.getPath());
            System.out.println("original bitmap.getByteCount() = " + bitmap.getByteCount());
            bitmap = ImageUtils.myScaleBitmap(bitmap, Constant.IMAGE_MAX_SIZE);
            System.out.println("scaled bitmap.getByteCount() = " + bitmap.getByteCount());
            resizedImageFile = ImageUtils.storeCompressedImage(bitmap);
            System.out.println(" imageFile.getAbsolutePath() = " + resizedImageFile.getAbsolutePath() + " length " + resizedImageFile.length());
            //scrivi path di compressione
            dbMediaEvent.setEventType(DbMediaEvent.MEDIA_EVENT_COMPRESSED);
            //aggiorna flag mediaEvent
            dbMediaEvent.setCompressedMediaPath(resizedImageFile.getAbsolutePath());
            dbMediaEventDAO.upsert(dbMediaEvent);
            System.out.println("compressed and scaled bitmap.getByteCount() = " + bitmap.getByteCount());
            ////////////////////////////////////////////////////////
             //resizedImageFile = resizeCompressAndSetMediaEventPath(dbMediaEvent);
        }
        if(dbMediaEvent.getEventType() == DbMediaEvent.MEDIA_EVENT_COMPRESSED) {
            resizedImageFile = new File(dbMediaEvent.getCompressedMediaPath());
        }
        //invio header e bitmap
        JSONObject jsonRequestHeader = null; //prima prendevo header direttamente da deviceMedia, ora lo leggo dal db: da testare
        try {
            System.out.println("MediaStoreObserver.sendMetadataAndMedia building header with serializedData"+ dbMediaEvent.getSerializedData());
            jsonRequestHeader = new JSONObject(dbMediaEvent.getSerializedData());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte [] byteAR = fileToByteAR(resizedImageFile.getAbsolutePath());
        MyServerResponse myServerDataResponse = ServerApiUtils.addMediaMetadataAndMediaDataToServer(jsonRequestHeader, byteAR);
        myServerDataResponse.dump();
        //se response ok cancella da mediaEvent
        System.out.println("SENDING NEW USER MEDIA(METADATA + MEDIA) TO SERVER");
        if (myServerDataResponse.getResponseCode() > 199 && myServerDataResponse.getResponseCode() < 300) {
            System.out.println("SENT NEW USER MEDIA(METADATA + MEDIA) TO SERVER");
            dbMediaEvent.setEventType(DbMediaEvent.DEBUG_MEDIA_EVENT_SENT_METADATA_AND_MEDIA_TO_DELETE);
            dbMediaEventDAO.upsert(dbMediaEvent);
            dbMediaEvent.deleteMe();
            File compressedFile = new File(dbMediaEvent.getCompressedMediaPath());
            if (compressedFile != null && compressedFile.exists()) {
                System.out.println("deleting compressedFile = " + compressedFile.getAbsolutePath());
            } else {
                System.out.println("compressedFile don't exists");
            }
        }
    }





//                     /storage/emulated/0/Download.nebula.jpg

    /*//*
    scenario: ho nel db una lista di eventi add:
    la invio in bulk. in che stato vanno? sono eventi spediti ma non compressi:la compressione potrebbe fallire o potrebbe essere fatta in seguito.
    se li lascio in uno stato add al flush successivo li spedisco di nuovo
    mi serve uno stato sent_metadata only?
     */
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
        MyLog.i(this,"manageMediaDeleted removedDbMediaAL.size= " + removedDbMediaAL.size());
        for (DbMedia dbMedia:removedDbMediaAL) {
            //dbMedia.dump();
            /*DeviceMedia deviceMedia = deviceMediaHM.get(dbMedia.getPhoneId());
            System.out.println("MediaStoreObserver.manageMediaDeleted 2");
            if(deviceMedia == null) {
                System.out.println("deviceMedia is null");
                //throw new NullPointerException("deviceMedia is null");
            }*/
            if (db.inTransaction()) {
                System.out.println("manageMediaDeleted in transaction");
            }
            System.out.println("manageMediaDeleted starting transaction");
            dbMediaDAO.beginTransaction();
            System.out.println("manageMediaDeleted started transaction");
            try {
                MyLog.i(this, "removing from media _id: " + dbMedia.getId());
                dbMediaDAO.removeMedia(dbMedia);
                DbMediaEvent dbMediaEvent = new DbMediaEvent(0, dbMedia.getPhoneId(), DbMediaEvent.MEDIA_EVENT_DELETE, dbMedia.getJson().getJSonString(),"","unknown");
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
                    MyLog.i(this," REMOVED MEDIA FROM SERVER");
                    dbMediaEvent.deleteMe();
                }
                /*flushMediaEventTable();*/
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("finally");
                if (db.inTransaction()) {
                    dbMediaDAO.endTransaction(); //>>>>>>>>>>>>>>>>END TRANSACTION>>>>>>>>>>>>>>>>>>
                    System.out.println(" closed transaction");
                }
               /* dbMediaDAO.close();
                dbMediaEventDAO.close();
                System.out.println("closed DAO");*/
            }
        }
        System.out.println("MediaStoreObserver.manageMediaDeleted completed");
    }

    /**DEPRECATED
     * resize bitmap and update MediaEvent status to compressed
     //* @param dbMediaEvent
     * @return compressed bitmap handler
     */
   /* private Bitmap resizeCompressAndSetMediaEvent(DbMediaEvent dbMediaEvent) {
        Bitmap bitmap = ImageUtils.getBitmapFromDataPath(dbMediaEvent.getPath());
        System.out.println("original bitmap.getByteCount() = " + bitmap.getByteCount());
        bitmap = ImageUtils.myScaleBitmap(bitmap, Constant.IMAGE_MAX_SIZE);
        System.out.println("scaled bitmap.getByteCount() = " + bitmap.getByteCount());
        File imageFile = ImageUtils.storeCompressedImage(bitmap);
        bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath()); // TODO: 22/11/16 a bit inefficient store bitmap and the extract
        System.out.println(" imageFile.getAbsolutePath() = " + imageFile.getAbsolutePath() + " length " + imageFile.length());
        //scrivi path di compressione
        dbMediaEvent.setEventType(DbMediaEvent.MEDIA_EVENT_COMPRESSED);
        //aggiorna flag mediaEvent
        dbMediaEvent.setCompressedMediaPath(imageFile.getAbsolutePath());
        dbMediaEventDAO.upsert(dbMediaEvent);
        System.out.println("compressed and scaled bitmap.getByteCount() = " + bitmap.getByteCount());
        return bitmap;
    }*/


}
