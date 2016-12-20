package org.teenguard.child.service;

import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdao.DbContactEventDAO;
import org.teenguard.child.dbdao.DbGeofenceEventDAO;
import org.teenguard.child.dbdao.DbLocationEventDAO;
import org.teenguard.child.dbdao.DbMediaEventDAO;
import org.teenguard.child.dbdao.DbVisitEventDAO;
import org.teenguard.child.dbdatatype.DbContactEvent;
import org.teenguard.child.dbdatatype.DbGeofenceEvent;
import org.teenguard.child.dbdatatype.DbLocationEvent;
import org.teenguard.child.dbdatatype.DbMediaEvent;
import org.teenguard.child.dbdatatype.DbVisitEvent;
import org.teenguard.child.observer.MediaStoreObserver;
import org.teenguard.child.utils.CalendarUtils;
import org.teenguard.child.utils.MyConnectionUtils;
import org.teenguard.child.utils.ServerApiUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static org.teenguard.child.dbdatatype.DbMediaEvent.DEBUG_MEDIA_EVENT_SENT_METADATA_ONLY;
import static org.teenguard.child.observer.GeofencesObserver.checkInterval;
import static org.teenguard.child.observer.MediaStoreObserver.sendMetadataAndMedia;


/**
 * Created by chris on 02/12/16.
 */

public class FlushService {

