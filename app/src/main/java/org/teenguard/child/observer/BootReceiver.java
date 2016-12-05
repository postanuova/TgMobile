package org.teenguard.child.observer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.teenguard.child.service.ChildMonitoringService;
import org.teenguard.child.utils.MyApp;

//BootReceiver rileva l'evento di boot/install e richiama ChildMonitoringService
public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(this.getClass().getName(),"started BootReceiver intent.getAction() " + intent.getAction());
        String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) ||
                action.equalsIgnoreCase(Intent.ACTION_PACKAGE_REPLACED) ||
                action.equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED) ||
                action.equalsIgnoreCase(Intent.ACTION_PACKAGE_RESTARTED)) {
            Log.i(this.getClass().getName()," STARTING  deviceMonitoringServiceIntent");

            Intent deviceMonitoringServiceIntent = new Intent(context, ChildMonitoringService.class);
            context.startService(deviceMonitoringServiceIntent);
            MyApp.dumpSharedPreferences();

           /* Intent childMainActivityIntent= new Intent(context,ChildMainActivity.class);
            childMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(childMainActivityIntent);*/
        }
    }


}
