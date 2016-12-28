package org.teenguard.child.observer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.teenguard.child.activity.ProperlySettedActivity;
import org.teenguard.child.activity.WelcomeActivity;
import org.teenguard.child.utils.MyApp;
import org.teenguard.parent.activity.WebFrameActivity;

import static org.teenguard.child.utils.MyApp.getSharedPreferences;

//BootReceiver rileva l'evento di boot/install e richiama ChildMonitoringService
public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(this.getClass().getName(),"started BootReceiver intent.getAction() " + intent.getAction());
        String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) ||
                action.equalsIgnoreCase(Intent.ACTION_PACKAGE_REPLACED) /*||
                action.equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED) ||
                action.equalsIgnoreCase(Intent.ACTION_PACKAGE_RESTARTED)*/) {
            MyApp.dumpSharedPreferences();
            boolean isChild = MyApp.getSharedPreferences().getBoolean("IS-CHILD",false);
            boolean isChildConfigured = getSharedPreferences().getBoolean("IS-CHILD-CONFIGURED",false);
            boolean isParent = getSharedPreferences().getBoolean("IS-PARENT",false);
            boolean isParentConfigured = getSharedPreferences().getBoolean("IS-PARENT-CONFIGURED",false);
          /*  /////////////////////////////////////////////
            System.out.println("BootReceiver.onReceive skipping");
            Intent skipIntent = new Intent(MyApp.getInstance().getApplicationContext(), InsertSmsCodeActivity.class);
            skipIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApp.getInstance().getApplicationContext().startActivity(skipIntent);
            ////////////////////////////////////////////////*/
            Class nextActivityClass = null;
            if((!isChild && !isParent)||
                    ((isChild&& !isChildConfigured)) ||
                    (isParent&& !isParentConfigured)) {
                System.out.println("BootReceiver.onReceive: setting default shared preference");
                MyApp.resetSharedPreferences();
                MyApp.dumpSharedPreferences();
                System.out.println(" BootReceiver.onReceive starting WelcomeActivity");
                Intent welcomeIntent = new Intent(MyApp.getInstance().getApplicationContext(), WelcomeActivity.class);
                welcomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApp.getInstance().getApplicationContext().startActivity(welcomeIntent);
            }

            if  ((isChild&& isChildConfigured)) {
                Intent properlySettedIntent = new Intent(MyApp.getInstance().getApplicationContext(), ProperlySettedActivity.class);
                properlySettedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApp.getInstance().getApplicationContext().startActivity(properlySettedIntent);
                System.out.println("BootReceiver.onReceive starting ProperlySettedActivity");
            }

            if((isParent&& isParentConfigured)) {
                Intent webFrameIntent = new Intent(MyApp.getInstance().getApplicationContext(), WebFrameActivity.class);
                webFrameIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApp.getInstance().getApplicationContext().startActivity(webFrameIntent);
                System.out.println("BootReceiver.onReceive starting WebFrameActivity");
            }



           /* Intent childMainActivityIntent= new Intent(context,MainActivity.class);
            childMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(childMainActivityIntent);*/
        }
    }


}
/*
in boot receiver
se è child e non configurato avvio la configurazione: quando arrivo in properly  avvio il monitoraggio
    se è child ed è configurato lo mando in properly da dove  avvio il monitoraggio

    se è parent non configurato chiedere ad ale
    se è parent configurato invia a web frame activity*/
