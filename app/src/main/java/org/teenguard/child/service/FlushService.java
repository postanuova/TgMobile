package org.teenguard.child.service;

import org.teenguard.child.observer.ContactListObserver;
import org.teenguard.child.observer.DeviceLocationListener;
import org.teenguard.child.observer.GeofencesObserver;
import org.teenguard.child.observer.MediaStoreObserver;
import org.teenguard.child.observer.VisitObserver;
import org.teenguard.child.utils.MyConnectionUtils;

import java.util.Timer;
import java.util.TimerTask;

import static org.teenguard.child.observer.GeofencesObserver.checkInterval;


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
                    System.out.println("FLUSH SERVICE:  checkInterval " + checkInterval);
                    /////////////////////////////////////////////
                    ContactListObserver.flushContactEventTable();
                    GeofencesObserver.flushGeofenceEventTable();
                    MediaStoreObserver.flushMediaEventTable();
                    VisitObserver.flushVisitTable();
                    DeviceLocationListener.flushLocationTable();
                } else {
                    System.out.println("GeofenceObserver.SendBeatToServerThread: DEVICE IS IN AIRPLANE MODE");
                }
                flushTimer.cancel();
                checkInterval ++;
                startTimedFlush();
            }
        }, checkInterval,1000);
    }
}