    public static void startTimedFlush() {
        final Timer flushTimer = new Timer();
        flushTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!MyConnectionUtils.isAirplaneModeOn()) {
                    System.out.println("FLUSH SERVICE:  checkInterval " + checkInterval  + "seconds");
                    /////////////////////////////////////////////
                    flushContactEventTable();
                    flushGeofenceEventTable();
                    flushMediaEventTable();
                    flushVisitTable();
                    flushLocationTable();
                } else {
                    System.out.println("GeofenceObserver.SendBeatToServerThread: DEVICE IS IN AIRPLANE MODE");
                }
                flushTimer.cancel();
                checkInterval ++;
                startTimedFlush();
            }
        }, checkInterval*1000,1000);
    }

    /**
     * transmit events to server and cleanup events table
     */
    public static void flushContactEventTable() {
        System.out.println("FLUSHING CONTACT event table " + CalendarUtils.currentDatetimeUTC());
        DbContactEventDAO dbContactEventDAO = new DbContactEventDAO();
        ArrayList<DbContactEvent> dbContactEventAL = dbContactEventDAO.getList();
        if(dbContactEventAL.size() == 0 ) {
            System.out.println("no CONTACTS events to flushr " + CalendarUtils.currentDatetimeUTC());
            return;
        }
        StringBuilder addEventSB = new StringBuilder();
        StringBuilder updateEventSB = new StringBuilder();
        StringBuilder deleteEventSB = new StringBuilder();
        String addEventIdToRemoveList = "";         //lista degli eventi ADD da rimuovere dal db dopo l'ok del server
        String updateEventIdToRemoveList = "";      //lista degli eventi UPDATE da rimuovere dal db dopo l'ok del server
        String deleteEventIdToRemoveList = "";      //lista degli eventi DELETE da rimuovere dal db dopo l'ok del server

        for (DbContactEvent dbContactEvent : dbContactEventAL) {
            //dbContactEvent.dump();
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

    public static void flushGeofenceEventTable() {
        // TODO: 25/11/16 to be used and tested
        System.out.println("FLUSHING GEOFENCE EVENT TABLE " + new Date(CalendarUtils.nowUTCMillis()).toString());
        DbGeofenceEventDAO dbGeofenceEventDAO = new DbGeofenceEventDAO();
        ArrayList<DbGeofenceEvent> dbGeofenceEventAL = dbGeofenceEventDAO.getList();
        System.out.println(" FLUSHING dbGeofenceEventAL.size() = " + dbGeofenceEventAL.size());
        if(dbGeofenceEventAL.size() > 0) {
            StringBuilder bulkGeofenceEventSB = new StringBuilder();//contiene gli eventi geofence da inviare
            StringBuilder idToDeleteListSB = new StringBuilder(); //la usero' per cancellare gli eventi una volta inviati
            for (DbGeofenceEvent dbGeofenceEvent : dbGeofenceEventAL) {
                System.out.println("dbGeofenceEvent id= " + dbGeofenceEvent.getGeofenceId() + " event" + dbGeofenceEvent.getEvent());
                bulkGeofenceEventSB.append(dbGeofenceEvent.getSerializedData());
                bulkGeofenceEventSB.append(",");
                idToDeleteListSB.append(dbGeofenceEvent.getId());
                idToDeleteListSB.append(",");
            }
            String bulkGeofenceEventSTR = bulkGeofenceEventSB.toString();
            if (bulkGeofenceEventSTR.endsWith(","))
                bulkGeofenceEventSTR = bulkGeofenceEventSTR.substring(0, bulkGeofenceEventSTR.length() - 1);
            String idToDeleteListSTR = idToDeleteListSB.toString();
            if (idToDeleteListSTR.endsWith(","))
                idToDeleteListSTR = idToDeleteListSTR.substring(0, idToDeleteListSTR.length() - 1);
            GeofenceTransitionsIntentService.AsyncSendToServer asyncSendToServer = new GeofenceTransitionsIntentService().new AsyncSendToServer("[" + bulkGeofenceEventSTR + "]", idToDeleteListSTR);
            asyncSendToServer.execute();
        } else {
            System.out.println(" no GEOFENCE events to flush " + CalendarUtils.currentDatetimeUTC());
        }
    }

    /**
     * transmit events to server and cleanup events table
     */
    public static void flushMediaEventTable() {
        System.out.println("FLUSHING MEDIA event table");
        DbMediaEventDAO dbMediaEventDAO = new DbMediaEventDAO();
        ArrayList<DbMediaEvent> dbMediaEventAL = dbMediaEventDAO.getList();
        if(dbMediaEventAL.size() == 0 ) {
            System.out.println(" no MEDIA events to flush " + CalendarUtils.currentDatetimeUTC());
            return;
        }
        StringBuilder addEventSB = new StringBuilder();
        StringBuilder deleteEventSB = new StringBuilder();
        StringBuilder compressedEventSB = new StringBuilder();
        String addEventIdList = "";         //lista degli eventi ADD da rimuovere dal db dopo l'ok del server
        String deleteEventIdToRemoveList = "";      //lista degli eventi DELETE da rimuovere dal db dopo l'ok del server
        String compressedEventIdList = "";
        for (DbMediaEvent dbMediaEvent : dbMediaEventAL) {
            //dbMediaEvent.dump();
            switch (dbMediaEvent.getEventType()) {
                case DbMediaEvent.MEDIA_EVENT_ADD:
                case DEBUG_MEDIA_EVENT_SENT_METADATA_ONLY:{
                    addEventSB.append(dbMediaEvent.getSerializedData() + ",");
                    addEventIdList += dbMediaEvent.getId() + ",";
                    break;
                }
                case DbMediaEvent.MEDIA_EVENT_DELETE: {
                    deleteEventSB.append("\"" + dbMediaEvent.getCsId() + "\"" + ",");
                    deleteEventIdToRemoveList += dbMediaEvent.getId() + ",";
                    break;
                }
                case DbMediaEvent.MEDIA_EVENT_COMPRESSED: {
                    compressedEventSB.append("\"" + dbMediaEvent.getSerializedData() + "\"" + ",");//sostituito getCsId con getSerializedData
                    compressedEventIdList += dbMediaEvent.getId() + ",";
                    break;
                }
            }
        }//fine for
        System.out.println("addEventIdList = " + addEventIdList);
        System.out.println("deleteEventIdToRemoveList = " + deleteEventIdToRemoveList);
        System.out.println("compressedEventIdList = " + compressedEventIdList);
        /////add
        String addDataBulkSTR = addEventSB.toString();
        if (addDataBulkSTR.length() > 0) {//ci sono eventi add
            if (addDataBulkSTR.endsWith(",")) {
                addDataBulkSTR = addDataBulkSTR.substring(0, addDataBulkSTR.length() - 1);
            }
            if (addEventIdList.endsWith(",")) {
                addEventIdList = addEventIdList.substring(0, addEventIdList.length() - 1);
            }
            System.out.println("addDataBulkSTR = " + addDataBulkSTR);
            //send metadata to server
            MyServerResponse myServerResponse = ServerApiUtils.addMediaMetadataToServer("[" + addDataBulkSTR + "]");
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {//succesfully sent metadata
                System.out.println(" ADD BULK MEDIA METADATA SENT SUCCESFULLY TO SERVER: DELETING FROM DB");
                ArrayList<DbMediaEvent> dbMediaEventAddAL = dbMediaEventDAO.getList("WHERE _id IN(" + addEventIdList + ")");
                for (DbMediaEvent dbMediaEvent : dbMediaEventAddAL) {
                    dbMediaEvent.setEventType(DEBUG_MEDIA_EVENT_SENT_METADATA_ONLY);
                    dbMediaEventDAO.upsert(dbMediaEvent);
                }
                //now try to compress and send individually
                for (DbMediaEvent dbMediaEvent : dbMediaEventAddAL) {
                    sendMetadataAndMedia(dbMediaEvent);
                }
            }
        }

        /////compress
        String compressedDataBulkSTR = compressedEventSB.toString();
        if (compressedDataBulkSTR.length() > 0) {//ci sono eventi compressed, da spedire uno per uno
            if (compressedDataBulkSTR.endsWith(",")) {
                compressedDataBulkSTR = compressedDataBulkSTR.substring(0, compressedDataBulkSTR.length() - 1);
            }
            if (compressedEventIdList.endsWith(",")) {
                compressedEventIdList = compressedEventIdList.substring(0, compressedEventIdList.length() - 1);
            }
            System.out.println("compressedDataBulkSTR = " + compressedDataBulkSTR);
            compressedDataBulkSTR = "[" + compressedDataBulkSTR + "]";
            MyServerResponse myServerResponse = ServerApiUtils.addMediaMetadataToServer(compressedDataBulkSTR);
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                System.out.println("ADD BULK MEDIA METADATA SENT SUCCESFULLY TO SERVER: NOW TRY TO SEND MEDIA");
                ArrayList<DbMediaEvent> dbMediaEventCompressedAL = dbMediaEventDAO.getList("WHERE _id IN(" + compressedEventIdList + ")");
                //now try to send individually
                for (DbMediaEvent dbMediaEvent : dbMediaEventCompressedAL) {
                    MediaStoreObserver.sendMetadataAndMedia(dbMediaEvent);
                }
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
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                System.out.println("DELETE BULK MEDIA SENT SUCCESFULLY TO SERVER: DELETING FROM DB");
                dbMediaEventDAO.delete(deleteEventIdToRemoveList);
                System.out.println("deleted from events list " + deleteEventIdToRemoveList);
            }
        }
    }

    public static  void flushVisitTable() {
        System.out.println(" FLUSHING VISIT EVENT TABLE " + new Date(CalendarUtils.nowUTCMillis()).toString());
        DbVisitEventDAO dbVisitEventDAO = new DbVisitEventDAO();
        ArrayList<DbVisitEvent> dbVisitEventAL = dbVisitEventDAO.getList();
        System.out.println(" FLUSHING dbVisitEventAL.size() = " + dbVisitEventAL.size());
        if(dbVisitEventAL.size() > 0) {
            StringBuilder bulkVisitEventSB = new StringBuilder();
            StringBuilder idToDeleteListSB = new StringBuilder(); //la usero' per cancellare gli eventi una volta inviati
            for (DbVisitEvent dbVisitEvent : dbVisitEventAL) {
                bulkVisitEventSB.append(dbVisitEvent.buildSerializedDataString());
                bulkVisitEventSB.append(",");
                idToDeleteListSB.append(dbVisitEvent.getId());
                idToDeleteListSB.append(",");
            }
            String bulkVisitEventSTR = bulkVisitEventSB.toString();
            if (bulkVisitEventSTR.endsWith(","))
                bulkVisitEventSTR = bulkVisitEventSTR.substring(0, bulkVisitEventSTR.length() - 1);
            String idToDeleteListSTR = idToDeleteListSB.toString();
            if (idToDeleteListSTR.endsWith(","))
                idToDeleteListSTR = idToDeleteListSTR.substring(0, idToDeleteListSTR.length() - 1);
            // TODO: 12/12/16 testare il flushing: NetworkOnMainThreadException
           /* AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + bulkVisitEventSTR + "]", idToDeleteListSTR);
            asyncSendToServer.execute();*/
            ///////////NOT ASYNC VERSION/////////
            MyServerResponse myServerResponse = ServerApiUtils.addVisitToServer("[" + bulkVisitEventSTR + "]");
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                System.out.println("FLUSHING NEW VISIT TO SERVER, DELETING  "  + idToDeleteListSTR);
                dbVisitEventDAO = new DbVisitEventDAO();
                dbVisitEventDAO.delete(idToDeleteListSTR);
            }
            ////////////////////////////////////
        }
    }

    public static void flushLocationTable() {
        DbLocationEventDAO dbLocationEventDAO = new DbLocationEventDAO();
        ArrayList <DbLocationEvent> dbLocationEventAL = dbLocationEventDAO.getList();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder idToDeleteListSB = new StringBuilder(); //la usero' per cancellare gli eventi una volta inviati
        for (DbLocationEvent dbLocationEvent:dbLocationEventAL) {
            stringBuilder.append(dbLocationEvent.buildSerializedDataString());
            stringBuilder.append(",");
            idToDeleteListSB.append(dbLocationEvent.getId());
        }
        String bulkLocationEventSTR = stringBuilder.toString();
        if(bulkLocationEventSTR.endsWith(",")) bulkLocationEventSTR = bulkLocationEventSTR.substring(0,bulkLocationEventSTR.length()-1);
        String idToDeleteListSTR = stringBuilder.toString();
        if(idToDeleteListSTR.endsWith(",")) idToDeleteListSTR = idToDeleteListSTR.substring(0,idToDeleteListSTR.length()-1);
        /*AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + bulkLocationEventSTR + "]",idToDeleteListSTR);
        asyncSendToServer.execute();*/
        ///////not async///////
        if(bulkLocationEventSTR.length() > 0) {
            MyServerResponse myServerResponse = ServerApiUtils.addLocationToServer("[" + bulkLocationEventSTR + "]");
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                System.out.println("FLUSHED NEW LOCATION TO SERVER, DELETING  " + idToDeleteListSTR);
                dbLocationEventDAO.delete(idToDeleteListSTR);
            }
        } else {
            System.out.println("no LOCATION to flush " + CalendarUtils.currentDatetimeUTC());
        }
        ////////////////////////
    }

}
