package org.teenguard.child.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.teenguard.child.datatype.DeviceContact;
import org.teenguard.child.datatype.DeviceMedia;
import org.teenguard.child.observer.ContactListObserver;
import org.teenguard.child.observer.GpsObserver;
import org.teenguard.child.observer.MediaStoreObserver;

public class DeviceMonitoringService extends Service {
    ContactListObserver contactListObserver = new ContactListObserver(null);
    MediaStoreObserver mediaStoreObserver = new MediaStoreObserver(null);
    GpsObserver gpsObserver = new GpsObserver(null);

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
        //monitoring gps position
        startMonitoringGPSChanges();

    }

    private void startMonitoringMediaStoreChanges() {
        Log.i(this.getClass().getName(), "startMonitoringMediaStoreChanges");
        getContentResolver().registerContentObserver(DeviceMedia.PHOTO_EXTERNAL_CONTENT_URI,
                false,/*no propagation to descendant*/
                mediaStoreObserver);
    }

    private void startMonitoringGPSChanges() {
        Log.i(this.getClass().getName(), "startMonitoringGPSChanges: not implemented");
    }

    private void startMonitoringContactsChanges() {
        Log.i(this.getClass().getName(), "startMonitoringContactsChanges");
        getContentResolver().registerContentObserver(DeviceContact.CONTACTS_URI,false, contactListObserver);
    }

    public void onDestroy() {
        Log.i(this.getClass().getName(), "invoked onDestroy");
        Log.i(this.getClass().getName(), ">>>>>>>>>>>>>>>>>unregistering observers observers");
        getContentResolver().unregisterContentObserver(contactListObserver);
        getContentResolver().unregisterContentObserver(mediaStoreObserver);
        getContentResolver().unregisterContentObserver(gpsObserver);

    }
}
