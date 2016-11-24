package org.teenguard.child.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.teenguard.child.R;
import org.teenguard.child.activity.MainActivity;
import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdao.DbGeofenceEventDAO;
import org.teenguard.child.dbdatatype.DbGeofenceEvent;
import org.teenguard.child.utils.CalendarUtils;
import org.teenguard.child.utils.MyLog;
import org.teenguard.child.utils.ServerApiUtils;

import java.util.List;

/**
 * Created by chris on 04/11/16.
 * registrarlo in manifest.xml <service android:name=".service.GeofenceTransitionsIntentService"/>
 */

public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "GeofenceTransitions";

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
        System.out.println("GeofenceTransitionsIntentService constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("GeofenceTransitionsIntentService.onHandleIntent");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Goefencing Error " + geofencingEvent.getErrorCode());
            return;
        }
        List<Geofence> triggeringGeofencesAL = geofencingEvent.getTriggeringGeofences();
        for (Geofence triggeringGeofence: triggeringGeofencesAL) {
            System.out.println("geofence.getRequestId() = " + triggeringGeofence.getRequestId());
            System.out.println("geofence.toString() = " + triggeringGeofence.toString());
            //building dbGeofenceEvent

            DbGeofenceEvent dbGeofenceEvent = new DbGeofenceEvent(0,triggeringGeofence.getRequestId(), CalendarUtils.nowUTCMillis(),DbGeofenceEvent.DB_GEOFENCE_EVENT_ENTER);

            int geofenceTransition = geofencingEvent.getGeofenceTransition();
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) dbGeofenceEvent.setEvent(DbGeofenceEvent.DB_GEOFENCE_EVENT_LEAVE);
            long id = dbGeofenceEvent.writeMe();
            dbGeofenceEvent.setId(id);
            dbGeofenceEvent.dump();
            //sending dbGeofenceEvent
            AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + dbGeofenceEvent.getSerializedData() + "]",String.valueOf(dbGeofenceEvent.getId()));
            asyncSendToServer.doInBackground();
            System.out.println("REMEBER TO RE-ENABLE DB DELETION");
            // TODO: 11/11/16 flush dbGeofence event

        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            System.out.println("<<<<< GEOFENCE EVENT: ENTERED >>>>>");
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            System.out.println("<<<<< GEOFENCE EVENT: EXITED  >>>>>");
        } else {
            System.out.println("<<<<< GEOFENCE EVENT: ERROR");
        }
    }

    public void showNotification(String text, String bigText) {

        // 1. Create a NotificationManager
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // 2. Create a PendingIntent for AllGeofencesActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 3. Create and send a notification
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(text)
                .setContentText(text)
                .setContentIntent(pendingNotificationIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, notification);
    }

    //////////////////////////////////
    public class AsyncSendToServer extends AsyncTask<String, String, String> {
        //http://www.journaldev.com/9708/android-asynctask-example-tutorial
        String dataToSend;
        String idToDeleteListSTR;

        public  AsyncSendToServer(String dataToSend, String idToDeleteListSTR) {
            this.dataToSend = dataToSend;
            this.idToDeleteListSTR = idToDeleteListSTR;
        }
        @Override
        protected String doInBackground(String... params) {
            ///////////////NEEDS TO BE EXECUTED IN BACKGROUND/////////////////////
            MyLog.i(this, "ASYNC SENDING GEOFENCE EVENT TO SERVER");
            MyServerResponse myServerResponse = ServerApiUtils.addGeofenceEventToServer(dataToSend);
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                MyLog.i(this, "SENT NEW GEOFENCE EVENT TO SERVER, DELETING  "  + idToDeleteListSTR);
                DbGeofenceEventDAO dbGeofenceEventDAO = new DbGeofenceEventDAO();
                System.out.println("enable delete ?");
                //dbGeofenceEventDAO.delete(idToDeleteListSTR);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("completed async execution");
        }
    }
    ///////////////////////////////

}