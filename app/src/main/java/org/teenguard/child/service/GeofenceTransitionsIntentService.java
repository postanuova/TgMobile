package org.teenguard.child.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.teenguard.child.R;
import org.teenguard.child.activity.MainActivity;

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

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            System.out.println("ENTERED");
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            System.out.println("EXITED");
        } else {
            System.out.println("ERROR");
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
}