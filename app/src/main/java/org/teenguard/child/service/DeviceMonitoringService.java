package org.teenguard.child.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.teenguard.child.datatype.DeviceContact;
import org.teenguard.child.datatype.DeviceMedia;
import org.teenguard.child.observer.ContactListObserver;
import org.teenguard.child.observer.LocationObserver;
import org.teenguard.child.observer.MediaStoreObserver;
import org.teenguard.child.observer.VisitObserver;

public class DeviceMonitoringService extends Service {
    ContactListObserver contactListObserver = new ContactListObserver(null);
    MediaStoreObserver mediaStoreObserver = new MediaStoreObserver(null);
    LocationObserver gpsObserver;
    VisitObserver visitObserver;

    public DeviceMonitoringService() {
        Log.i("DeviceMonitoringService", "invoked constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("DeviceMonitoringService", "invoked onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(this.getClass().getName(), "invoked onCreate");
        //monitoring contact list changes
        startMonitoringContactsChanges();
        //monitoring media changes
        startMonitoringMediaStoreChanges();
        //location tracking
        //startLocationTracking();
        //visit tracking
        startVisitTracking();

    }

    private void startMonitoringMediaStoreChanges() {
        Log.i(this.getClass().getName(), "startMonitoringMediaStoreChanges");
        getContentResolver().registerContentObserver(DeviceMedia.PHOTO_EXTERNAL_CONTENT_URI,
                false,/*no propagation to descendant*/
                mediaStoreObserver);
    }

    private void startLocationTracking() {
         gpsObserver = new LocationObserver();
    }

    private void startVisitTracking() {
        visitObserver = new VisitObserver();
    }

    private void startMonitoringContactsChanges() {
        Log.i(this.getClass().getName(), "startMonitoringContactsChanges");
        getContentResolver().registerContentObserver(DeviceContact.CONTACTS_URI,false, contactListObserver);
    }

    public void onDestroy() {
        Log.i(this.getClass().getName(), "invoked onDestroy");
        Log.i(this.getClass().getName(), ">>>>>>>>>>>>>>>>unregistering  observers");
        getContentResolver().unregisterContentObserver(contactListObserver);
        getContentResolver().unregisterContentObserver(mediaStoreObserver);
/*
        getContentResolver().unregisterContentObserver(gpsObserver);
*/

    }
}
